package com.gaufoo.bbs.components.file;

import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public interface FileFactory {
    Optional<FileId> createFile(byte[] file, String filename);

    Optional<String> filename(FileId id);

    Optional<String> fileURI(FileId id);

    void Remove(FileId id);

    String getName();

    static FileFactory defau1t(String componentName, FileFactoryRepository repository, IdGenerator idGenerator) {
        return new FileFactoryImpl(componentName, repository, idGenerator);
    }
}