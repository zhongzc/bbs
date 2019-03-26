package com.gaufoo.bbs.components.lostfound;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lostfound.common.*;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public interface LostFound {
    LostId pubLost(LostInfo lostInfo);

    FoundId pubFound(FoundInfo foundInfo);

    Optional<LostInfo> lostInfo(LostId lostId);

    Optional<FoundInfo> foundInfo(FoundId foundId);

    boolean changeLostInfo(LostId lostId, LostInfo lostInfo);

    boolean changeFoundInfo(FoundId foundId, FoundInfo foundInfo);

    Stream<LostId> allLosts();

    Stream<FoundId> allFounds();

    default boolean claimLost(LostId lostId, String claimant) {
        Optional<LostInfo> info = lostInfo(lostId);
        return info.map(i -> i.claim(claimant)).orElse(false);
    }

    default boolean claimFound(FoundId foundId, String claimant) {
        Optional<FoundInfo> info = foundInfo(foundId);
        return info.map(i -> i.claim(claimant)).orElse(false);
    }

    default Stream<LostId> allUnclaimedLosts() {
        return allLosts()
                .filter(l -> lostInfo(l).filter(i -> !i.getClaimant().isPresent()).isPresent());
    }

    default Stream<FoundId> allUnclaimedFounds() {
        return allFounds()
                .filter(f -> foundInfo(f).filter(i -> !i.getClaimant().isPresent()).isPresent());
    }

    default Stream<LostId> searchLostByName(String objName) {
        return allLosts()
                .filter(l -> lostInfo(l).filter(i -> i.objName.matches(".*" + objName + ".*")).isPresent());
    }

    default Stream<FoundId> searchFoundByName(String objName) {
        return allFounds()
                .filter(f -> foundInfo(f).filter(i -> i.objName.matches(".*" + objName + ".*")).isPresent());
    }

    default Stream<LostId> searchLostByTime(Instant begin, Instant end) {
        return allLosts()
                .filter(lostId -> lostInfo(lostId).filter(l -> {
                    Instant lostTime = l.lostTime;
                    return !begin.isAfter(lostTime) && !lostTime.isAfter(end);
                }).isPresent());
    }

    default Stream<FoundId> searchFoundByTime(Instant begin, Instant end) {
        return allFounds()
                .filter(foundId -> foundInfo(foundId).filter(f -> {
                    Instant foundTime = f.foundTime;
                    return !begin.isAfter(foundTime) && !foundTime.isAfter(end);
                }).isPresent());
    }

    void removeLost(LostId lostId);

    void removeFound(FoundId foundId);

    static LostFound defau1t(LostFoundRepository repository,
                             IdGenerator idGenerator,
                             IdGenerator foundIds) {
        return new LostFoundImpl(repository, idGenerator, foundIds);
    }
}
