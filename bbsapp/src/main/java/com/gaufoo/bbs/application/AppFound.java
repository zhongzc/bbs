package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostInfo;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.found.common.FoundId;
import com.gaufoo.bbs.components.found.common.FoundInfo;
import com.gaufoo.bbs.components.user.common.UserId;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.util.TaskChain.*;
import com.gaufoo.bbs.application.types.Found;


public class AppFound {
    private static Logger log = LoggerFactory.getLogger(AppFound.class);

    public static Found.AllFoundsResult allFounds(Long first, Long skip) {
        final long fFirst = first == null ? Long.MAX_VALUE : first;
        final long fSkip = skip == null ? 0L : skip;

        return new Found.MultiFoundInfos() {
            public Long getTotalCount() { return componentFactory.found.allPostsCount(); }

            public List<Found.FoundInfo> getFounds() {
                return componentFactory.found.allPosts()
                        .map(foundId -> componentFactory.found.postInfo(foundId)
                                .map(foundInfo -> consFoundInfo(foundId, foundInfo))
                                .orElse(null))
                        .filter(Objects::nonNull)
                        .skip(fFirst).limit(fSkip).collect(Collectors.toList());
            }
        };
    }

    public static Found.FoundInfoResult foundInfo(String foundIdStr) {
        FoundId foundId = FoundId.of(foundIdStr);
        return componentFactory.found.postInfo(foundId)
                .map(foundInfo -> (Found.FoundInfoResult)consFoundInfo(foundId, foundInfo))
                .orElse(Error.of(ErrorCode.FoundPostNonExist));
    }

    public static Found.CreateFoundResult createFound(String userToken, Found.FoundInput input) {

        return componentFactory.authenticator.getLoggedUser(UserToken.of(userToken))
                .then(permission -> Result.of(UserId.of(permission.userId)))
                .mapF(ErrorCode::fromAuthError)
                .then(userId -> addPictureIfNecessary(input.pictureBase64)
                        .then(oFileId -> {
                            String fileId = oFileId.map(x -> x.value).orElse(null);
                            Optional<FoundId> oFoundId = componentFactory.found.publishPost(
                                    FoundInfo.of(input.itemName, userId.value, input.description, input.position, fileId, input.contact, nilOrTr(input.foundTime, Instant::ofEpochMilli))
                            );
                            return Procedure.fromOptional(oFoundId, ErrorCode.PublishFoundFailed)
                                    .then(foundId -> Result.of(consFoundInfo(input, foundId, userId, nilOrTr(fileId, FileId::of))));
                })).reduce(Error::of, i -> i);
    }

    public static Found.DeleteFoundResult deleteFound(String foundId, String userToken) {
        return componentFactory.authenticator.getLoggedUser(UserToken.of(userToken))
                .then(permission -> Result.of(UserId.of(permission.userId)))
                .mapF(ErrorCode::fromAuthError)
                .then(userId -> ensureOwnPost(userId, FoundId.of(foundId)))
                .then(ig -> Result.of(componentFactory.found.removePost(FoundId.of(foundId))))
                .mapR(success -> success ? Ok.build() : Error.of(ErrorCode.DeleteFoundFailed))
                .reduce(Error::of, i -> i);
    }

    public static Found.ClaimFoundResult claimFound(String foundId, String userToken) {
        return componentFactory.authenticator.getLoggedUser(UserToken.of(userToken))
                .then(permission -> Result.of(UserId.of(permission.userId)))
                .mapF(ErrorCode::fromAuthError)
                .then(userId -> Result.of(componentFactory.found.claim(FoundId.of(foundId), userId.value)))
                .mapR(op -> op.isPresent() ? Ok.build() : Error.of(ErrorCode.ClaimFoundFailed))
                .reduce(Error::of, i -> i);
    }

    private static Found.FoundInfo consFoundInfo(FoundId foundId, FoundInfo foundInfo) {
        return new Found.FoundInfo() {
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
                        .reduce(AppFound::warnNil, r -> r);
            }
            public PersonalInformation.PersonalInfo getClaimer() {
                if (foundInfo.losterId == null) return null;
                return Commons.fetchPersonalInfo(UserId.of(foundInfo.losterId))
                        .reduce(AppFound::warnNil, r -> r);
            }
        };
    }

    private static Found.FoundInfo consFoundInfo(Found.FoundInput input, FoundId id, UserId publisherId, @Nullable FileId fileId) {
        return new Found.FoundInfo() {
            public String getId() { return id.value; }
            public PersonalInformation.PersonalInfo getPublisher() {
                return Commons.fetchPersonalInfo(publisherId).reduce(AppFound::warnNil, i -> i);
            }
            public String getName() { return input.itemName; }
            public String getDescription() { return input.description; }
            public String getPosition() { return input.position; }
            public String getPictureURL() {
                if (fileId == null) return null;
                return factorOutPictureUrl(fileId);
            }
            public String getContact() { return input.contact; }
            public Long getCreateTime() { return Instant.now().toEpochMilli(); }
            public Long getFoundTime() { return input.foundTime; }
            public PersonalInformation.PersonalInfo getClaimer() { return null; }
        };
    }

    private static Procedure<ErrorCode, Optional<FileId>> addPictureIfNecessary(@Nullable String pictureBase64) {
        if (pictureBase64 == null) return Result.of(Optional.empty());
        byte[] image = Base64.getDecoder().decode(pictureBase64);
        Optional<FileId> newPicId = componentFactory.lostFoundImages.createFile(image, UUID.randomUUID().toString());
        return Procedure.fromOptional(newPicId, ErrorCode.CreateFoundImageFailed)
                .then(fileId -> Result.of(Optional.of(fileId), () -> componentFactory.lostFoundImages.Remove(fileId)));
    }

    private static String factorOutPictureUrl(FileId fileId) {
        return Commons.fetchPictureUrl(componentFactory.lostFoundImages, StaticResourceConfig.FileType.LostFoundImage, fileId)
                .reduce(e -> null, i -> i);
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }

    private static <T, R> R nilOrTr(T obj, Function<T, R> transformer) {
        if (obj == null) return null;
        return transformer.apply(obj);
    }

    private static Procedure<ErrorCode, ?> ensureOwnPost(UserId userId, FoundId foundId) {
        return Procedure.fromOptional(componentFactory.found.postInfo(foundId), ErrorCode.FoundPostNonExist)
                .then(foundInfo -> foundInfo.publisherId.equals(userId.value) ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied));
    }
}
