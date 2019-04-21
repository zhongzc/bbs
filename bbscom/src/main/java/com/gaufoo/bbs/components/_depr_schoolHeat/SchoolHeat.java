package com.gaufoo.bbs.components._depr_schoolHeat;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostId;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostInfo;

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

    static SchoolHeat defau1t(SchoolHeatRepository schoolHeatRepository, IdGenerator idGenerator) {
        return SchoolHeatImpl.get(schoolHeatRepository, idGenerator);
    }
}
