package com.gaufoo.bbs.components.learningResource;

import com.gaufoo.bbs.components.learningResource.common.LearningResourceId;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceInfo;

import java.util.stream.Stream;

public interface LearningResourceRepository {

    Stream<LearningResourceId> getAllPostsAsc();
    Stream<LearningResourceId> getAllPostsDes();
    Stream<LearningResourceId> getAllPostsByAuthorAsc(String authorId);
    Stream<LearningResourceId> getAllPostsByAuthorDes(String authorId);
    Stream<LearningResourceId> getAllPostsOfCourseAsc(String courseCode);
    Stream<LearningResourceId> getAllPostsOfCourseDes(String courseCode);

    LearningResourceInfo getPostInfo(LearningResourceId postId);

    boolean savePostInfo(LearningResourceId postId, LearningResourceInfo postInfo);

    void deletePostInfo(LearningResourceId postId);

    default void shutdown() {}
}
