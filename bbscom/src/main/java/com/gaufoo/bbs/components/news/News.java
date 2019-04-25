package com.gaufoo.bbs.components.news;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.news.common.NewsId;
import com.gaufoo.bbs.components.news.common.NewsInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface News {
    Stream<NewsId> allPosts(boolean descending);

    default Stream<NewsId> allPosts() {
        return allPosts(true);
    }

    Optional<NewsInfo> postInfo(NewsId newsId);

    Optional<NewsId> publishPost(NewsInfo newsInfo);

    boolean changeTitle(NewsId newsId, String title);

    boolean changeContentId(NewsId newsId, String contentId);

    boolean changePictureId(NewsId newsId, String pictureId);

    boolean removePost(NewsId newsId);

    Long allPostsCount();

    static News defau1t(NewsRepository repository, IdGenerator idGenerator) {
        return new NewsImpl(repository, idGenerator);
    }
}
