package com.gaufoo.bbs.components.lostfound;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lostfound.common.FoundId;
import com.gaufoo.bbs.components.lostfound.common.FoundInfo;
import com.gaufoo.bbs.components.lostfound.common.LostId;
import com.gaufoo.bbs.components.lostfound.common.LostInfo;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public interface LostFound {
    Optional<LostId> pubLost(LostInfo lostInput);

    Optional<FoundId> pubFound(FoundInfo foundInput);

    Optional<LostInfo> lostInfo(LostId lostId);

    Optional<FoundInfo> foundInfo(FoundId foundId);

    boolean changePublisher(LostId lostId, String newPublisher);

    boolean changeObjName(LostId lostId, String newObjName);

    boolean changeLostTime(LostId lostId, Instant newLostTime);

    boolean changePosition(LostId lostId, String newPosition);

    boolean changeDescription(LostId lostId, String newDescription);

    boolean changeImageIdentifier(LostId lostId, String newImageIdentifier);

    boolean changeContact(LostId lostId, String newContact);

    boolean changeClaimant(LostId lostId, String newClaimant);

    boolean changePublisher(FoundId foundId, String newPublisher);

    boolean changeObjName(FoundId foundId, String newObjName);

    boolean changeFoundTime(FoundId foundId, Instant newFoundTime);

    boolean changePosition(FoundId foundId, String newPosition);

    boolean changeDescription(FoundId foundId, String newDescription);

    boolean changeImageIdentifier(FoundId foundId, String newImageIdentifier);

    boolean changeContact(FoundId foundId, String newContact);

    boolean changeClaimant(FoundId foundId, String newClaimant);

    Stream<LostId> allLosts();

    Stream<FoundId> allFounds();

    boolean claimLost(LostId lostId, String claimant);

    boolean claimFound(FoundId foundId, String claimant);

    default Stream<LostId> allUnclaimedLosts() {
        return allLosts().filter(l ->
                lostInfo(l).map(i -> i.claimant == null).orElse(false));
    }

    default Stream<FoundId> allUnclaimedFounds() {
        return allFounds().filter(f ->
                foundInfo(f).map(i -> i.claimant == null).orElse(false));
    }

    default Stream<LostId> searchLostByName(String objName) {
        return allLosts().filter(l ->
                lostInfo(l).map(i -> i.objName.matches(".*" + objName + ".*")).orElse(false));
    }

    default Stream<FoundId> searchFoundByName(String objName) {
        return allFounds().filter(f ->
                foundInfo(f).map(i -> i.objName.matches(".*" + objName + ".*")).orElse(false));
    }

    default Stream<LostId> searchLostByTime(Instant begin, Instant end) {
        return allLosts()
                .filter(lostId -> lostInfo(lostId).map(l -> {
                    Instant lostTime = l.lostTime;
                    return !begin.isAfter(lostTime) && !lostTime.isAfter(end);
                }).orElse(false));
    }

    default Stream<FoundId> searchFoundByTime(Instant begin, Instant end) {
        return allFounds()
                .filter(foundId -> foundInfo(foundId).map(f -> {
                    Instant foundTime = f.foundTime;
                    return !begin.isAfter(foundTime) && !foundTime.isAfter(end);
                }).orElse(false));
    }

    void removeLost(LostId lostId);

    void removeFound(FoundId foundId);

    String getName();

    static LostFound defau1t(String componentName, LostFoundRepository repository, IdGenerator lostIds, IdGenerator foundIds) {
        return new LostFoundImpl(componentName, repository, lostIds, foundIds);
    }
}
