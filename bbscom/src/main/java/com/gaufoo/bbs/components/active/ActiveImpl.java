package com.gaufoo.bbs.components.active;

import com.gaufoo.bbs.components.active.common.ActiveInfo;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public class ActiveImpl implements Active {
    private final ActiveRepository repository;

    ActiveImpl(ActiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ActiveInfo> touch(String activeGroup, String itemId, String toucherId) {
        Instant time = Instant.now();
        ActiveInfo newInfo = ActiveInfo.of(toucherId, time);

        Optional<Optional<ActiveInfo>> i = getLatestActiveInfo(activeGroup, itemId).map(__ -> {
            if (repository.updateActive(activeGroup, itemId, newInfo)) {
                return Optional.of(newInfo);
            } else {
                return Optional.empty();
            }
        });

        return i.orElseGet(() -> {
            if (repository.saveActive(activeGroup, itemId, newInfo)) {
                return Optional.of(newInfo);
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<ActiveInfo> getLatestActiveInfo(String activeGroup, String id) {
        return Optional.ofNullable(repository.getActive(activeGroup, id));
    }

    @Override
    public boolean remove(String activeGroup, String id) {
        return repository.delete(activeGroup, id);
    }

    @Override
    public Stream<String> getAllAsc(String activeGroup, int idLen) {
        return repository.getAllAsc(activeGroup, idLen);
    }

    @Override
    public Stream<String> getAllDes(String activeGroup, int idLen) {
        return repository.getAllDes(activeGroup, idLen);
    }

    @Override
    public boolean removeAll(String activeGroup) {
        return repository.delete(activeGroup);
    }
}
