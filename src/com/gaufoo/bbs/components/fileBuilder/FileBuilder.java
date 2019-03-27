package com.gaufoo.bbs.components.fileBuilder;

import com.gaufoo.bbs.components.fileBuilder.common.FileId;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public interface FileBuilder {
    Optional<FileId> createFile(byte[] file, String filename);

    Optional<String> filename(FileId id);

    Optional<String> fileURI(FileId id);

    void Remove(FileId id);

    static FileBuilder defau1t(FileBuilderRepository repository, IdGenerator idGenerator) {
        return new FileBuilderImpl(repository, idGenerator);
    }
}
