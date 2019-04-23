package com.gaufoo.bbs.components.lost;

import com.gaufoo.bbs.components.lost.common.LostId;
import com.gaufoo.bbs.components.lost.common.LostInfo;

import java.util.stream.Stream;

public interface LostRepository {

    Stream<LostId> getAllPostsAsc();
    Stream<LostId> getAllPostsDes();

    LostInfo getPostInfo(LostId postId);

    boolean savePost(LostId postId, LostInfo postInfo);

    boolean updatePost(LostId postId, LostInfo postInfo);

    boolean deletePost(LostId postId);

    Long count();

    default void shutdown() {}

}
