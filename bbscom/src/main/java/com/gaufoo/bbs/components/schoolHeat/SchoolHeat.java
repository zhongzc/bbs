package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.schoolHeat.common.PostId;
import com.gaufoo.bbs.components.schoolHeat.common.PostInfo;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public interface SchoolHeat {
    // use comparators in common.PostComparators
    // fixme: not easy to use correctly
    Stream<PostId> allPosts(Comparator<PostInfo> comparator);

    Optional<PostInfo> postInfo(PostId postId);

    Optional<PostId> publishPost(PostInfo postInfo);

    void removePost(PostId postId);

    void increaseHeat(PostId postId, int delta);

    void setLatestCommenter(PostId postId, String replier);

    Long allPostsCount();

    void addComment(PostId postId, String commentIdentifier);

    void removeComment(PostId postId, String commentIdentifier);

    void updatePost(PostId postId, PostInfo modPostInfo);

    void shutdown();

    String getName();

    static SchoolHeat defau1t(String componentName, SchoolHeatRepository schoolHeatRepository, IdGenerator idGenerator) {
        return SchoolHeatImpl.get(componentName, schoolHeatRepository, idGenerator);
    }
}
