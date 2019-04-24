package com.gaufoo.bbs.components.active;

import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class ActiveSstRepository implements ActiveRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;
    private final SST cluster;

    private ActiveSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-time", storingPath);
        this.cluster = SST.of("cluster", storingPath);
    }

    @Override
    public boolean saveActive(String activeGroup, String id, ActiveInfo activeInfo) {
        String newKey = formatAG(activeGroup) + formatID(id);
        if (SstUtils.contains(idToInfo, newKey)) return false;
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(idToInfo, newKey, gson.toJson(activeInfo)));
        tasks.add(SstUtils.setEntryAsync(cluster, concat(activeGroup, id, activeInfo.time), "GAUFOO"));

        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    @Override
    public ActiveInfo getActive(String activeGroup, String id) {
        return SstUtils.getEntry(idToInfo, formatAG(activeGroup) + formatID(id), ActiveSstRepository::retrieveInfo);
    }

    @Override
    public boolean updateActive(String activeGroup, String id, ActiveInfo activeInfo) {
        return Optional.ofNullable(getActive(activeGroup, id)).map(info -> {
            List<CompletionStage<Boolean>> tasks = new ArrayList<>();
            tasks.add(SstUtils.setEntryAsync(idToInfo, formatAG(activeGroup) + formatID(id), gson.toJson(activeInfo)));
            tasks.add(cluster.delete(concat(activeGroup, id, info.time)).thenApply(Optional::isPresent));
            tasks.add(SstUtils.setEntryAsync(cluster, concat(activeGroup, id, activeInfo.time), "GAUFOO"));
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
    public boolean delete(String activeGroup, String id) {
        return Optional.ofNullable(SstUtils.removeEntryByKey(idToInfo, formatAG(activeGroup) + formatID(id), ActiveSstRepository::retrieveInfo))
                .map(oi -> SstUtils.removeEntryByKey(cluster, concat(activeGroup, id, oi.time)) != null).orElse(false);
    }

    @Override
    public boolean delete(String activeGroup) {
        SstUtils.waitAllFuturesPar(
                idToInfo.rangeKeysAsc(formatAG(activeGroup) + many('0', 14), formatAG(activeGroup) + many('9', 14))
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(idToInfo::delete))),
                cluster.rangeKeysAsc(formatAG(activeGroup) + many('0', 28), formatAG(activeGroup) + many('9', 28))
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(cluster::delete)))
        );
        return true;
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToInfo.shutdown(), cluster.shutdown());
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

    private static ActiveInfo retrieveInfo(String string) {
        return gson.fromJson(string, ActiveInfo.class);
    }

    public static ActiveRepository get(Path storingPath) {
        return new ActiveSstRepository(storingPath);
    }
}
