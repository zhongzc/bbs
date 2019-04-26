package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.Found;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.found.common.FoundId;
import com.gaufoo.bbs.components.found.common.FoundInfo;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.TaskChain;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.util.TaskChain.*;


public class AppFound {
    private static Logger log = LoggerFactory.getLogger(AppFound.class);

    public static Found.AllFoundsResult allFounds(Long skip, Long first) {
        final long fSkip = skip == null ? 0L : skip;
        final long fFirst = first == null ? Long.MAX_VALUE : first;

        Supplier<List<Found.FoundInfo>> fs = () -> componentFactory.found.allPosts()
                .map(foundId -> componentFactory.found.postInfo(foundId)
                        .map(foundInfo -> consFoundInfo(foundId, foundInfo))
                        .orElse(null))
                .filter(Objects::nonNull)
                .skip(fSkip).limit(fFirst)
                .collect(Collectors.toList());

        return consMultiFoundsInfo(fs);
    }

    public static Found.FoundInfoResult foundInfo(String foundIdStr) {
        FoundId foundId = FoundId.of(foundIdStr);
        return componentFactory.found.postInfo(foundId)
                .map(foundInfo -> (Found.FoundInfoResult)consFoundInfo(foundId, foundInfo))
                .orElse(Error.of(ErrorCode.FoundPostNonExist));
    }

    public static Found.CreateFoundResult createFound(Found.FoundInput input, String userToken) {
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(userId -> addPictureIfNecessary(input.pictureBase64)
                        .mapR(oFileId -> oFileId.orElse(null))
                        .then(fileId -> publishFoundPost(input, userId, fileId)
                                .mapR(fndId -> consFoundInfo(input, fndId, userId, fileId))))
                .reduce(Error::of, i -> i);
    }

    public static Found.DeleteFoundResult deleteFound(String foundId, String userToken) {
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(userId -> ensureOwnPost(userId, FoundId.of(foundId)))
                .then(ig -> Result.of(componentFactory.found.removePost(FoundId.of(foundId))))
                .mapR(success -> success ? Ok.build() : Error.of(ErrorCode.DeleteFoundFailed))
                .reduce(Error::of, i -> i);
    }

    public static Found.ClaimFoundResult claimFound(String foundId, String userToken) {
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(userId -> Result.of(componentFactory.found.claim(FoundId.of(foundId), userId.value)))
                .reduce(Error::of, op -> op.isPresent() ? Ok.build() : Error.of(ErrorCode.ClaimFoundFailed));
    }

    public static Found.CancelClaimFoundResult cancelClaimFound(String foundId, String userToken) {
        Procedure<ErrorCode, UserId> userIdProc = Commons.fetchUserId(UserToken.of(userToken));
        Procedure<ErrorCode, FoundInfo> foundInfoProc = Procedure.fromOptional(
                componentFactory.found.postInfo(FoundId.of(foundId)),
                ErrorCode.FoundPostNonExist);

        return userIdProc.then(userId -> foundInfoProc
                .then(foundInfo -> Result.of(Objects.equals(foundInfo.losterId, userId.value)))
                .then(eq -> eq ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied))
                .then(continued -> Procedure.fromOptional(componentFactory.found.removeClaim(FoundId.of(foundId)), ErrorCode.CancelClaimFailed))
        ).reduce(Error::of, foundInfo -> Ok.build());
    }

    public static void reset() {
        componentFactory.found.allPosts().forEach(id ->
                componentFactory.found.removePost(id)
        );
    }

    private static Procedure<ErrorCode, FoundId> publishFoundPost(Found.FoundInput input, UserId publisher, @Nullable FileId pictureId) {
        return Procedure.fromOptional(componentFactory.found.publishPost(
                FoundInfo.of(input.itemName, publisher.value, input.description, input.position, nilOrTr(pictureId, x -> x.value),
                        input.contact, nilOrTr(input.foundTime, Instant::ofEpochMilli))
        ), ErrorCode.PublishFoundFailed);
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
                return Commons.fetchPersonalInfo(UserId.of(foundInfo.publisherId)).reduce(AppFound::warnNil, r -> r);
            }
            public PersonalInformation.PersonalInfo getClaimer() {
                if (foundInfo.losterId == null) return null;
                return Commons.fetchPersonalInfo(UserId.of(foundInfo.losterId)).reduce(AppFound::warnNil, r -> r);
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

    private static Found.MultiFoundInfos consMultiFoundsInfo(Supplier<List<Found.FoundInfo>> foundInfos) {
        return new Found.MultiFoundInfos() {
            public Long getTotalCount() { return componentFactory.found.allPostsCount(); }
            public List<Found.FoundInfo> getFounds() { return foundInfos.get(); }
        };
    }

    private static Procedure<ErrorCode, Optional<FileId>> addPictureIfNecessary(@Nullable String pictureBase64) {
        if (pictureBase64 == null) return Result.of(Optional.empty());
        byte[] image = Base64.getDecoder().decode(pictureBase64);
        Optional<FileId> newPicId = componentFactory.lostFoundImages.createFile(image);
        return Procedure.fromOptional(newPicId, ErrorCode.CreateFoundImageFailed)
                .then(fileId -> Result.of(Optional.of(fileId), () -> componentFactory.lostFoundImages.Remove(fileId)));
    }

    private static String factorOutPictureUrl(FileId fileId) {
        return Commons.fetchPictureUrl(componentFactory.lostFoundImages, StaticResourceConfig.FileType.LostFoundImage, fileId)
                .reduce(e -> null, i -> i);
    }

    private static Procedure<ErrorCode, ?> ensureOwnPost(UserId userId, FoundId foundId) {
        return Procedure.fromOptional(componentFactory.found.postInfo(foundId), ErrorCode.FoundPostNonExist)
                .then(foundInfo -> foundInfo.publisherId.equals(userId.value) ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied));
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }

    private static <T, R> R nilOrTr(T obj, Function<T, R> transformer) {
        if (obj == null) return null;
        else return transformer.apply(obj);
    }

}
