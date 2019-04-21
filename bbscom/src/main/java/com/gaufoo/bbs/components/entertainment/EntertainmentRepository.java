package com.gaufoo.bbs.components.entertainment;

import com.gaufoo.bbs.components.entertainment.common.EntertainmentId;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentInfo;

import java.util.stream.Stream;

public interface EntertainmentRepository {

    Stream<EntertainmentId> getAllPostsAsc();
    Stream<EntertainmentId> getAllPostsDes();
    Stream<EntertainmentId> getAllPostsByAuthorAsc(String authorId);
    Stream<EntertainmentId> getAllPostsByAuthorDes(String authorId);

    EntertainmentInfo getPostInfo(EntertainmentId postId);

    boolean savePost(EntertainmentId postId, EntertainmentInfo postInfo);

    void deletePost(EntertainmentId postId);

    default void shutdown() {}

}
