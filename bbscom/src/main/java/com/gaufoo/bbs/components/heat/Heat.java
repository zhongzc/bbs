package com.gaufoo.bbs.components.heat;

import java.util.Optional;
import java.util.stream.Stream;

public interface Heat {
    // heatGroup ascii 字长最长 14, id ascii 字长最长 14
    Optional<Long> increase(String heatGroup, String id, long delta);

    Optional<Long> getHeat(String heatGroup, String id);

    boolean remove(String heatGroup, String id);

    Stream<String> getAllAsc(String heatGroup);

    Stream<String> getAllDes(String heatGroup);

    boolean removeAll(String heatGroup);

    static Heat defau1t(HeatRepository repository) {
        return new HeatImpl(repository);
    }
}
