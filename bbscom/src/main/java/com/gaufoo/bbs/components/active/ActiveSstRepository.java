package com.gaufoo.bbs.components.active;

import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class ActiveSstRepository implements ActiveRepository {
    private final SST idToTime;
    private final SST cluster;

    private ActiveSstRepository(Path storingPath) {
        this.idToTime = SST.of("id-to-time", storingPath);
        this.cluster = SST.of("cluster", storingPath);
    }

    @Override
    public boolean saveActive(String activeGroup, String id, Instant time) {
        String newKey = formatAG(activeGroup) + id;
        if (SstUtils.contains(idToTime, newKey)) return false;
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(idToTime, newKey, format(time)));
        tasks.add(SstUtils.setEntryAsync(cluster, concat(activeGroup, id, time), "GAUFOO"));

        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    @Override
    public Instant getActive(String activeGroup, String id) {
        return SstUtils.getEntry(idToTime, formatAG(activeGroup) + id, ActiveSstRepository::retrieveTime);
    }

    @Override
    public boolean updateActive(String activeGroup, String id, Instant time) {
        return Optional.ofNullable(getActive(activeGroup, id)).map(ot -> {
            List<CompletionStage<Boolean>> tasks = new ArrayList<>();
            tasks.add(SstUtils.setEntryAsync(idToTime, formatAG(activeGroup) + id, format(time)));
            tasks.add(cluster.delete(concat(activeGroup, id, ot)).thenApply(Optional::isPresent));
            tasks.add(SstUtils.setEntryAsync(cluster, concat(activeGroup, id, time), "GAUFOO"));
            return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
        }).orElse(false);
    }

    @Override
    public Stream<String> getAllAsc(String activeGroup) {
        return SstUtils.waitFuture(cluster.rangeKeysAsc(
                formatAG(activeGroup) + "0000000000000000000000",
                formatAG(activeGroup) + "9999999999999999999999").thenApply(
                s -> s.map(ActiveSstRepository::retrieveId)
        )).orElse(Stream.empty());
    }

    @Override
    public Stream<String> getAllDes(String activeGroup) {
        return SstUtils.waitFuture(cluster.rangeKeysDes(
                formatAG(activeGroup) + "9999999999999999999999",
                formatAG(activeGroup) + "0000000000000000000000"
        ).thenApply(
                s -> s.map(ActiveSstRepository::retrieveId)
        )).orElse(Stream.empty());
    }

    @Override
    public void delete(String activeGroup, String id) {
        Optional.ofNullable(getActive(activeGroup, id)).ifPresent(otime -> SstUtils.waitAllFuturesPar(
                idToTime.delete(formatAG(activeGroup) + id),
                cluster.delete(concat(activeGroup, id, otime))));
    }

    @Override
    public void delete(String activeGroup) {
        SstUtils.waitAllFuturesPar(
                idToTime.rangeKeysAsc(formatAG(activeGroup) + "00000000", formatAG(activeGroup) + "99999999")
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(idToTime::delete))),
                cluster.rangeKeysAsc(formatAG(activeGroup) + "0000000000000000000000", formatAG(activeGroup) + "9999999999999999999999")
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(cluster::delete)))
        );
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToTime.shutdown(), cluster.shutdown());
    }

    private static String concat(String activeGroup, String id, Instant time) {
        return formatAG(activeGroup) + format(time) + id;
    }

    private static String retrieveId(String string) {
        return string.substring(22);
    }

    private static String format(Instant time) {
        return String.format("%014d", time.toEpochMilli());
    }

    private static String formatAG(String activeGroup) {
        return String.format("%8s", activeGroup);
    }

    private static Instant retrieveTime(String string) {
        return Instant.ofEpochMilli(Long.parseLong(string));
    }

    public static ActiveRepository get(Path storingPath) {
        return new ActiveSstRepository(storingPath);
    }
}