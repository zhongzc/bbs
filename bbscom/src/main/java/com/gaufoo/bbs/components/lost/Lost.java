package com.gaufoo.bbs.components.lost;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lost.common.LostId;
import com.gaufoo.bbs.components.lost.common.LostInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface Lost {
    Stream<LostId> allPosts(boolean descending);

    default Stream<LostId> allPosts() {
        return allPosts(true);
    }

    Optional<LostInfo> postInfo(LostId lostId);

    Optional<LostId> publishPost(LostInfo lostInfo);

    Optional<LostInfo> claim(LostId lostId, String founderId) ;

    boolean removePost(LostId lostId);

    Long allPostsCount();

    static Lost defau1t(LostRepository repository, IdGenerator idGenerator) {
        return new LostImpl(repository, idGenerator);
    }

}
