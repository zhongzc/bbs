package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.Content;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.content.common.*;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.util.TaskChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class AppContent {
    private static Logger log = LoggerFactory.getLogger(AppContent.class);

    public static TaskChain.Procedure<ErrorCode, ContentInfo> consContent(Content.ContentInput contentInput) {
        Stream<TaskChain.Procedure<ErrorCode, ContentElem>> items = contentInput.elems.stream().map(i -> {
            if (i.type == Content.ElemType.Picture) {
                Optional<FileId> fileId = componentFactory.contentImages.createFile(Base64.getDecoder().decode(i.str));
                return TaskChain.Procedure.fromOptional(fileId, ErrorCode.SaveFileFailed)
                        .then(fid -> TaskChain.Result.of(ContentFig.of(fid.value), () -> componentFactory.contentImages.Remove(fid)));
            } else if (i.type == Content.ElemType.Text) {
                return TaskChain.Result.of(ContentParag.of(i.str));
            } else {
                return TaskChain.Fail.of(ErrorCode.UnsupportedOperation);
            }
        });

        return TaskChain.Procedure.sequence(items.collect(Collectors.toList())).mapR(ContentInfo::of);
    }

    public static Content fromContentInfo(ContentInfo contentInfo) {
        return () -> contentInfo.elems.stream().map(e -> {
            if (e instanceof ContentFig) {
                return (Content.Picture) () ->
                        Commons.fetchPictureUrl(componentFactory.contentImages, StaticResourceConfig.FileType.ContentImages,
                                FileId.of(((ContentFig) e).figureId)).reduce(AppContent::warnNil, i -> i);
            } else {
                return (Content.Paragraph) () -> ((ContentParag) e).paragraph;
            }
        }).collect(Collectors.toList());
    }

    public static TaskChain.Procedure<ErrorCode, Content> fromContentId(ContentId contentId) {
        return TaskChain.Procedure.fromOptional(componentFactory.content.contentInfo(contentId), ErrorCode.ContentNonExit)
                .mapR(AppContent::fromContentInfo);
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }
}
