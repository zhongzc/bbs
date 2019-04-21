package com.gaufoo.bbs.components._depr_learningResource;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceId;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceInfo;

import java.util.Optional;
import java.util.stream.Stream;

class LearningResourceImpl implements LearningResource {
    private final LearningResourceRepository repository;
    private final IdGenerator idGenerator;

    LearningResourceImpl(LearningResourceRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<ResourceId> pubResource(ResourceInfo resourceInfo) {
        ResourceId id = ResourceId.of(idGenerator.generateId());

        if (repository.saveResource(id, resourceInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ResourceInfo> resourceInfo(ResourceId resourceId) {
        return Optional.ofNullable(repository.getResourceInfo(resourceId));
    }

    @Override
    public Stream<ResourceId> allResources() {
        return repository.getAllResources();
    }

    @Override
    public boolean changeSharer(ResourceId resourceId, String newSharer) {
        return Optional.ofNullable(repository.getResourceInfo(resourceId)).map(u ->
                repository.updateResource(resourceId, u.modSharer(newSharer))
        ).orElse(false);
    }

    @Override
    public boolean changeMajorCode(ResourceId resourceId, String newMajorCode) {
        return Optional.ofNullable(repository.getResourceInfo(resourceId)).map(u ->
                repository.updateResource(resourceId, u.modMajorCode(newMajorCode))
        ).orElse(false);
    }

    @Override
    public boolean changeTitle(ResourceId resourceId, String newTitle) {
        return Optional.ofNullable(repository.getResourceInfo(resourceId)).map(u ->
                repository.updateResource(resourceId, u.modTitle(newTitle))
        ).orElse(false);
    }

    @Override
    public boolean changeContent(ResourceId resourceId, String newContent) {
        return Optional.ofNullable(repository.getResourceInfo(resourceId)).map(u ->
                repository.updateResource(resourceId, u.modContent(newContent))
        ).orElse(false);
    }

    @Override
    public boolean changeAttachedFileIdentifier(ResourceId resourceId, String newAttachedFileIdentifier) {
        return Optional.ofNullable(repository.getResourceInfo(resourceId)).map(u ->
                repository.updateResource(resourceId, u.modAttachedFileIdentifier(newAttachedFileIdentifier))
        ).orElse(false);
    }

    @Override
    public void removeResource(ResourceId resourceId) {
        repository.deleteResource(resourceId);
    }

}
