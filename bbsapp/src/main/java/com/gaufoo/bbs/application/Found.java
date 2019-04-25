package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.found.common.FoundId;
import com.gaufoo.bbs.components.user.common.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.types.Found.*;
import static com.gaufoo.bbs.util.TaskChain.Procedure;

public class Found {
    public static Logger log = LoggerFactory.getLogger(Found.class);

    public static AllFoundsResult allFounds(Long first, Long skip) {
        final long fFirst = first == null ? Long.MAX_VALUE : first;
        final long fSkip = skip == null ? 0L : skip;

        return new MultiFoundInfos() {
            public Long getTotalCount() { return componentFactory.found.allPostsCount(); }

            public List<FoundInfo> getFounds() {
                return componentFactory.found.allPosts()
                        .map(foundId -> componentFactory.found.postInfo(foundId)
                                .map(foundInfo -> consFoundInfo(foundId, foundInfo))
                                .orElse(null))
                        .filter(Objects::nonNull)
                        .skip(fFirst).limit(fSkip).collect(Collectors.toList());
            }
        };
    }

    public static FoundInfoResult foundInfo(String foundIdStr) {
        FoundId foundId = FoundId.of(foundIdStr);
        return componentFactory.found.postInfo(foundId)
                .map(foundInfo -> (FoundInfoResult)consFoundInfo(foundId, foundInfo))
                .orElse(Error.of(ErrorCode.FoundPostNonExist));
    }

    private static FoundInfo consFoundInfo(FoundId foundId, com.gaufoo.bbs.components.found.common.FoundInfo foundInfo) {
        return new FoundInfo() {
            public String getId()           { return foundId.value; }
            public String getName()         { return foundInfo.name; }
            public String getDescription()  { return foundInfo.description; }
            public String getPosition()     { return foundInfo.position; }
            public String getPictureURL()   { return factorOutPictureUrl(FileId.of(foundInfo.pictureId)); }
            public String getContact()      { return foundInfo.contact; }
            public Long getCreateTime()     { return foundInfo.createTime.toEpochMilli(); }
            public Long getFoundTime()      { return foundInfo.foundTime.toEpochMilli(); }
            public PersonalInformation.PersonalInfo getPublisher() {
                return Commons.fetchPersonalInfo(UserId.of(foundInfo.publisherId))
                        .reduce(e -> {
                            log.warn("Cant fetch user for: {}, cause: {}", foundInfo, e);
                            return null;
                        }, r -> r);
            }
            public PersonalInformation.PersonalInfo getClaimer() {
                if (foundInfo.losterId == null) return null;
                return Commons.fetchPersonalInfo(UserId.of(foundInfo.losterId))
                        .reduce(e -> {
                            log.warn("Cant fetch user for: {}, cause: {}", foundInfo, e);
                            return null;
                        }, r -> r);
            }
        };
    }


    private static String factorOutPictureUrl(FileId fileId) {
        return Commons.fetchPictureUrl(componentFactory.lostFoundImages, StaticResourceConfig.FileType.LostFoundImage, fileId)
                .reduce(e -> null, i -> i);
    }
}
