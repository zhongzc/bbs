package com.gaufoo.bbs.components.fileBuilder;

import com.gaufoo.bbs.components.fileBuilder.common.FileId;

public interface FileBuilderRepository {
    boolean saveFile(FileId fileId, byte[] file, String filename);

    String getFilename(FileId fileId);

    String getURI(FileId fileId);

    void delete(FileId id);
}
