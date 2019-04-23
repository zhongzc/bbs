package com.gaufoo.bbs.components.active;

import java.time.Instant;
import java.util.stream.Stream;

public interface ActiveRepository {

    boolean saveActive(String activeGroup, String id, Instant time);

    Instant getActive(String activeGroup, String id);

    boolean updateActive(String activeGroup, String id, Instant time);

    Stream<String> getAllAsc(String activeGroup);

    Stream<String> getAllDes(String activeGroup);

    boolean delete(String activeGroup, String id);

    boolean delete(String activeGroup);

    default void shutdown() { }

}
