package com.gaufoo.db;

import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.bbs.util.Tuple;
import com.gaufoo.db.common.Index;
import com.gaufoo.sst.SST;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.gaufoo.db.util.Util.formatStrWithLen;

public class TenGoKV<Key, Value> {
    private final SST kv;
    private final SST agg;
    private final List<Index.Aggregate> aggregates;
    private final Map<IndexHandler<Key, Value>, Cluster<Key, Value>> indexMap;
    private final Tuple<Function<Key, String>, Integer> keySerializer;
    private final Function<Value, String> valueSerializer;
    private final Function<String, Key> keyShaper;
    private final Function<String, Value> valueShaper;

    private TenGoKV(SST kv,
                   SST agg, List<Index.Aggregate> aggregates, Map<IndexHandler<Key, Value>, Cluster<Key, Value>> indexMap,
                   Tuple<Function<Key, String>, Integer> keySerializer,
                    Function<Value, String> valueSerializer, Function<String, Key> keyShaper, Function<String, Value> valueShaper) {
        this.kv = kv;
        this.agg = agg;
        this.aggregates = aggregates;
        this.indexMap = indexMap;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.keyShaper = keyShaper;
        this.valueShaper = valueShaper;
    }

    public Value getValue(Key key) {
        String k = formatStrWithLen(keySerializer.left.apply(key), keySerializer.right);
        return SstUtils.getEntry(kv, k, valueShaper);
    }

    public Stream<Key> getAllKeysAsc() {
        return SstUtils.waitFuture(kv.allKeysAsc()).map(ss -> ss.map(keyShaper)).orElse(Stream.empty());
    }

    public Stream<Key> getAllKeysDes() {
        return SstUtils.waitFuture(kv.allKeysDes()).map(ss -> ss.map(keyShaper)).orElse(Stream.empty());
    }

    public Stream<Key> getAllKeysAsc(IndexHandler<Key, Value> handler, GroupChain.EndGroup groupChain) {
        String prefix = groupChain.getPrefix();
        Cluster<Key, Value> cluster = indexMap.get(handler);
        int restLen = cluster.prefixLength + this.keySerializer.right - prefix.length();
        String from = expandMin(prefix, restLen);
        String to = expandMax(prefix, restLen);
        return SstUtils.waitFuture(cluster.cluster.rangeKeysAsc(from, to)).map(ss -> ss.map(
                s -> this.keyShaper.apply(s.substring(cluster.prefixLength))
        )).orElse(Stream.empty());
    }

    public Stream<Key> getAllKeysDes(IndexHandler<Key, Value> handler, GroupChain.EndGroup group) {
        String prefix = group.getPrefix();
        Cluster<Key, Value> cluster = indexMap.get(handler);
        int restLen = cluster.prefixLength + this.keySerializer.right - prefix.length();
        String from = expandMax(prefix, restLen);
        String to = expandMin(prefix, restLen);
        return SstUtils.waitFuture(cluster.cluster.rangeKeysDes(from, to)).map(ss -> ss.map(
                s -> this.keyShaper.apply(s.substring(cluster.prefixLength))
        )).orElse(Stream.empty());
    }

    public Long getCount() {
        if (this.aggregates.contains(Index.Aggregate.Count)) {
            return Optional.ofNullable(SstUtils.getEntry(agg, "Count", Long::parseLong)).orElse(0L);
        } else {
            return getAllKeysAsc().count();
        }
    }

    public Long getCount(IndexHandler<Key, Value> handler, GroupChain.EndGroup group) {
        Cluster<Key, Value> cluster = this.indexMap.get(handler);
        Tuple<Index.Aggregate, Integer> ag = Tuple.of(Index.Aggregate.Count, group.getPrefix().length());
        SST aggSst = cluster.agg.get(ag);
        if (aggSst != null) {
            return Optional.ofNullable(SstUtils.getEntry(aggSst, group.getPrefix(), Long::parseLong)).orElse(0L);
        } else {
            return getAllKeysAsc(handler, group).count();
        }
    }

