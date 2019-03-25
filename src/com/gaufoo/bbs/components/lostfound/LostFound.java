package com.gaufoo.bbs.components.lostfound;

import com.gaufoo.bbs.components.lostfound.common.*;

import java.time.Instant;
import java.util.stream.Stream;

public interface LostFound {
    LostId pubLost(LostInfo lostInfo);

    FoundId pubFound(FoundInfo foundInfo);

    LostInfo lostInfo(LostId lostId);

    FoundInfo foundInfo(FoundId foundId);

    Stream<LostId> allLosts();

    Stream<FoundId> allFounds();

    default boolean claimLost(LostId lostId, String claimant) {
        return lostInfo(lostId).claim(claimant);
    }

    default boolean claimFound(FoundId foundId, String claimant) {
        return foundInfo(foundId).claim(claimant);
    }

    default Stream<LostId> searchLostByName(String objName) {
        return allLosts()
                .filter(l -> lostInfo(l).objName.matches(".*" + objName + ".*"));
    }

    default Stream<FoundId> searchFoundByName(String objName) {
        return allFounds()
                .filter(f -> foundInfo(f).objName.matches(".*" + objName + ".*"));
    }

    default Stream<LostId> searchLostByTime(Instant begin, Instant end) {
        return allLosts()
                .filter(l -> {
                    Instant lostTime = lostInfo(l).lostTime;
                    return !begin.isAfter(lostTime) && !lostTime.isAfter(end);
                });
    }

    default Stream<FoundId> searchFoundByTime(Instant begin, Instant end) {
        return allFounds()
                .filter(f -> {
                    Instant foundTime = foundInfo(f).foundTime;
                    return !begin.isAfter(foundTime) && !foundTime.isAfter(end);
                });
    }

    void removeLost(LostId lostId);

    void removeFound(FoundId foundId);
}
