package com.gaufoo.bbs.util;

import com.gaufoo.sst.SST;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class IndexableSST<K, V> {
    private SST keyValues;

    private Function<String, K> keyShaper;
    private Function<K, String> keySerializer;
    private Function<String, V> valShaper;
    private Function<V, String> valSerializer;

    private Map<ExtractorId, Tuple<ClusterKeyExtractor<K, V>, SST>> clusters = new HashMap<>();
    private Map<ExtractorId, Tuple<CountingKeyExtractor<V>, SST>> counters = new HashMap<>();

    private int clusterFieldLength;

    private AtomicLong counter;

    private IndexableSST(Function<String, K> keyShaper, Function<K, String> keySerializer, Function<String, V> valShaper, Function<V, String> valSerializer,
                         Map<ExtractorId, ClusterKeyExtractor<K, V>> clusters, Map<ExtractorId, CountingKeyExtractor<V>> counters, int clusterFieldLength, Path storingPath) {
        AtomicInteger cluster = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);

        keyValues = SST.of("key-value", storingPath);
        this.keyShaper = keyShaper;
        this.keySerializer = keySerializer;
        this.valShaper = valShaper;
        this.valSerializer = valSerializer;
        clusters.forEach((id, fn) ->
                this.clusters.put(id, Tuple.of(fn, SST.of("cluster" + cluster.incrementAndGet(), storingPath))));
        counters.forEach((id, fn) ->
                this.counters.put(id, Tuple.of(fn, SST.of("counter" + count.incrementAndGet(), storingPath))));
        this.counter = new AtomicLong(getAllValuesAsc().count());
        this.clusterFieldLength = clusterFieldLength;
    }

    public Stream<K> getAllValuesAsc() {
        return SstUtils.waitFuture(keyValues.allKeysAsc())
                .map(stringStream -> stringStream.map(keyShaper))
                .orElse(Stream.empty());
    }

    public Stream<K> getAllValuesDes() {
        return SstUtils.waitFuture(keyValues.allKeysDes())
                .map(stringStream -> stringStream.map(keyShaper))
                .orElse(Stream.empty());
    }

    public Stream<K> getAllValuesAscBy(ExtractorId extractorId, String fieldValue) {
        Tuple<ClusterKeyExtractor<K, V>, SST> fnSstTuple = clusters.get(extractorId);
        if (Objects.isNull(fnSstTuple)) return Stream.empty();

        return SstUtils.waitFuture(fnSstTuple.right.rangeKeysAsc(fieldValue + MIN(), fieldValue + MAX())
                .thenApply(keys -> keys.map(this::retrieveId))).orElse(Stream.empty());
    }

    public Stream<K> getAllValuesDesBy(ExtractorId extractorId, String fieldValue) {
        Tuple<ClusterKeyExtractor<K, V>, SST> fnSstTuple = clusters.get(extractorId);
        if (Objects.isNull(fnSstTuple)) return Stream.empty();

        return SstUtils.waitFuture(fnSstTuple.right.rangeKeysDes(fieldValue + MAX(), fieldValue + MIN())
                .thenApply(keys -> keys.map(this::retrieveId))).orElse(Stream.empty());
    }

    public V getValue(K key) {
        return SstUtils.getEntry(keyValues, keySerializer.apply(key), value -> valShaper.apply(value));
    }

    public boolean saveValue(K key, V value) {
        String keyStr = keySerializer.apply(key);
        String valueStr = valSerializer.apply(value);

        if (SstUtils.contains(keyValues, valueStr)) return false;
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(keyValues, keyStr, valueStr));
        clusters.forEach((extractorId, fnSstTup) -> {
            tasks.add(SstUtils.setEntryAsync(fnSstTup.right, fnSstTup.left.extract(key, value) + keyStr, "GAUFOO"));
        });
        counters.forEach((extractorId, fnSstTup) -> {
            String extractedField = fnSstTup.left.extract(value);

            tasks.add(SstUtils.setEntryAsync(fnSstTup.right, extractedField, String.valueOf(countBy(extractorId, extractedField) + 1L)));
        });

        boolean ok = SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
        if (ok) this.counter.incrementAndGet();
        return ok;
    }

    public boolean deleteValue(K key) {
        String keyStr = keySerializer.apply(key);

        return Optional.ofNullable(SstUtils.removeEntryByKey(keyValues, keyStr))
                .map(valueStr -> {
                    V value = valShaper.apply(valueStr);
                    List<CompletionStage<Boolean>> tasks = new LinkedList<>();
                    clusters.forEach((extractorId, fnSstTup) -> {
                        tasks.add(SstUtils.removeEntryAsync(fnSstTup.right, fnSstTup.left.extract(key, value) + keyStr));
                    });
                    counters.forEach((extractorId, fnSstTup) -> {
                        String extractedField = fnSstTup.left.extract(value);

                        Optional<String> oldCount = retryIfEmpty(3, () -> SstUtils.removeEntryByKey(fnSstTup.right, extractedField));
                        Optional<Boolean> decRes = oldCount.map(cnt -> SstUtils.setEntry(fnSstTup.right, extractedField, String.valueOf(Long.parseLong(cnt) - 1L)));

                        tasks.add(CompletableFuture.completedFuture(decRes.orElse(false)));
                    });
                    boolean ok =  SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
                    if (ok) counter.decrementAndGet();
                    return ok;
                })
                .orElse(false);
    }

    public Long count() {
        return counter.get();
    }

    public Long countBy(ExtractorId extractorId, String fieldValue) {
        return retryIfEmpty(3, () -> SstUtils.getEntry(counters.get(extractorId).right, fieldValue, Long::parseLong))
                .orElse(0L);
    }

    public static class ExtractorId {
        private static Long last = 0L;
        public Long value;

        private ExtractorId(Long value) {
            this.value = value;
        }

        private static ExtractorId next() {
            return new ExtractorId(last++);
        }
    }

    public static class IndexableSSTBuilder<K, V> {
        private Map<ExtractorId, ClusterKeyExtractor<K, V>> clusterExtractors = new HashMap<>();
        private Map<ExtractorId, CountingKeyExtractor<V>> counterExtractors = new HashMap<>();

        private Function<String, K> keyShaper = null;
        private Function<K, String> keySerializer = null;
        private Function<String, V> valShaper = null;
        private Function<V, String> valSerializer = null;

        private int clusterFieldLength = 8;
        private Path storingPath = null;

        public static class ExtractorIdContainer<K, V>  {
            IndexableSSTBuilder<K, V>  thisBuilder;
            ExtractorId id;

            private ExtractorIdContainer(IndexableSSTBuilder<K, V> thisBuilder, ExtractorId id) {
                this.thisBuilder = thisBuilder;
                this.id = id;
            }

            public IndexableSSTBuilder<K, V> takeId(Consumer<ExtractorId> idConsumer) {
                idConsumer.accept(id);
                return this.thisBuilder;
            }
        }

        public ExtractorIdContainer<K, V> withClustering(ClusterKeyExtractor<K, V> fieldExtractor, int clusterFieldLength) {
            ExtractorId extractorId = ExtractorId.next();
            clusterExtractors.put(extractorId, fieldExtractor);
            this.clusterFieldLength = clusterFieldLength;
            return new ExtractorIdContainer<>(this, extractorId);
        }

        public ExtractorIdContainer<K, V> withCounting(CountingKeyExtractor<V> fieldExtractor) {
            ExtractorId extractorId = ExtractorId.next();
            counterExtractors.put(extractorId, fieldExtractor);
            return new ExtractorIdContainer<>(this, extractorId);
        }

        public IndexableSSTBuilder<K, V> keyShaper(Function<String, K> keyShaper) {
            this.keyShaper = keyShaper;
            return this;
        }

        public IndexableSSTBuilder<K, V> valueShaper(Function<String, V> valueShaper) {
            this.valShaper = valueShaper;
            return this;
        }

        public IndexableSSTBuilder<K, V> keySerializer(Function<K, String> keySerializer) {
            this.keySerializer = keySerializer;
            return this;
        }

        public IndexableSSTBuilder<K, V> valueSerializer(Function<V, String> valSerializer) {
            this.valSerializer = valSerializer;
            return this;
        }

        public IndexableSSTBuilder<K, V> storingPath(Path storingPath) {
            this.storingPath = storingPath;
            return this;
        }

        public IndexableSST<K, V> build() {
            if (keyShaper == null || valShaper == null || this.storingPath == null) throw new IllegalArgumentException();
            if (keySerializer == null) keySerializer = Objects::toString;
            if (valSerializer == null) valSerializer = Object::toString;
            return new IndexableSST<>(keyShaper, keySerializer, valShaper, valSerializer, clusterExtractors, counterExtractors, clusterFieldLength, storingPath);
        }
    }

    private K retrieveId(String string) {
        return splitLast(string, clusterFieldLength);
    }

    private K splitLast(String string, int n) {
        return keyShaper.apply(string.substring(string.length() - n));
    }

    private String MIN() {
        return many('0', clusterFieldLength);
    }

    private String MAX() {
        return many('9', clusterFieldLength);
    }

    private static String many(char x, int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            stringBuilder.append(x);
        }
        return stringBuilder.toString();
    }

    private static <T> Optional<T> retryIfEmpty(int times, Supplier<T> fn) {
        for (int i = 0; i < times; i++) {
            T res = fn.get();
            if (res != null) return Optional.of(res);
        }
        return Optional.empty();
    }

    public static <K, V> IndexableSSTBuilder<K, V> builder() {
        return new IndexableSSTBuilder<>();
    }

    public interface CountingKeyExtractor<V> {
        String extract(V value);
    }

    public interface ClusterKeyExtractor<K, V> {
        String extract(K k, V v);
    }
}
