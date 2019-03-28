package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.like.LikeComponentRepository;
import com.gaufoo.bbs.components.like.common.LikeId;
import com.gaufoo.bbs.components.like.common.LikeInfo;

import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Stream;

public class LikeComponentMemoryRepository implements LikeComponentRepository {
    private final String repositoryName;
    private final Map<String, LikeInfo> map = new Hashtable<>();

    private LikeComponentMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveLike(LikeId likeId, LikeInfo likeInfo) {
        if (map.containsKey(likeId.value)) return false;
        map.put(likeId.value, likeInfo);
        return true;
    }

    @Override
    public LikeInfo getLikeInfo(LikeId id) {
        return map.get(id.value);
    }

    @Override
    public Stream<LikeId> getAllLike() {
        return map.keySet().stream().map(LikeId::of);
    }

    @Override
    public void removeLike(LikeId id) {
        map.remove(id.value);
    }

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static LikeComponentRepository get(String repositoryName) {
        return new LikeComponentMemoryRepository(repositoryName);
    }
}
