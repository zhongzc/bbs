package com.gaufoo.bbs.components.heat;

import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class HeatSstRepository implements HeatRepository {
    private final SST idToHeat;
    private final SST cluster;

    private HeatSstRepository(Path storingPath) {
        this.idToHeat = SST.of("id-to-heat", storingPath);
        this.cluster = SST.of("cluster", storingPath);
    }

    @Override
    public boolean saveHeat(String heatGroup, String id, long init) {
        String newKey = formatHG(heatGroup) + id;
        if (SstUtils.contains(idToHeat, newKey)) return false;
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(idToHeat, newKey, format(init)));
        tasks.add(SstUtils.setEntryAsync(cluster, concat(heatGroup, id, init), "GAUFOO"));

        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    @Override
    public Long getHeat(String heatGroup, String id) {
        return SstUtils.getEntry(idToHeat, formatHG(heatGroup) + id, HeatSstRepository::retrieveHeat);
    }

    @Override
    public boolean updateHeat(String heatGroup, String id, long value) {
        return Optional.ofNullable(getHeat(heatGroup, id)).map(oh -> {
            List<CompletionStage<Boolean>> tasks = new ArrayList<>();
            tasks.add(SstUtils.setEntryAsync(idToHeat, formatHG(heatGroup) + id, format(value)));
            tasks.add(cluster.delete(concat(heatGroup, id, oh)).thenApply(Optional::isPresent));
            tasks.add(SstUtils.setEntryAsync(cluster, concat(heatGroup, id, value), "GAUFOO"));
            return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
        }).orElse(false);
    }

    @Override
    public Stream<String> getAllAsc(String heatGroup) {
        return SstUtils.waitFuture(cluster.rangeKeysAsc(
                formatHG(heatGroup) + "0000000000000000000000",
                formatHG(heatGroup) + "9999999999999999999999"
        ).thenApply(
                s -> s.map(HeatSstRepository::retrieveId)
        )).orElse(Stream.empty());
    }

    @Override
    public Stream<String> getAllDes(String heatGroup) {
        return SstUtils.waitFuture(cluster.rangeKeysDes(
                formatHG(heatGroup) + "9999999999999999999999",
                formatHG(heatGroup) + "0000000000000000000000"
        ).thenApply(
                s -> s.map(HeatSstRepository::retrieveId)
        )).orElse(Stream.empty());
    }

    @Override
    public void delete(String heatGroup, String id) {
        Optional.ofNullable(getHeat(heatGroup, id)).ifPresent(h -> SstUtils.waitAllFuturesPar(
                idToHeat.delete(formatHG(heatGroup) + id),
                cluster.delete(concat(heatGroup, id, h))));
    }

    @Override
    public void delete(String heatGroup) {
        SstUtils.waitAllFuturesPar(
                idToHeat.rangeKeysAsc(formatHG(heatGroup) + "00000000", formatHG(heatGroup) + "99999999")
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(idToHeat::delete))),
                cluster.rangeKeysAsc(formatHG(heatGroup) + "0000000000000000000000", formatHG(heatGroup) + "9999999999999999999999")
                        .thenAccept(keys -> SstUtils.waitAllFuturesPar(keys.map(cluster::delete)))
        );
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToHeat.shutdown(), cluster.shutdown());
    }

    private static String concat(String heatGroup, String id, Long heat) {
        return formatHG(heatGroup) + format(heat) + id;
    }

    private static String retrieveId(String string) {
        return string.substring(22);
    }

    private static String format(Long heat) {
        return String.format("%014d", heat);
    }

    private static String formatHG(String heatGroup) {
        return String.format("%8s", heatGroup);
    }

    private static Long retrieveHeat(String string) {
        return Long.parseLong(string);
    }

    public static HeatRepository get(Path storingPath) {
        return new HeatSstRepository(storingPath);
    }
}
