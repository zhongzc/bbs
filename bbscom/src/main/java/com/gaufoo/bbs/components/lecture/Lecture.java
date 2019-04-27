package com.gaufoo.bbs.components.lecture;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lecture.common.LectureId;
import com.gaufoo.bbs.components.lecture.common.LectureInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface Lecture {
    Stream<LectureId> allPosts(boolean descending);

    default Stream<LectureId> allPosts() {
        return allPosts(false);
    }

    Stream<LectureId> allPostsTimeOrder(boolean descending);

    default Stream<LectureId> allPostsTimeOrder() {
        return allPostsTimeOrder(true);
    }

    Optional<LectureInfo> postInfo(LectureId lectureId);

    Optional<LectureId> publishPost(LectureInfo lectureInfo);

    boolean changePost(LectureId lectureId, LectureInfo lectureInfo);

    boolean removePost(LectureId lectureId);

    Long allPostsCount();

    static Lecture defau1t(LectureRepository repository, IdGenerator idGenerator) {
        return new LectureImpl(repository, idGenerator);
    }
}
