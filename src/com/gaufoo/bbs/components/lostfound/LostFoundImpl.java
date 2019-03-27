package com.gaufoo.bbs.components.lostfound;

import com.gaufoo.bbs.components._repositories.FileBuilderMemoryRepository;
import com.gaufoo.bbs.components._repositories.LostFoundMemoryRepository;
import com.gaufoo.bbs.components.fileBuilder.FileBuilder;
import com.gaufoo.bbs.components.fileBuilder.common.FileId;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lostfound.common.*;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public class LostFoundImpl implements LostFound {
    private final LostFoundRepository repository;
    private final FileBuilder fileBuilder;
    private final IdGenerator lostIds;
    private final IdGenerator foundIds;

    LostFoundImpl(LostFoundRepository repository,
                  FileBuilder fileBuilder,
                  IdGenerator lostIds,
                  IdGenerator foundIds) {
        this.repository = repository;
        this.fileBuilder = fileBuilder;
        this.lostIds = lostIds;
        this.foundIds = foundIds;
    }

    @Override
    public Optional<LostId> pubLost(LostInput lostInput) {
        LostId id = LostId.of(lostIds.generateId());

        return fromInput(lostInput, id).flatMap(in -> {
            if (repository.saveLost(id, in)) return Optional.of(id);
            else return Optional.empty();
        });
    }

    @Override
    public Optional<FoundId> pubFound(FoundInput foundInput) {
        FoundId id = FoundId.of(foundIds.generateId());

        return fromInput(foundInput, id).flatMap(in -> {
            if (repository.saveFound(id, in)) return Optional.of(id);
            else return Optional.empty();
        });
    }

    @Override
    public Optional<LostInfo> lostInfo(LostId lostId) {
        return Optional.ofNullable(repository.getLostInfo(lostId)).flatMap(this::fromInternal);
    }

    @Override
    public Optional<FoundInfo> foundInfo(FoundId foundId) {
        return Optional.ofNullable(repository.getFoundInfo(foundId)).flatMap(this::fromInternal);
    }

//    @Override
//    public boolean changeLostInfo(LostId lostId, LostInput lostInput) {
//        if (lostInfo(lostId).isPresent()) {
//            return repository.updateLost(lostId, lostInput);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean changeFoundInfo(FoundId foundId, FoundInput foundInput) {
//        if (foundInfo(foundId).isPresent()) {
//            return repository.updateFound(foundId, foundInput);
//        }
//        return false;
//    }

    @Override
    public Stream<LostId> allLosts() {
        return repository.getAllLosts();
    }

    @Override
    public Stream<FoundId> allFounds() {
        return repository.getAllFounds();
    }

    @Override
    public boolean claimLost(LostId lostId, String claimant) {
        Optional<LostInternal> lost = Optional.ofNullable(repository.getLostInfo(lostId));

        return lost.map(l -> {
            if (l.claimant != null) return false;
            else return repository.updateLost(lostId, l.modClaimant(claimant));
        }).orElse(false);
    }

    @Override
    public boolean claimFound(FoundId foundId, String claimant) {
        Optional<FoundInternal> found = Optional.ofNullable(repository.getFoundInfo(foundId));

        return found.map(f -> {
            if (f.claimant != null) return false;
            else return repository.updateFound(foundId, f.modClaimant(claimant));
        }).orElse(false);
    }

    @Override
    public void removeLost(LostId lostId) {
        repository.deleteLost(lostId);
    }

    @Override
    public void removeFound(FoundId foundId) {
        repository.deleteFound(foundId);
    }

    private Optional<LostInternal> fromInput(LostInput lostInput, LostId id) {
        Optional<FileId> fileId = fileBuilder.createFile(lostInput.image, id.value + lostInput.objName);
        return fileId.map(f -> LostInternal.of(lostInput.publisher, lostInput.objName, lostInput.lostTime,
                lostInput.position, lostInput.description, f, lostInput.contact));
    }

    private Optional<FoundInternal> fromInput(FoundInput foundInput, FoundId id) {
        Optional<FileId> fileId = fileBuilder.createFile(foundInput.image, id.value + foundInput.objName);
        return fileId.map(f -> FoundInternal.of(foundInput.publisher, foundInput.objName, foundInput.foundTime,
                foundInput.position, foundInput.description, f, foundInput.contact));
    }

    private Optional<FoundInfo> fromInternal(FoundInternal foundInternal) {
        Optional<String> imageURI = fileBuilder.fileURI(foundInternal.image);
        return imageURI.map(uri -> FoundInfo.of(foundInternal.publisher, foundInternal.objName, foundInternal.foundTime,
                foundInternal.position, foundInternal.description, uri, foundInternal.contact, foundInternal.claimant));
    }

    private Optional<LostInfo> fromInternal(LostInternal lostInternal) {
        Optional<String> imageURI = fileBuilder.fileURI(lostInternal.image);
        return imageURI.map(uri -> LostInfo.of(lostInternal.publisher, lostInternal.objName, lostInternal.lostTime,
                lostInternal.position, lostInternal.description, uri, lostInternal.contact, lostInternal.claimant));
    }

    public static void main(String[] args) {
        LostFoundRepository repository = LostFoundMemoryRepository.get();
        FileBuilder fileBuilder = FileBuilder.defau1t(FileBuilderMemoryRepository.get(), IdGenerator.seqInteger());
        LostFound lostFound = LostFound.defau1t(repository, fileBuilder, IdGenerator.seqInteger(), IdGenerator.seqInteger());
        byte[] image = {};
        Optional<LostId> id = lostFound.pubLost(LostInput.of("aaa", "bbb", Instant.now(), "ccc", "ddd", image, "eee"));
        System.out.println(id);
    }


}
