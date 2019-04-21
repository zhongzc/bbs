package com.gaufoo.bbs.components._depr_like;

import com.gaufoo.bbs.components._depr_like.common.LikeId;
import com.gaufoo.bbs.components._depr_like.common.LikeInfo;

import java.util.stream.Stream;

public interface LikeComponentRepository {

    boolean saveLike(LikeId likeId, LikeInfo likeInfo);

    boolean updateLike(LikeId likeId, LikeInfo likeInfo);

    LikeInfo getLikeInfo(LikeId id);

    Stream<LikeId> getAllLike();

    void removeLike(LikeId id);

    default void shutdown() {}

    String getRepositoryName();
}
