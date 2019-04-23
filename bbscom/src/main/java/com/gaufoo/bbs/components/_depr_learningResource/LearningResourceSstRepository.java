package com.gaufoo.bbs.components._depr_learningResource;

import com.gaufoo.bbs.components._depr_learningResource.common.ResourceId;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceInfo;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.stream.Stream;

import static com.gaufoo.bbs.util.SstUtils.*;

public class LearningResourceSstRepository implements LearningResourceRepository {
    private static Gson gson = new Gson();
    private final SST idToInfo;

    private LearningResourceSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
    }

    @Override
    public boolean saveResource(ResourceId id, ResourceInfo resourceInfo) {
        return setEntry(idToInfo, id.value, gson.toJson(resourceInfo));
    }

    @Override
    public boolean updateResource(ResourceId resourceId, ResourceInfo modSharer) {
        return setEntry(idToInfo, resourceId.value, gson.toJson(modSharer));
    }

    @Override
    public ResourceInfo getResourceInfo(ResourceId resourceId) {
        return getEntry(idToInfo, resourceId.value, json -> gson.fromJson(json, ResourceInfo.class));
    }

    @Override
    public Stream<ResourceId> getAllResources() {
        return waitFuture(idToInfo.allKeysAsc()
                .thenApply(stringStream -> stringStream
                        .map(ResourceId::of))
        ).orElse(Stream.empty());
    }

    @Override
    public void deleteResource(ResourceId resourceId) {
        removeEntryByKey(idToInfo, resourceId.value);
    }

    @Override
    public void shutdown() {
        waitFuture(idToInfo.shutdown());
    }

    public static LearningResourceSstRepository get(Path path) {
        return new LearningResourceSstRepository(path);
    }
}
