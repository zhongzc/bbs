package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.schoolHeat.common.PostId;
import com.gaufoo.bbs.components.schoolHeat.common.PostInfo;

import java.util.Comparator;
import java.util.stream.Stream;

public interface HeatRepository {
    Stream<PostId> getAllPosts(Comparator<PostInfo> comparator);

    Stream<PostId> allPostsByTimeAsc();
    Stream<PostId> allPostsByTimeDes();
    Stream<PostId> allPostsByHeatAsc();
    Stream<PostId> allPostsByHeatDes();

    PostInfo getPostInfo(PostId postId);

    boolean savePostInfo(PostId postId, PostInfo postInfo);

    void deletePostInfo(PostId postId);

    void updatePostInfo(PostId postId, PostInfo postInfo);

    default void shutdown() {}

    String getRepositoryName();
}