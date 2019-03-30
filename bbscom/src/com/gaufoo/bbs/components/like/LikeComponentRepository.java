package com.gaufoo.bbs.components.like;

import com.gaufoo.bbs.components.like.common.LikeId;
import com.gaufoo.bbs.components.like.common.LikeInfo;

import java.util.stream.Stream;

public interface LikeComponentRepository {

    boolean saveLike(LikeId likeId, LikeInfo likeInfo);

    LikeInfo getLikeInfo(LikeId id);

    Stream<LikeId> getAllLike();

    void removeLike(LikeId id);

    String getRepositoryName();
}
