package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components._depr_learningResource.LearningResourceRepository;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceId;
import com.gaufoo.bbs.components._depr_learningResource.common.ResourceInfo;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class LearningResourceMemoryRepository implements LearningResourceRepository {
    private final Gson gson = new Gson();
    private final String repositoryName;
    private final Map<String, String> resources = new ConcurrentHashMap<>();

    private LearningResourceMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveResource(ResourceId id, ResourceInfo resourceInfo) {
        if (resources.containsKey(id.value)) return false;
        resources.put(id.value, gson.toJson(resourceInfo));
        return true;
    }

    @Override
    public boolean updateResource(ResourceId resourceId, ResourceInfo modSharer) {
        if (!resources.containsKey(resourceId.value)) return false;
        resources.put(resourceId.value, gson.toJson(modSharer));
        return true;
    }

    @Override
    public ResourceInfo getResourceInfo(ResourceId resourceId) {
        return gson.fromJson(resources.get(resourceId.value), ResourceInfo.class);
    }

    @Override
    public Stream<ResourceId> getAllResources() {
        return resources.keySet().stream().map(ResourceId::of);
    }

    @Override
    public void deleteResource(ResourceId resourceId) {
        resources.remove(resourceId.value);
    }

    public static LearningResourceRepository get(String repositoryName) {
        return new LearningResourceMemoryRepository(repositoryName);
    }

}
