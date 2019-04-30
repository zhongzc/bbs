package com.gaufoo.bbs.components.learningResource;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceId;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LearningResourceImpl implements LearningResource {
    private final LearningResourceRepository repository;
    private final IdGenerator idGenerator;

    LearningResourceImpl(LearningResourceRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Stream<LearningResourceId> allPosts(boolean descending) {
        if (descending) return repository.getAllPostsDes();
        else return repository.getAllPostsAsc();
    }

    @Override
    public Stream<LearningResourceId> allPostsByAuthor(String authorId, boolean descending) {
        if (descending) return repository.getAllPostsByAuthorDes(authorId);
        else return repository.getAllPostsByAuthorAsc(authorId);
    }

    @Override
    public Stream<LearningResourceId> allPostsOfCourse(String courseCode, boolean descending) {
        if (descending) return repository.getAllPostsOfCourseDes(courseCode);
        else return repository.getAllPostsOfCourseAsc(courseCode);
    }

    @Override
    public Optional<LearningResourceInfo> postInfo(LearningResourceId learningResourceId) {
        return Optional.ofNullable(repository.getPostInfo(learningResourceId));
    }

    @Override
    public Optional<LearningResourceId> publishPost(LearningResourceInfo learningResourceInfo) {
        LearningResourceId id = LearningResourceId.of(idGenerator.generateId());
        if (repository.savePostInfo(id, learningResourceInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean removePost(LearningResourceId learningResourceId) {
        return repository.deletePostInfo(learningResourceId);
    }

    @Override
    public Long allPostsCount() {
        return repository.count();
    }

    @Override
    public Long allPostsCountOfCourse(String courseCode) {
        return repository.countOfCourse(courseCode);
    }

    @Override
    public Long allPostsCountOfAuthor(String authorId) {
        return repository.countOfAuthor(authorId);
    }
}
