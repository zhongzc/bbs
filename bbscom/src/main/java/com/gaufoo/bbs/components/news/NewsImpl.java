package com.gaufoo.bbs.components.news;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.news.common.NewsId;
import com.gaufoo.bbs.components.news.common.NewsInfo;

import java.util.Optional;
import java.util.stream.Stream;

public class NewsImpl implements News {
    private final NewsRepository repository;
    private final IdGenerator idGenerator;

    NewsImpl(NewsRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Stream<NewsId> allPosts(boolean descending) {
        if (descending) return repository.getAllPostsDes();
        return repository.getAllPostsAsc();
    }

    @Override
    public Optional<NewsInfo> postInfo(NewsId newsId) {
        return Optional.ofNullable(repository.getPostInfo(newsId));
    }

    @Override
    public Optional<NewsId> publishPost(NewsInfo newsInfo) {
        NewsId id = NewsId.of(idGenerator.generateId());
        if (repository.savePost(id, newsInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean changeTitle(NewsId newsId, String title) {
        return postInfo(newsId).map(i -> repository.updatePost(newsId, i.modTitle(title))).orElse(false);
    }

    @Override
    public boolean changeContentId(NewsId newsId, String contentId) {
        return postInfo(newsId).map(i -> repository.updatePost(newsId, i.modContentId(contentId))).orElse(false);

    }

    @Override
    public boolean changePictureId(NewsId newsId, String pictureId) {
        return postInfo(newsId).map(i -> repository.updatePost(newsId, i.modPictureId(pictureId))).orElse(false);
    }

    @Override
    public boolean removePost(NewsId newsId) {
        return repository.deletePost(newsId);
    }

    @Override
    public Long allPostsCount() {
        return repository.count();
    }
}
