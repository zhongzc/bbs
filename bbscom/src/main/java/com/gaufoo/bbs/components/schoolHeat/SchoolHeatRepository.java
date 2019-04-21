package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;

import java.util.stream.Stream;

public interface SchoolHeatRepository {

    Stream<SchoolHeatId> getAllPostsAsc();
    Stream<SchoolHeatId> getAllPostsDes();
    Stream<SchoolHeatId> getAllPostsByAuthorAsc(String authorId);
    Stream<SchoolHeatId> getAllPostsByAuthorDes(String authorId);

    SchoolHeatInfo getPostInfo(SchoolHeatId postId);

    boolean savePost(SchoolHeatId postId, SchoolHeatInfo postInfo);

    void deletePost(SchoolHeatId postId);

    default void shutdown() {}
}
