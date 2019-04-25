package com.gaufoo.bbs.components.news;

import com.gaufoo.bbs.components.news.common.NewsId;
import com.gaufoo.bbs.components.news.common.NewsInfo;

import java.util.stream.Stream;

public interface NewsRepository {

    Stream<NewsId> getAllPostsAsc();
    Stream<NewsId> getAllPostsDes();

    NewsInfo getPostInfo(NewsId newsId);

    boolean savePost(NewsId newsId, NewsInfo newsInfo);

    boolean updatePost(NewsId newsId, NewsInfo newsInfo);

    boolean deletePost(NewsId newsId);

    Long count();

    default void shutdown() {}
}
