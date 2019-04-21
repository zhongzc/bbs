package com.gaufoo.bbs.components._depr_learningResource;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceId;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface LearningResource {
    Optional<ResourceId> pubResource(ResourceInfo resourceInfo);

    Optional<ResourceInfo> resourceInfo(ResourceId resourceId);

    Stream<ResourceId> allResources();

    default Stream<ResourceId> resourcesOfMajor(String majorCode) {
        return allResources().filter(r ->
                resourceInfo(r).map(ri -> ri.majorCode.equals(majorCode)).orElse(false));
    }

    boolean changeSharer(ResourceId resourceId, String newSharer);

    boolean changeMajorCode(ResourceId resourceId, String newMajorCode);

    boolean changeTitle(ResourceId resourceId, String newTitle);

    boolean changeContent(ResourceId resourceId, String newContent);

    boolean changeAttachedFileIdentifier(ResourceId resourceId, String newAttachedFileIdentifier);

    default Stream<ResourceId> searchResourcesByTitle(String title) {
        return allResources().filter(r ->
                resourceInfo(r).map(ri -> ri.title.matches(".*" + title + ".*")).orElse(false));
    }

    default Stream<ResourceId> searchResourcesByContent(String content) {
        return allResources().filter(r ->
                resourceInfo(r).map(ri -> ri.content.matches(".*" + content + ".*")).orElse(false));
    }

    void removeResource(ResourceId resourceId);

    static LearningResource defau1t(LearningResourceRepository repository, IdGenerator idGenerator) {
        return new LearningResourceImpl(repository, idGenerator);
    }
}
