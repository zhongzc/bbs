package com.gaufoo.bbs.components.lostfound;

import com.gaufoo.bbs.components._repositories.LostFoundMemoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lostfound.common.*;

import java.util.Optional;
import java.util.stream.Stream;

public class LostFoundImpl implements LostFound {
    private final LostFoundRepository repository;
    private final IdGenerator lostIds;
    private final IdGenerator foundIds;

    LostFoundImpl(LostFoundRepository repository,
                  IdGenerator idGenerator,
                  IdGenerator foundIds) {
        this.repository = repository;
        this.lostIds = idGenerator;
        this.foundIds = foundIds;
    }

    @Override
    public LostId pubLost(LostInfo lostInfo) {
        LostId id = LostId.of(lostIds.generateId());
        if (repository.saveLost(id, lostInfo)) {
            return id;
        }
        return null;
    }

    @Override
    public FoundId pubFound(FoundInfo foundInfo) {
        FoundId id = FoundId.of(foundIds.generateId());
        if (repository.saveFound(id, foundInfo)) {
            return id;
        }
        return null;
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
    public boolean changeLostInfo(LostId lostId, LostInfo lostInfo) {
        if (lostInfo(lostId).isPresent()) {
            return repository.updateLost(lostId, lostInfo);
        }
        return false;
    }

    @Override
    public boolean changeFoundInfo(FoundId foundId, FoundInfo foundInfo) {
        if (foundInfo(foundId).isPresent()) {
            return repository.updateFound(foundId, foundInfo);
        }
        return false;
    }

    @Override
    public Stream<LostId> allLosts() {
        return repository.getAllLosts();
    }

    @Override
    public Stream<FoundId> allFounds() {
        return repository.getAllFounds();
    }

    @Override
    public void removeLost(LostId lostId) {
        repository.deleteLost(lostId);
    }

    @Override
    public void removeFound(FoundId foundId) {
        repository.deleteFound(foundId);
    }

    public static void main(String[] args) {
        LostFoundRepository repository = LostFoundMemoryRepository.get();
        IdGenerator a = IdGenerator.seqInteger();
        IdGenerator b = IdGenerator.seqInteger();
        LostFound lostFound = LostFound.defau1t(repository, a, b);
//        lostFound
    }
}
