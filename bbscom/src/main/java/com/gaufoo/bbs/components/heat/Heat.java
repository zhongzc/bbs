package com.gaufoo.bbs.components.heat;

import java.util.Optional;
import java.util.stream.Stream;

public interface Heat {
    // heatGroup 字长 8, id 字长 8
    Optional<Long> increase(String heatGroup, String id, long delta);

    Optional<Long> getHeat(String heatGroup, String id);

    void remove(String heatGroup, String id);

    Stream<String> getAllAsc(String heatGroup);

    Stream<String> getAllDes(String heatGroup);

    void removeAll(String heatGroup);

    static Heat defau1t(HeatRepository repository) {
        return new HeatImpl(repository);
    }
}
