package com.gaufoo.bbs.components._depr_lostfound;

import com.gaufoo.bbs.components._depr_lostfound.common.*;

import java.util.stream.Stream;

public interface LostFoundRepository {
    boolean saveLost(LostId id, LostInfo info);

    boolean saveFound(FoundId id, FoundInfo info);

    boolean updateLost(LostId id, LostInfo info);

    boolean updateFound(FoundId id, FoundInfo info);

    LostInfo getLostInfo(LostId id);

    FoundInfo getFoundInfo(FoundId id);

    Stream<LostId> getAllLosts();

    Stream<FoundId> getAllFounds();

    void deleteLost(LostId id);

    void deleteFound(FoundId id);

    default void shutdown() {}
}
