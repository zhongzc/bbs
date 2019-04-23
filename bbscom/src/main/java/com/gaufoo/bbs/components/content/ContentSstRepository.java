package com.gaufoo.bbs.components.content;

import com.gaufoo.bbs.components.content.common.ContentElem;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.content.common.ContentInfo;
import com.gaufoo.bbs.util.GsonUtils;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;

public class ContentSstRepository implements ContentRepository {
    private static final Gson gson = GsonUtils.interfaceGson(ContentElem.class);
    private final SST idToInfo;

    private ContentSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
    }

    @Override
    public ContentInfo get(ContentId contentId) {
        return SstUtils.getEntry(idToInfo, contentId.value, info -> gson.fromJson(info, ContentInfo.class));
    }

    @Override
    public boolean save(ContentId contentId, ContentInfo contentInfo) {
        if (SstUtils.contains(idToInfo, contentId.value)) return false;
        return SstUtils.setEntry(idToInfo, contentId.value, gson.toJson(contentInfo));
    }

    @Override
    public boolean delete(ContentId contentId) {
        return SstUtils.removeEntryByKey(idToInfo, contentId.value) != null;
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    public static ContentRepository get(Path storingPath) {
        return new ContentSstRepository(storingPath);
    }
}
