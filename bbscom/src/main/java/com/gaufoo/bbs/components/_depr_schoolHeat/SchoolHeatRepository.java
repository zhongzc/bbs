package com.gaufoo.bbs.components._depr_schoolHeat;

import com.gaufoo.bbs.components._depr_schoolHeat.common.PostId;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostInfo;

import java.util.Comparator;
import java.util.stream.Stream;

public interface SchoolHeatRepository {
    Stream<PostId> getAllPosts(Comparator<PostInfo> comparator);
    Stream<PostId> getAllPosts();

    Stream<PostId> allPostsByTimeAsc();
    Stream<PostId> allPostsByTimeDes();
    Stream<PostId> allPostsByHeatAsc();
    Stream<PostId> allPostsByHeatDes();

    PostInfo getPostInfo(PostId postId);

    boolean savePostInfo(PostId postId, PostInfo postInfo);

    void deletePostInfo(PostId postId);

    boolean updatePostInfo(PostId postId, PostInfo postInfo);

    default void shutdown() {}
}
