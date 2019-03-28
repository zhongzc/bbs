package com.gaufoo.bbs.components.fileBuilder;

import com.gaufoo.bbs.components.fileBuilder.common.FileId;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public interface FileBuilder {
    Optional<FileId> createFile(byte[] file, String filename);

    Optional<String> filename(FileId id);

    Optional<String> fileURI(FileId id);

    void Remove(FileId id);

    String getName();

    static FileBuilder defau1t(String componentName, FileBuilderRepository repository, IdGenerator idGenerator) {
        return new FileBuilderImpl(componentName, repository, idGenerator);
    }
}
