package com.gaufoo.bbs.components.found;

import com.gaufoo.bbs.components.found.common.FoundId;
import com.gaufoo.bbs.components.found.common.FoundInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;
import java.util.stream.Stream;

public interface Found {
    Stream<FoundId> allPosts(boolean descending);

    default Stream<FoundId> allPosts() {
        return allPosts(true);
    }

    Optional<FoundInfo> postInfo(FoundId foundId);

    Optional<FoundId> publishPost(FoundInfo foundInfo);

    Optional<FoundInfo> claim(FoundId foundId, String losterId);

    void removePost(FoundId foundId);

    Long allPostsCount();

    static Found defau1t(FoundRepository repository, IdGenerator idGenerator) {
        return new FoundImpl(repository, idGenerator);
    }
}