    public boolean saveValue(Key key, Value value) {
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();

        // 添加信息
        tasks.add(SstUtils.setEntryAsync(kv, keySerializer.left.apply(key), valueSerializer.apply(value)));

        // 更新聚集信息
        for (Index.Aggregate aggregate : this.aggregates) {
            if (aggregate == Index.Aggregate.Count) {
                tasks.add(upOne(agg, "Count"));
            }
        }

        // 更新索引信息
        updateIndexOfSave(key, value, tasks);

        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    private void updateIndexOfSave(Key key, Value value, List<CompletionStage<Boolean>> tasks) {
        for (Cluster<Key, Value> cluster : this.indexMap.values()) {
            String ikey = cluster.consClusterKey.apply(key, value);
            tasks.add(SstUtils.setEntryAsync(cluster.cluster, ikey, "TenGo"));
            for (Map.Entry<Tuple<Index.Aggregate, Integer>, SST> indexAgg : cluster.agg.entrySet()) {
                Tuple<Index.Aggregate, Integer> aggInfo = indexAgg.getKey();
                if (aggInfo.left == Index.Aggregate.Count) {
                    String aggKey = ikey.substring(0, aggInfo.right);
                    tasks.add(upOne(indexAgg.getValue(), aggKey));
                }
            }
        }
    }

    public boolean deleteValue(Key key) {
        Value val = SstUtils.getEntry(kv, keySerializer.left.apply(key), valueShaper);
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();

        // 删键值对信息
        tasks.add(SstUtils.removeEntryAsync(this.kv, keySerializer.left.apply(key)));

        // 删聚集
        for (Index.Aggregate aggregate : this.aggregates) {
            if (aggregate == Index.Aggregate.Count) {
                tasks.add(downOne(agg, "Count"));
            }
        }

        // 更新索引
        updateIndexOfDelete(key, tasks, val);

        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    public boolean deleteAll(IndexHandler<Key, Value> handler, GroupChain.EndGroup group) {
        return getAllKeysDes(handler, group).parallel().map(this::deleteValue)
                .reduce(true, (a, b) -> a && b);
    }

    private void updateIndexOfDelete(Key key, List<CompletionStage<Boolean>> tasks, Value val) {
        if (val != null) {
            for (Cluster<Key, Value> cluster : this.indexMap.values()) {
                String ikey = cluster.consClusterKey.apply(key, val);

                // 更新索引
                CompletionStage<Boolean> t1 = SstUtils.removeEntryAsync(cluster.cluster, ikey);
                tasks.add(t1);

                // 更新索引聚集
                for (Map.Entry<Tuple<Index.Aggregate, Integer>, SST> inAgg : cluster.agg.entrySet()) {
                    Tuple<Index.Aggregate, Integer> aggInfo = inAgg.getKey();
                    if (aggInfo.left == Index.Aggregate.Count) {
                        String aggKey = ikey.substring(0, aggInfo.right);
                        tasks.add(downOne(inAgg.getValue(), aggKey));
                    }
                }
            }
        }
    }

    private CompletionStage<Boolean> downOne(SST sst, String key) {
        return SstUtils.updateEntryAsync(sst, key, (v) -> {
            long old = Long.parseLong(v);
            return String.valueOf(old - 1L);
        }, "0");
    }

    private CompletionStage<Boolean> upOne(SST sst, String key) {
        return SstUtils.updateEntryAsync(sst, key, (v) -> {
            long old = Long.parseLong(v);
            return String.valueOf(old + 1L);
        }, "1");
    }

    public void shutdown() {
        Stream<CompletionStage<?>> shutdowns = Stream.concat(
                Stream.of(kv.shutdown(), agg.shutdown()),
                this.indexMap.values().stream().flatMap(i ->
                        Stream.concat(Stream.of(i.cluster.shutdown()),
                                i.agg.values().stream().map(SST::shutdown)))
        );

        SstUtils.waitAllFuturesPar(shutdowns);
    }

    /**
     * types
     */

    public interface IndexHandler<Key, Value> {
        GroupChain<Key, Value> getGroupChain();
    }

    public static class GroupChain<Key, Value> {
        private final ListIterator<Index.Record<Key, Value>> iterator;
        private final StringBuilder sb = new StringBuilder();

        public GroupChain(ListIterator<Index.Record<Key, Value>> iterator) {
            this.iterator = iterator;
        }

        public interface WithGroup {
            WithGroup group(String cluster);
            EndGroup endGroup();
        }

        public interface EndGroup {
            String getPrefix();
        }

        public WithGroup group(String cluster) {
            Index.Record<Key, Value> next = iterator.next();
            int strLength = next.factor.strLength;
            sb.append(formatStrWithLen(cluster, strLength));
            return new WithGroup() {
                public WithGroup group(String cluster) {
                    return GroupChain.this.group(cluster);
                }
                public EndGroup endGroup() {
                    return GroupChain.this.sb::toString;
                }
            };
        }

        public EndGroup endGroup() {
            return () -> "";
        }
    }

    private static class Cluster<Key, Value> {
        final public List<Index.Record<Key, Value>> records;
        final public BiFunction<Key, Value, String> consClusterKey;
        final public SST cluster;
        final public Map<Tuple<Index.Aggregate, Integer>, SST> agg;
        final public int prefixLength;

        private Cluster(List<Index.Record<Key, Value>> records, BiFunction<Key, Value, String> consClusterKey, SST cluster, Map<Tuple<Index.Aggregate, Integer>, SST> agg, int prefixLength) {
            this.records = records;
            this.consClusterKey = consClusterKey;
            this.cluster = cluster;
            this.agg = agg;
            this.prefixLength = prefixLength;
        }

        public static <Key, Value> Cluster<Key, Value> of(List<Index.Record<Key, Value>> records, Function<Key, String> keySerializer, Path storingPath) {
            int prefixLength = 0;
            SST cluster = SST.of("cluster", storingPath);
            Map<Tuple<Index.Aggregate, Integer>, SST> s = new HashMap<>();
            for (Index.Record<Key, Value> record : records) {
                prefixLength += record.factor.strLength;
                if (record.type == Index.Type.Group) {
                    for (Index.Aggregate statistic : record.agg) {
                        s.put(Tuple.of(statistic, prefixLength), SST.of(statistic.name() + prefixLength, storingPath));
                    }
                }
            }
            BiFunction<Key, Value, String> consClusterKey = (key, value) ->
                    records.stream().map(r -> r.factor.partialOfKV.model(key, value))
                    .reduce("", (l, r) -> l + r) + keySerializer.apply(key);
            return new Cluster<>(records, consClusterKey, cluster, s, prefixLength);
        }
    }

    public static class TenGoKVBuilder<Key, Value> {
        private final Map<IndexHandler<Key, Value>, Cluster<Key, Value>> map = new HashMap<>();
        private int indexCnt = 0;

        public interface KeySerializer<Key, Value> {
            ValueSerializer<Key, Value> keySerializer(Function<Key, String> keySerializer, int strLength);
        }
        public interface ValueSerializer<Key, Value> {
            KeyShaper<Key, Value> valueSerializer(Function<Value, String> valueSerializer);
        }
        public interface KeyShaper<Key, Value> {
            ValueShaper<Key, Value> keyShaper(Function<String, Key> keyShaper);
        }
        public interface ValueShaper<Key, Value> {
            WithAggregate<Key, Value> valueShaper(Function<String, Value> valueShaper);
        }
        public interface WithAggregate<Key, Value> {
            WithIndex<Key, Value> withAggregate(List<Index.Aggregate> aggs);
        }
        public interface WithIndex<Key, Value> {
            GetHandler<Key, Value> withIndex(Index<Key, Value> index);
            TenGoKV<Key, Value> build();
        }

        public KeySerializer<Key, Value> withPath(Path path) {
            return (Function<Key, String> keySerializer, int keyLen) ->
                   (Function<Value, String> valueSerializer) ->
                   (Function<String, Key> keyShaper) ->
                   (Function<String, Value> valueShaper) ->
                   (List<Index.Aggregate> aggs) ->
                       new WithIndex<Key, Value>() {
                           public GetHandler<Key, Value> withIndex(Index<Key, Value> index) {
                               IndexHandler<Key, Value> handler = () ->
                                       new GroupChain<>(index.records.listIterator());
                               TenGoKVBuilder.this.map.put(handler, Cluster.of(index.records, keySerializer,
                                       path.resolve("index" + TenGoKVBuilder.this.indexCnt)));
                               TenGoKVBuilder.this.indexCnt++;
                               return new GetHandler<>(this, handler);
                           }
                           public TenGoKV<Key, Value> build() {
                               return new TenGoKV<>(SST.of("kv", path), SST.of("agg", path), aggs, TenGoKVBuilder.this.map,
                                       Tuple.of(keySerializer, keyLen), valueSerializer, keyShaper, valueShaper);
                           }
                       };
        }

        public static <Key, Value> TenGoKVBuilder<Key, Value> get() {
            return new TenGoKVBuilder<>();
        }

        public static class GetHandler<Key, Value> {
            private WithIndex<Key, Value> thisBuilder;
            private IndexHandler<Key, Value> handler;

            private GetHandler(WithIndex<Key, Value> thisBuilder, IndexHandler<Key, Value> handler) {
                this.thisBuilder = thisBuilder;
                this.handler = handler;
            }

            public WithIndex<Key, Value> takeHandler(Consumer<IndexHandler<Key, Value>> hdConsumer) {
                hdConsumer.accept(handler);
                return this.thisBuilder;
            }
        }
    }

    private static String many(char x, int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            stringBuilder.append(x);
        }
        return stringBuilder.toString();
    }

    private static String expandMin(String prefix, int restLen) {
        return prefix + many(Character.MIN_VALUE, restLen);
    }

    private static String expandMax(String prefix, int restLen) {
        return prefix + many(Character.MAX_VALUE, restLen);
    }

    public static void main(String[] args) {
//        TenGoKV<String, String> aa = TenGoKVBuilder.<String, String>get()
//                .withPath(Paths.get(""))
//                .keySerializer(null, 1)
//                .valueSerializer(null)
//                .keyShaper(null)
//                .valueShaper(null)
//                .withAggregate(null)
//                .withIndex(null).takeHandler(null)
//                .build();

    }
}
