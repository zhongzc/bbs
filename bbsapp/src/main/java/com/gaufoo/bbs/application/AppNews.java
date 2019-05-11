package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.Content;
import com.gaufoo.bbs.application.types.News;
import com.gaufoo.bbs.application.util.LazyVal;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.news.common.NewsId;
import com.gaufoo.bbs.components.news.common.NewsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.util.TaskChain.*;


public class AppNews {
    private static Logger log = LoggerFactory.getLogger(AppNews.class);
    private static final Consumer<ErrorCode> warnNil = errorCode -> log.warn("null warning: {}", errorCode);

    /**
     * 五条头条
     */
    public static News.MultiNewsInfos news() {
        return () -> componentFactory.news.allPosts().map(id -> consNewsInfo(id,
                LazyVal.of(() -> componentFactory.news.postInfo(id).orElse(null))))
                .limit(5L).collect(Collectors.toList());
    }

    public static News.NewsInfoResult newsInfo(String id) {
        NewsId newsId = NewsId.of(id);
        return componentFactory.news.postInfo(newsId)
                .map(info -> (News.NewsInfoResult) consNewsInfo(newsId, LazyVal.with(info)))
                .orElse(Error.of(ErrorCode.PostNonExist));
    }

    public static News.CreateNewsResult createNews(News.NewsInput input, String userToken) {
        final String group = Commons.getGroupId(Commons.PostType.News);
        return Commons.ensureAdmin(UserToken.of(userToken))
                .then(__ -> consNewsInfo(input))
                .then(info -> Procedure.fromOptional(componentFactory.news.publishPost(info), ErrorCode.CreatePostFailed)
                .mapR(id -> {
                    componentFactory.active.touch(group, id.value, null);
                    return consNewsInfo(id, LazyVal.with(info));
                })).reduce(Error::of, i -> i);
    }

    public static News.DeleteNewsResult deleteNews(String id, String userToken) {
        return Commons.ensureAdmin(UserToken.of(userToken))
                .then(__ -> clearNews(NewsId.of(id)))
                .reduce(Error::of, i -> i);
    }

    public static News.EditNewsResult editNews(String id, News.NewsInput input, String userToken) {
        NewsId newsId = NewsId.of(id);
        final String group = Commons.getGroupId(Commons.PostType.News);
        return Commons.ensureAdmin(UserToken.of(userToken))
                .then(__ -> modNewsInfo(newsId, input))
                .mapR(ok -> {
                    componentFactory.active.touch(group, id, null);
                    return consNewsInfo(newsId, LazyVal.of(() -> componentFactory.news.postInfo(newsId).orElse(null)));
                })
                .reduce(Error::of, i -> i);
    }

    private static Procedure<ErrorCode, Ok> clearNews(NewsId id) {
        final String group = Commons.getGroupId(Commons.PostType.News);
        return Procedure.fromOptional(componentFactory.news.postInfo(id), ErrorCode.PostNonExist)
                .mapR(info -> {
                    if (componentFactory.news.removePost(id) && componentFactory.active.remove(group, id.value)) {
                        AppContent.deleteContent(ContentId.of(info.contentId));
                        componentFactory.newsImages.remove(FileId.of(info.pictureId));
                        return true;
                    } else return false;
                }).then(ok -> ok ? Result.of(Ok.build()) : Fail.of(ErrorCode.DeletePostFailed));
    }

    private static News.NewsInfo consNewsInfo(NewsId id, LazyVal<NewsInfo> info) {
        final String group = Commons.getGroupId(Commons.PostType.News);
        return new News.NewsInfo() {
            public String getId() { return id.value; }
            public String getTitle() { return info.get().title; }
            public Content getContent() {
                return AppContent.fromContentId(ContentId.of(info.get().contentId)).reduce(AppNews::warnNil, i -> i);
            }
            public Long getPostTime() { return info.get().postTime.toEpochMilli(); }
            public Long getEditTime() {
                Optional<ActiveInfo> a = componentFactory.active.getLatestActiveInfo(group, id.value);
                return a.map(i -> i.time.toEpochMilli()).orElse(null);
            }
            public String getPictureURL() {
                return Commons.fetchFileUrlAndUnwrap(
                        componentFactory.newsImages,
                        StaticResourceConfig.FileType.NewsImages,
                        FileId.of(info.get().pictureId), warnNil);
            }
        };
    }

    private static Procedure<ErrorCode, NewsInfo> consNewsInfo(News.NewsInput input) {
        return AppContent.storeContentInput(input.content)
                .then(contentId -> Commons.storeBase64File(componentFactory.newsImages, input.pictureBase64)
                .mapR(fileId -> NewsInfo.of(input.title, contentId.value, fileId.value)));
    }

    private static Procedure<ErrorCode, Boolean> modNewsInfo(NewsId id, News.NewsInput input) {
        com.gaufoo.bbs.components.news.News n = componentFactory.news;
        Procedure<ErrorCode, NewsInfo> a = Procedure.fromOptional(componentFactory.news.postInfo(id), ErrorCode.PostNonExist)
                .then(info -> {
                    if (input.content != null) {
                        return AppContent.storeContentInput(input.content)
                                .mapR(contentId -> n.changeContentId(id, contentId.value))
                                .then(ok -> ok ? Result.of(info, () -> n.changeContentId(id, info.contentId)) : Fail.of(ErrorCode.ChangeNewsFailed));
                    } else return Result.of(info);
                })
                .then(info -> {
                    if (input.pictureBase64 != null) {
                        return Commons.storeBase64File(componentFactory.newsImages, input.pictureBase64)
                                .mapR(fileId -> n.changePictureId(id, fileId.value))
                                .then(ok -> ok ? Result.of(info, () -> n.changePictureId(id, info.pictureId)) : Fail.of(ErrorCode.ChangeNewsFailed));
                    } else return Result.of(info);
                })
                .then(info -> {
                    if (input.title != null) {
                        return Result.<ErrorCode, Boolean>of(n.changeTitle(id, input.title))
                                .then(ok -> ok ? Result.of(info, () -> n.changeTitle(id, info.title)) : Fail.of(ErrorCode.ChangeNewsFailed));
                    } else return Result.of(info);
                });

        return a.mapR(info -> {
            if (input.content != null) AppContent.deleteContent(ContentId.of(info.contentId));
            if (input.pictureBase64 != null) componentFactory.newsImages.remove(FileId.of(info.pictureId));
            return true;
        });
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }

    public static void reset() {
        componentFactory.news.allPosts().forEach(AppNews::clearNews);
    }
}
