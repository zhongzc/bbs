package com.gaufoo.bbs.components.found;

import com.gaufoo.bbs.components.found.common.FoundId;
import com.gaufoo.bbs.components.found.common.FoundInfo;

import java.util.stream.Stream;

public interface FoundRepository {

    Stream<FoundId> getAllPostsAsc();
    Stream<FoundId> getAllPostsDes();

    FoundInfo getPostInfo(FoundId postId);

    boolean savePost(FoundId postId, FoundInfo postInfo);

    boolean updatePost(FoundId postId, FoundInfo postInfo);

    boolean deletePost(FoundId postId);

    Long count();

    default void shutdown() {}

}
