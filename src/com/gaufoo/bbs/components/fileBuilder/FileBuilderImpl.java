package com.gaufoo.bbs.components.fileBuilder;

import com.gaufoo.bbs.components.fileBuilder.common.FileId;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public class FileBuilderImpl implements FileBuilder {
    private final FileBuilderRepository repository;
    private final IdGenerator idGenerator;

    FileBuilderImpl(FileBuilderRepository repository, IdGenerator idGenerator) {
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
    public Optional<String> filename(FileId id) {
        return Optional.ofNullable(repository.getFilename(id));
    }

    @Override
    public Optional<String> fileURI(FileId id) {
        return Optional.ofNullable(repository.getURI(id));
    }

    @Override
    public void Remove(FileId id) {
        repository.delete(id);
    }
}
