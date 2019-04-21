package com.gaufoo.bbs.components._depr_learningResource;

import com.gaufoo.bbs.components._depr_learningResource.common.ResourceId;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceInfo;

import java.util.stream.Stream;

public interface LearningResourceRepository {
    boolean saveResource(ResourceId id, ResourceInfo resourceInfo);

    boolean updateResource(ResourceId resourceId, ResourceInfo modSharer);

    ResourceInfo getResourceInfo(ResourceId resourceId);

    Stream<ResourceId> getAllResources();

    void deleteResource(ResourceId resourceId);

    default void shutdown() {}
}
