package com.gaufoo.bbs.components.lostfound;

import com.gaufoo.bbs.components.lostfound.common.*;

import java.util.stream.Stream;

public interface LostFoundRepository {
    boolean saveLost(LostId id, LostInternal info);

    boolean saveFound(FoundId id, FoundInternal info);

    boolean updateLost(LostId id, LostInternal info);

    boolean updateFound(FoundId id, FoundInternal info);

    LostInternal getLostInfo(LostId id);

    FoundInternal getFoundInfo(FoundId id);

    Stream<LostId> getAllLosts();

    Stream<FoundId> getAllFounds();

    void deleteLost(LostId id);

    void deleteFound(FoundId id);
}
