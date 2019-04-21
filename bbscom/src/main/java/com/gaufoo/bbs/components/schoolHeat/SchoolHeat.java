package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface SchoolHeat {
    Stream<SchoolHeatId> allPosts(boolean descending);

    default Stream<SchoolHeatId> allPosts() {
        return allPosts(false);
    }

    Stream<SchoolHeatId> allPostsByAuthor(String authorId, boolean descending);

    default Stream<SchoolHeatId> allPostsByAuthor(String authorId) {
        return allPostsByAuthor(authorId, true);
    }

    Optional<SchoolHeatInfo> postInfo(SchoolHeatId schoolHeatId);

    Optional<SchoolHeatId> publishPost(SchoolHeatInfo schoolHeatInfo);

    void removePost(SchoolHeatId schoolHeatId);

    Long allPostsCount();

    static SchoolHeat defau1t(SchoolHeatRepository repository, IdGenerator idGenerator) {
        return new SchoolHeatImpl(repository, idGenerator);
    }
}
