package com.gaufoo.bbs.components.lostfound;

import com.gaufoo.bbs.components._repositories.LostFoundMemoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lostfound.common.FoundId;
import com.gaufoo.bbs.components.lostfound.common.FoundInfo;
import com.gaufoo.bbs.components.lostfound.common.LostId;
import com.gaufoo.bbs.components.lostfound.common.LostInfo;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LostFoundImpl implements LostFound {
    private final String componentName;
    private final LostFoundRepository repository;
    private final IdGenerator lostIds;
    private final IdGenerator foundIds;
    private final AtomicLong lostCounts;
    private final AtomicLong foundCounts;

    LostFoundImpl(String componentName, LostFoundRepository repository, IdGenerator lostIds, IdGenerator foundIds) {
        this.componentName = componentName;
        this.repository = repository;
        this.lostIds = lostIds;
        this.foundIds = foundIds;
        this.lostCounts = new AtomicLong(repository.getAllLosts().count());
        this.foundCounts = new AtomicLong(repository.getAllFounds().count());
    }

    @Override
    public Optional<LostId> pubLost(LostInfo lostInfo) {
        LostId id = LostId.of(lostIds.generateId());

        if (repository.saveLost(id, lostInfo)) {
            lostCounts.incrementAndGet();
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<FoundId> pubFound(FoundInfo foundInfo) {
        FoundId id = FoundId.of(foundIds.generateId());

        if (repository.saveFound(id, foundInfo)) {
            foundCounts.incrementAndGet();
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<LostInfo> lostInfo(LostId lostId) {
        return Optional.ofNullable(repository.getLostInfo(lostId));
    }

    @Override
    public Optional<FoundInfo> foundInfo(FoundId foundId) {
        return Optional.ofNullable(repository.getFoundInfo(foundId));
    }

    @Override
    public boolean changePublisher(LostId lostId, String newPublisher) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modPublisher(newPublisher))
        ).orElse(false);
    }

    @Override
    public boolean changeObjName(LostId lostId, String newObjName) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modObjName(newObjName))
        ).orElse(false);
    }

    @Override
    public boolean changeLostTime(LostId lostId, Instant newLostTime) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modLostTime(newLostTime))
        ).orElse(false);
    }

    @Override
    public boolean changePosition(LostId lostId, String newPosition) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modPosition(newPosition))
        ).orElse(false);
    }

    @Override
    public boolean changeDescription(LostId lostId, String newDescription) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modDescription(newDescription))
        ).orElse(false);
    }

    @Override
    public boolean changeImageIdentifier(LostId lostId, String newImageIdentifier) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modImageIdentifier(newImageIdentifier))
        ).orElse(false);
    }

    @Override
    public boolean changeContact(LostId lostId, String newContact) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modContact(newContact))
        ).orElse(false);
    }

    @Override
    public boolean changeClaimant(LostId lostId, String newClaimant) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).map(l ->
                repository.updateLost(lostId, l.modClaimant(newClaimant))
        ).orElse(false);
    }

    @Override
    public boolean changePublisher(FoundId foundId, String newPublisher) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modPublisher(newPublisher))
        ).orElse(false);
    }

    @Override
    public boolean changeObjName(FoundId foundId, String newObjName) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modObjName(newObjName))
        ).orElse(false);
    }

    @Override
    public boolean changeFoundTime(FoundId foundId, Instant newFoundTime) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modFoundTime(newFoundTime))
        ).orElse(false);
    }

    @Override
    public boolean changePosition(FoundId foundId, String newPosition) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modPosition(newPosition))
        ).orElse(false);
    }

    @Override
    public boolean changeDescription(FoundId foundId, String newDescription) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modDescription(newDescription))
        ).orElse(false);
    }

    @Override
    public boolean changeImageIdentifier(FoundId foundId, String newImageIdentifier) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modImageIdentifier(newImageIdentifier))
        ).orElse(false);
    }

    @Override
    public boolean changeContact(FoundId foundId, String newContact) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modContact(newContact))
        ).orElse(false);
    }

    @Override
    public boolean changeClaimant(FoundId foundId, String newClaimant) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).map(l ->
                repository.updateFound(foundId, l.modClaimant(newClaimant))
        ).orElse(false);
    }

    @Override
    public Stream<LostId> allLosts() {
        return repository.getAllLosts();
    }

    @Override
    public Long allLostCounts() {
        return lostCounts.get();
    }

    @Override
    public Stream<FoundId> allFounds() {
        return repository.getAllFounds();
    }

    @Override
    public Long allFoundCounts() {
        return foundCounts.get();
    }

    @Override
    public boolean claimLost(LostId lostId, String claimant) {
        Optional<LostInfo> lost = Optional.ofNullable(repository.getLostInfo(lostId));

        return lost.map(l -> {
            if (l.claimant != null) return false;
            else return repository.updateLost(lostId, l.modClaimant(claimant));
        }).orElse(false);
    }

    @Override
    public boolean claimFound(FoundId foundId, String claimant) {
        Optional<FoundInfo> found = Optional.ofNullable(repository.getFoundInfo(foundId));

        return found.map(f -> {
            if (f.claimant != null) return false;
            else return repository.updateFound(foundId, f.modClaimant(claimant));
        }).orElse(false);
    }

    @Override
    public void shutdown() {
        repository.shutdown();
    }

    @Override
    public void removeLost(LostId lostId) {
        lostCounts.decrementAndGet();
        repository.deleteLost(lostId);
    }

    @Override
    public void removeFound(FoundId foundId) {
        foundCounts.decrementAndGet();
        repository.deleteFound(foundId);
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) {
        LostFoundRepository repository = LostFoundMemoryRepository.get("lostfoundRep");
        LostFound lostFound = LostFound.defau1t("lostfoundCom", repository, IdGenerator.seqInteger(""), IdGenerator.seqInteger(""));
        Optional<LostId> id = lostFound.pubLost(LostInfo.of("aaa", "bbb", Instant.now(), "ccc", "ddd", "fff", "eee"));
        System.out.println(id);
        System.out.println(lostFound.lostInfo(id.get()));
        if (lostFound.changeImageIdentifier(id.get(), "a-beautiful-pic")) {
            System.out.println(lostFound.lostInfo(id.get()));
        }

        if (lostFound.claimLost(id.get(), "cm")) {
            System.out.println(lostFound.lostInfo(id.get()));
        }

    }

}
