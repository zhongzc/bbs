package com.gaufoo.bbs.components.file;

import com.gaufoo.bbs.components.file.common.FileId;

public interface FileFactoryRepository {
    boolean saveFile(FileId fileId, byte[] file, String filename);

    String getFilename(FileId fileId);

    String getURI(FileId fileId);

    boolean delete(FileId id);

    default void shutdown() {}
}
