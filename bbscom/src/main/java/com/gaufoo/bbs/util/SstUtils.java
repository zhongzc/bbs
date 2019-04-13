package com.gaufoo.bbs.util;

import com.gaufoo.sst.SST;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class SstUtils {

    public static <T> T getEntry(SST kvMap, String key, Function<String, T> shaper) {
        return waitFuture(kvMap.get(key)
                .thenApply(oStr -> oStr
                        .map(shaper)
                        .orElse(null)))
                .orElse(null);
    }

    public static boolean setEntry(SST kvMap, String key, String value) {
        return waitFuture(kvMap.set(key, value)
                .thenApply(value::equals)).orElse(false);
    }

    public static void removeEntryWithKey(SST kvMap, String key) {
        waitFuture(kvMap.delete(key));
    }

    public static void removeEntryWithValue(SST kvMap, String value) {
        waitFuture(kvMap.allKeysAsc()
                .thenAccept(stringStream -> stringStream
                        .map(key -> Tuple.of(key, waitFuture(kvMap.get(key)).orElse(Optional.empty())))
                        .filter(tuple -> tuple.right.isPresent() && tuple.right.get().equals(value))
                        .findFirst()
                        .map(tup -> tup.left)
                        .ifPresent(matchedKey -> waitFuture(kvMap.delete(matchedKey)))
                )
        );
    }

    public static <T> Optional<T> waitFuture(CompletionStage<T> completableFuture) {
        try {
            return Optional.ofNullable(completableFuture.toCompletableFuture().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void waitAllFuturesPar(CompletionStage<?>... completionStages) {
        CompletableFuture<?>[] futures = Arrays.stream(completionStages)
                .map(CompletionStage::toCompletableFuture).toArray(CompletableFuture<?>[]::new);
        try {
            CompletableFuture.allOf(futures).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
