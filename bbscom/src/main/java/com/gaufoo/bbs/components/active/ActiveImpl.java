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
    public Optional<Instant> cons(String activeGroup, String id) {
        Instant time = Instant.now();
        if (repository.saveActive(activeGroup, id, time)) {
            return Optional.of(time);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Instant> touch(String activeGroup, String id) {
        Instant time = Instant.now();
        if (repository.updateActive(activeGroup, id, time)) {
            return Optional.of(time);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Instant> getLatestActiveTime(String activeGroup, String id) {
        return Optional.ofNullable(repository.getActive(activeGroup, id));
    }

    @Override
    public void remove(String activeGroup, String id) {
        repository.delete(activeGroup, id);
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
    public void removeAll(String activeGroup) {
        repository.delete(activeGroup);
    }
}
