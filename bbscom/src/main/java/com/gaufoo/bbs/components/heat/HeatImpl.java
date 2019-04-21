package com.gaufoo.bbs.components.heat;

import java.util.Optional;
import java.util.stream.Stream;

public class HeatImpl implements Heat {
    private final HeatRepository repository;

    HeatImpl(HeatRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Long> cons(String heatGroup, String id) {
        if (repository.saveHeat(heatGroup, id, 0)) {
            return Optional.of(0L);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> increase(String heatGroup, String id, long delta) {
        return getHeat(heatGroup, id).flatMap(h -> {
            if (repository.updateHeat(heatGroup, id, h + delta)) {
                return Optional.of(h + delta);
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
    public void remove(String heatGroup, String id) {
        repository.delete(heatGroup, id);
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
    public void removeAll(String heatGroup) {
        repository.delete(heatGroup);
    }
}
