package com.gaufoo.bbs.components.lecture;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lecture.common.LectureId;
import com.gaufoo.bbs.components.lecture.common.LectureInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LectureImpl implements Lecture {
    private final LectureRepository repository;
    private final IdGenerator idGenerator;

    LectureImpl(LectureRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Stream<LectureId> allPosts(boolean descending) {
        if (descending) return repository.getAllPostsDes();
        else return repository.getAllPostsAsc();
    }

    @Override
    public Stream<LectureId> allPostsTimeOrder(boolean descending) {
        if (descending) return repository.getAllPostsTimeOrderDes();
        else return repository.getAllPostsTimeOrderAsc();
    }

    @Override
    public Optional<LectureInfo> postInfo(LectureId lectureId) {
        return Optional.ofNullable(repository.getPostInfo(lectureId));
    }

    @Override
    public Optional<LectureId> publishPost(LectureInfo lectureInfo) {
        LectureId id = LectureId.of(idGenerator.generateId());
        if (repository.savePost(id, lectureInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean removePost(LectureId lectureId) {
        return repository.deletePost(lectureId);
    }

    @Override
    public Long allPostsCount() {
        return repository.count();
    }

}
