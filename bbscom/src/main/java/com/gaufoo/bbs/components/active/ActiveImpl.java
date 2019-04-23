package com.gaufoo.bbs.components.active;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public class ActiveImpl implements Active {
    private final ActiveRepository repository;

    ActiveImpl(ActiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Instant> touch(String activeGroup, String id) {
        Instant time = Instant.now();

        Optional<Optional<Instant>> i = getLatestActiveTime(activeGroup, id).map(__ -> {
            if (repository.updateActive(activeGroup, id, time)) {
                return Optional.of(time);
            } else {
                return Optional.empty();
            }
        });

        return i.orElseGet(() -> {
            if (repository.saveActive(activeGroup, id, time)) {
                return Optional.of(time);
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<Instant> getLatestActiveTime(String activeGroup, String id) {
        return Optional.ofNullable(repository.getActive(activeGroup, id));
    }

    @Override
    public boolean remove(String activeGroup, String id) {
        return repository.delete(activeGroup, id);
    }

    @Override
    public Stream<String> getAllAsc(String activeGroup) {
        return repository.getAllAsc(activeGroup);
    }

    @Override
    public Stream<String> getAllDes(String activeGroup) {
        return repository.getAllDes(activeGroup);
    }

    @Override
    public boolean removeAll(String activeGroup) {
        return repository.delete(activeGroup);
    }
}
