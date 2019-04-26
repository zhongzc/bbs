package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.Lost;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.LazyVal;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.lost.common.LostId;
import com.gaufoo.bbs.components.lost.common.LostInfo;
import com.gaufoo.bbs.components.user.common.UserId;
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

public class AppLost {
    private static Logger log = LoggerFactory.getLogger(AppLost.class);

    public static Lost.AllLostsResult allLosts(Long skip, Long first) {
        final long fSkip = skip == null ? 0L : skip;
        final long fFirst = first == null ? Long.MAX_VALUE : first;

        Supplier<List<Lost.LostInfo>> fs = () -> componentFactory.lost.allPosts()
                .map(lostId -> consLostInfo(lostId, LazyVal.of(() -> componentFactory.lost.postInfo(lostId).orElse(null))))
                .skip(fSkip).limit(fFirst)
                .collect(Collectors.toList());

        return consMultiLostsInfo(fs);
    }

    public static Lost.LostInfoResult lostInfo(String lostIdStr) {
        LostId lostId = LostId.of(lostIdStr);
        return componentFactory.lost.postInfo(lostId)
                .map(lostInfo -> (Lost.LostInfoResult) consLostInfo(lostId, LazyVal.with(lostInfo)))
                .orElse(Error.of(ErrorCode.LostPostNonExist));
    }

    public static Lost.CreateLostResult createLost(Lost.LostInput input, String userToken) {
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(userId -> addPictureIfNecessary(input.pictureBase64)
                        .mapR(oFileId -> oFileId.orElse(null))
                        .then(fileId -> publishLostPost(input, userId, fileId)
                                .mapR(fndId -> consLostInfo(input, fndId, userId, fileId))))
                .reduce(Error::of, i -> i);
    }

    public static Lost.DeleteLostResult deleteLost(String lostId, String userToken) {
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(userId -> ensureOwnPost(userId, LostId.of(lostId)))
                .then(ig -> Result.of(componentFactory.lost.removePost(LostId.of(lostId))))
                .mapR(success -> success ? Ok.build() : Error.of(ErrorCode.DeleteLostFailed))
                .reduce(Error::of, i -> i);
    }

    public static Lost.ClaimLostResult claimLost(String lostId, String userToken) {
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(userId -> Result.of(componentFactory.lost.claim(LostId.of(lostId), userId.value)))
                .reduce(Error::of, op -> op.isPresent() ? Ok.build() : Error.of(ErrorCode.ClaimLostFailed));
    }

    public static Lost.CancelClaimLostResult cancelClaimLost(String lostId, String userToken) {
        Procedure<ErrorCode, UserId> userIdProc = Commons.fetchUserId(UserToken.of(userToken));
        Procedure<ErrorCode, LostInfo> lostInfoProc = Procedure.fromOptional(
                componentFactory.lost.postInfo(LostId.of(lostId)),
                ErrorCode.LostPostNonExist);

        return userIdProc.then(userId -> lostInfoProc
                .then(lostInfo -> Result.of(Objects.equals(lostInfo.founderId, userId.value)))
                .then(eq -> eq ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied))
                .then(continued -> Procedure.fromOptional(componentFactory.lost.removeClaim(LostId.of(lostId)), ErrorCode.CancelClaimFailed))
        ).reduce(Error::of, lostInfo -> Ok.build());
    }

    public static void reset() {
        componentFactory.lost.allPosts().forEach(id ->
                componentFactory.lost.removePost(id)
        );
    }

    private static Procedure<ErrorCode, LostId> publishLostPost(Lost.LostInput input, UserId publisher, @Nullable FileId pictureId) {
        return Procedure.fromOptional(componentFactory.lost.publishPost(
                LostInfo.of(input.itemName, publisher.value, input.description, input.position, nilOrTr(pictureId, x -> x.value),
                        input.contact, nilOrTr(input.lostTime, Instant::ofEpochMilli))
        ), ErrorCode.PublishLostFailed);
    }

    private static Lost.LostInfo consLostInfo(LostId lostId, LazyVal<LostInfo> lostInfo) {
        return new Lost.LostInfo() {
            public String getId()           { return lostId.value; }
            public String getName()         { return nilOrTr(lostInfo.get(), x -> x.name); }
            public String getDescription()  { return nilOrTr(lostInfo.get(), x -> x.description); }
            public String getPosition()     { return nilOrTr(lostInfo.get(), x -> x.position); }
            public String getPictureURL()   { return factorOutPictureUrl(FileId.of(lostInfo.get().pictureId)); }
            public String getContact()      { return nilOrTr(lostInfo.get(), x -> x.contact); }
            public Long getCreateTime()     { return nilOrTr(lostInfo.get(), x -> x.createTime.toEpochMilli()); }
            public Long getLostTime()      { return nilOrTr(lostInfo.get(), x -> x.lostTime.toEpochMilli()); }
            public PersonalInformation.PersonalInfo getPublisher() {
                return Commons.fetchPersonalInfo(UserId.of(lostInfo.get().publisherId)).reduce(AppLost::warnNil, r -> r);
            }
            public PersonalInformation.PersonalInfo getClaimer() {
                if (lostInfo.get().founderId == null) return null;
                return Commons.fetchPersonalInfo(UserId.of(lostInfo.get().founderId)).reduce(AppLost::warnNil, r -> r);
            }
        };
    }

    private static Lost.LostInfo consLostInfo(Lost.LostInput input, LostId id, UserId publisherId, @Nullable FileId fileId) {
        return new Lost.LostInfo() {
            public String getId() { return id.value; }
            public PersonalInformation.PersonalInfo getPublisher() {
                return Commons.fetchPersonalInfo(publisherId).reduce(AppLost::warnNil, i -> i);
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
            public Long getLostTime() { return input.lostTime; }
            public PersonalInformation.PersonalInfo getClaimer() { return null; }
        };
    }

    private static Lost.MultiLostInfos consMultiLostsInfo(Supplier<List<Lost.LostInfo>> lostInfos) {
        return new Lost.MultiLostInfos() {
            public Long getTotalCount() { return componentFactory.lost.allPostsCount(); }
            public List<Lost.LostInfo> getLosts() { return lostInfos.get(); }
        };
    }

    private static Procedure<ErrorCode, Optional<FileId>> addPictureIfNecessary(@Nullable String pictureBase64) {
        if (pictureBase64 == null) return Result.of(Optional.empty());
        byte[] image = Base64.getDecoder().decode(pictureBase64);
        Optional<FileId> newPicId = componentFactory.lostFoundImages.createFile(image);
        return Procedure.fromOptional(newPicId, ErrorCode.CreateLostImageFailed)
                .then(fileId -> Result.of(Optional.of(fileId), () -> componentFactory.lostFoundImages.Remove(fileId)));
    }

    private static String factorOutPictureUrl(FileId fileId) {
        return Commons.fetchPictureUrl(componentFactory.lostFoundImages, StaticResourceConfig.FileType.LostFoundImage, fileId)
                .reduce(e -> null, i -> i);
    }

    private static Procedure<ErrorCode, ?> ensureOwnPost(UserId userId, LostId lostId) {
        return Procedure.fromOptional(componentFactory.lost.postInfo(lostId), ErrorCode.LostPostNonExist)
                .then(lostInfo -> lostInfo.publisherId.equals(userId.value) ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied));
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
