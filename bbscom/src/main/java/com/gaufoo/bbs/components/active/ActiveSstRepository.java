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
        String newKey = formatAG(activeGroup) + formatID(id);
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
            tasks.add(SstUtils.setEntryAsync(idToTime, formatAG(activeGroup) + formatID(id), format(time)));
            tasks.add(cluster.delete(concat(activeGroup, id, ot)).thenApply(Optional::isPresent));
            tasks.add(SstUtils.setEntryAsync(cluster, concat(activeGroup, id, time), "GAUFOO"));
            return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
        }).orElse(false);
    }

    @Override
    public Stream<String> getAllAsc(String activeGroup) {
        return SstUtils.waitFuture(cluster.rangeKeysAsc(
                formatAG(activeGroup) + many('0', 28),
                formatAG(activeGroup) + many('9', 28)).thenApply(
                s -> s.map(ActiveSstRepository::retrieveId)
        )).orElse(Stream.empty());
    }

    @Override
    public Stream<String> getAllDes(String activeGroup) {
        return SstUtils.waitFuture(cluster.rangeKeysDes(
                formatAG(activeGroup) + many('9', 28),
                formatAG(activeGroup) + many('0', 28)
        ).thenApply(
                s -> s.map(ActiveSstRepository::retrieveId)
        )).orElse(Stream.empty());
    }

    @Override
    public void delete(String activeGroup, String id) {
        Optional.ofNullable(getActive(activeGroup, id)).ifPresent(otime -> SstUtils.waitAllFuturesPar(
                idToTime.delete(formatAG(activeGroup) + formatID(id)),
                cluster.delete(concat(activeGroup, id, otime))));
    }

    @Override
    public void delete(String activeGroup) {
        SstUtils.waitAllFuturesPar(
                idToTime.rangeKeysAsc(formatAG(activeGroup) + many('0', 14), formatAG(activeGroup) + many('9', 14))
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(idToTime::delete))),
                cluster.rangeKeysAsc(formatAG(activeGroup) + many('0', 28), formatAG(activeGroup) + many('9', 28))
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(cluster::delete)))
        );
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToTime.shutdown(), cluster.shutdown());
    }

    private static String concat(String activeGroup, String id, Instant time) {
        return formatAG(activeGroup) + format(time) + formatID(id);
    }

    private static String retrieveId(String string) {
        return string.substring(28);
    }

    private static String format(Instant time) {
        return String.format("%014d", time.toEpochMilli());
    }

    private static String formatAG(String activeGroup) {
        return String.format("%14s", activeGroup);
    }

    private static String formatID(String id) {
        return String.format("%14s", id);
    }

    private static String many(char c, int len) {
        char[] cs = new char[len];
        for (int i = 0; i < len; i++) {
            cs[i] = c;
        }
        return new String(cs);
    }

    private static Instant retrieveTime(String string) {
        return Instant.ofEpochMilli(Long.parseLong(string));
    }

    public static ActiveRepository get(Path storingPath) {
        return new ActiveSstRepository(storingPath);
    }
}
