package com.gaufoo.bbs.components.active;

import com.gaufoo.bbs.components.active.common.ActiveInfo;

import java.time.Instant;
import java.util.stream.Stream;

public interface ActiveRepository {

    boolean saveActive(String activeGroup, String id, ActiveInfo activeInfo);

    ActiveInfo getActive(String activeGroup, String id);

    boolean updateActive(String activeGroup, String id, ActiveInfo activeInfo);

    Stream<String> getAllAsc(String activeGroup, int idLen);

    Stream<String> getAllDes(String activeGroup, int idLen);

    boolean delete(String activeGroup, String id);

    boolean delete(String activeGroup);

    default void shutdown() { }

}
