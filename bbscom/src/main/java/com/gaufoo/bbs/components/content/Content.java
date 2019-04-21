package com.gaufoo.bbs.components.content;

import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.content.common.ContentInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public interface Content {
    Optional<ContentId> cons(ContentInfo contentInfo);

    Optional<ContentInfo> contentInfo(ContentId contentId);

    void remove(ContentId contentId);

    static Content defau1t(ContentRepository repository, IdGenerator idGenerator) {
        return new ContentImpl(repository, idGenerator);
    }
}
