package com.gaufoo.bbs.components.like;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.like.common.LikeId;
import com.gaufoo.bbs.components.like.common.LikeInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface LikeComponent {
    Optional<LikeId> initLike(String obj);

    boolean like(LikeId likee, String liker);

    boolean cancelLike(LikeId likee, String liker);

    boolean dislike(LikeId likee, String disliker);

    Optional<LikeInfo> likeInfo(LikeId likeId);

    Optional<Integer> likeValue(LikeId likeId, LikeCalc calc);

    Stream<LikeId> allLikes();

    void remove(LikeId likeId);

    void shutdown();

    String getName();

    static LikeComponent defau1t(String componentName, LikeComponentRepository repository, IdGenerator idGenerator) {
        return new LikeComponentImpl(componentName, repository, idGenerator);
    }

    interface LikeCalc {
        int apply(int likerNum, int diskerNum);
    }
}
