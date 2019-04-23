package com.gaufoo.bbs.components.learningResource;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceId;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface LearningResource {
    Stream<LearningResourceId> allPosts(boolean descending);

    default Stream<LearningResourceId> allPosts() {
        return allPosts(false);
    }

    Stream<LearningResourceId> allPostsByAuthor(String authorId, boolean descending);

    default Stream<LearningResourceId> allPostsByAuhtor(String authorId) {
        return allPostsByAuthor(authorId, true);
    }

    Stream<LearningResourceId> allPostsOfCourse(String courseCode, boolean descending);

    default Stream<LearningResourceId> allPostsOfCourse(String courseCode) {
        return allPostsOfCourse(courseCode, true);
    }

    Optional<LearningResourceInfo> postInfo(LearningResourceId learningResourceId);

    Optional<LearningResourceId> publishPost(LearningResourceInfo learningResourceInfo);

    boolean removePost(LearningResourceId learningResourceId);

    Long allPostsCount();

    static LearningResource defau1t(LearningResourceRepository repository, IdGenerator idGenerator) {
        return new LearningResourceImpl(repository, idGenerator);
    }
}