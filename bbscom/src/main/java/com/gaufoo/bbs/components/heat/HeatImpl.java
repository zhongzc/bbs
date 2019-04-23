package com.gaufoo.bbs.components.heat;

import java.util.Optional;
import java.util.stream.Stream;

public class HeatImpl implements Heat {
    private final HeatRepository repository;

    HeatImpl(HeatRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Long> increase(String heatGroup, String id, long delta) {
        Optional<Optional<Long>> l = getHeat(heatGroup, id).map(h -> {
            if (repository.updateHeat(heatGroup, id, h + delta)) {
                return Optional.of(h + delta);
            } else {
                return Optional.empty();
            }
        });

        return l.orElseGet(() -> {
            if (repository.saveHeat(heatGroup, id, delta)) {
                return Optional.of(delta);
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<Long> getHeat(String heatGroup, String id) {
        return Optional.ofNullable(repository.getHeat(heatGroup, id));
    }

    @Override
    public boolean remove(String heatGroup, String id) {
        return repository.delete(heatGroup, id);
    }

    @Override
    public Stream<String> getAllAsc(String heatGroup) {
        return repository.getAllAsc(heatGroup);
    }

    @Override
    public Stream<String> getAllDes(String heatGroup) {
        return repository.getAllDes(heatGroup);
    }

    @Override
    public boolean removeAll(String heatGroup) {
        return repository.delete(heatGroup);
    }
}
