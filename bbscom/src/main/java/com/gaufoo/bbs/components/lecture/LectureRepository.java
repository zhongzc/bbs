package com.gaufoo.bbs.components.lecture;

import com.gaufoo.bbs.components.lecture.common.LectureId;
import com.gaufoo.bbs.components.lecture.common.LectureInfo;

import java.util.stream.Stream;

public interface LectureRepository {

    Stream<LectureId> getAllPostsAsc();
    Stream<LectureId> getAllPostsDes();
    Stream<LectureId> getAllPostsTimeOrderAsc();
    Stream<LectureId> getAllPostsTimeOrderDes();

    LectureInfo getPostInfo(LectureId postId);

    boolean savePost(LectureId postId, LectureInfo postInfo);

    boolean deletePost(LectureId postId);

    Long count();

    default void shutdown() {}

}
