package com.gaufoo.bbs.components.active;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public interface Active {
    // activeGroup ascii 字长最长 14, id ascii 字长最长 14
    Optional<Instant> touch(String activeGroup, String id);

    Optional<Instant> getLatestActiveTime(String activeGroup, String id);

    void remove(String activeGroup, String id);

    Stream<String> getAllAsc(String activeGroup);

    Stream<String> getAllDes(String activeGroup);

    void removeAll(String activeGroup);

    static Active defau1t(ActiveRepository repository) {
        return new ActiveImpl(repository);
    }
}
