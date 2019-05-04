package com.gaufoo.bbs.components.file;

import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public class FileFactoryImpl implements FileFactory {
    private final FileFactoryRepository repository;
    private final IdGenerator idGenerator;

    FileFactoryImpl(FileFactoryRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<FileId> createFile(byte[] file, String filename) {
        FileId id = FileId.of(idGenerator.generateId());
        if (repository.saveFile(id, file, filename)) {
            return Optional.of(id);
        }
        return Optional.empty();
    }

    @Override
    public Optional<FileId> createFile(byte[] file) {
        FileId id = FileId.of(idGenerator.generateId());
        if (repository.saveFile(id, file, id.value)) {
            return Optional.of(id);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> filename(FileId id) {
        return Optional.ofNullable(repository.getFilename(id));
    }

    @Override
    public Optional<String> fileURI(FileId id) {
        return Optional.ofNullable(repository.getURI(id));
    }

    @Override
    public void remove(FileId id) {
        repository.delete(id);
    }

}
