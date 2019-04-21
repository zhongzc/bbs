package com.gaufoo.bbs.components.content;

import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.content.common.ContentInfo;

public interface ContentRepository {
    ContentInfo get(ContentId contentId);

    boolean save(ContentId contentId, ContentInfo contentInfo);

    void delete(ContentId contentId);

    default void shutdown() {}
}
