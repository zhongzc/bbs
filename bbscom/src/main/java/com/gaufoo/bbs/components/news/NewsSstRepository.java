package com.gaufoo.bbs.components.news;

import com.gaufoo.bbs.components.news.common.NewsId;
import com.gaufoo.bbs.components.news.common.NewsInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class NewsSstRepository implements NewsRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;
    private final AtomicLong count;

    private NewsSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
        this.count = new AtomicLong(getAllPostsAsc().count());
    }

    @Override
    public Stream<NewsId> getAllPostsAsc() {
        return SstUtils.waitFuture(idToInfo.allKeysAsc()).map(i -> i.map(NewsId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<NewsId> getAllPostsDes() {
        return SstUtils.waitFuture(idToInfo.allKeysDes()).map(i -> i.map(NewsId::of)).orElse(Stream.empty());
    }

    @Override
    public NewsInfo getPostInfo(NewsId newsId) {
        return SstUtils.getEntry(idToInfo, newsId.value, info -> gson.fromJson(info, NewsInfo.class));
    }

    @Override
    public boolean savePost(NewsId newsId, NewsInfo newsInfo) {
        if (SstUtils.contains(idToInfo, newsId.value)) return false;
        if (SstUtils.setEntry(idToInfo, newsId.value, gson.toJson(newsInfo))) {
            this.count.incrementAndGet();
            return true;
        } else return false;
    }

    @Override
    public boolean updatePost(NewsId newsId, NewsInfo newsInfo) {
        if (!SstUtils.contains(idToInfo, newsId.value)) return false;
        return SstUtils.setEntry(idToInfo, newsId.value, gson.toJson(newsInfo));
    }

    @Override
    public boolean deletePost(NewsId newsId) {
        if (SstUtils.removeEntryByKey(idToInfo, newsId.value) != null) {
            this.count.decrementAndGet();
            return true;
        } else return false;
    }

    @Override
    public Long count() {
        return this.count.get();
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    public static NewsRepository get(Path storingPath) {
        return new NewsSstRepository(storingPath);
    }
}
