package com.gaufoo.bbs.components.active;

import com.gaufoo.bbs.components.active.common.ActiveInfo;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public interface Active {
    // activeGroup ascii 字长最长 14, id ascii 字长最长 14
    Optional<ActiveInfo> touch(String activeGroup, String itemId, String toucherId);

    Optional<ActiveInfo> getLatestActiveInfo(String activeGroup, String itemId);

    boolean remove(String activeGroup, String itemId);

    Stream<String> getAllAsc(String activeGroup, int idLen);

    Stream<String> getAllDes(String activeGroup, int idLen);

    boolean removeAll(String activeGroup);

    static Active defau1t(ActiveRepository repository) {
        return new ActiveImpl(repository);
    }
}
