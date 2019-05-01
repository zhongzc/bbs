package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.TaskChain;
import org.slf4j.Logger;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.PersonalInformation.consPersonalInfo;

public class Commons {
    public static UserId fetchUserIdAndUnwrap(UserToken userToken, Consumer<ErrorCode> nilCallBack) {
        return fetchUserId(userToken).reduce(e -> {
            nilCallBack.accept(e);
            return null;
        }, i -> i);
    }
    public static TaskChain.Procedure<ErrorCode, UserId> fetchUserId(UserToken userToken) {
        return fetchPermission(userToken)
                .then(permission -> TaskChain.Result.of(UserId.of(permission.userId)));
    }

    public static Permission fetchPermissionAndUnwrap(UserToken userToken, Consumer<ErrorCode> nilCallBack) {
        return fetchPermission(userToken).reduce(e -> {
            nilCallBack.accept(e);
            return null;
        }, i -> i);
    }
    public static TaskChain.Procedure<ErrorCode, Permission> fetchPermission(UserToken userToken) {
        return componentFactory.authenticator.getLoggedUser(userToken)
                .mapF(ErrorCode::fromAuthError);
    }

    public static TaskChain.Procedure<ErrorCode, Void> ensureAdmin(UserToken userToken) {
        return componentFactory.authenticator.getLoggedUser(userToken)
                .mapF(ErrorCode::fromAuthError)
                .then(permission -> permission.role.equals(Authenticator.Role.ADMIN) ?
                        TaskChain.Result.of(null) :
                        TaskChain.Fail.of(ErrorCode.PermissionDenied));
    }

    public static PersonalInformation.PersonalInfo fetchPersonalInfoAndUnwrap(UserId userId, Consumer<ErrorCode> nilCallBack) {
        return fetchPersonalInfo(userId).reduce(e -> {
            nilCallBack.accept(e);
            return null;
        }, i -> i);
    }
    public static TaskChain.Procedure<ErrorCode, PersonalInformation.PersonalInfo> fetchPersonalInfo(UserId userId) {
        return TaskChain.Procedure.fromOptional(componentFactory.user.userInfo(userId), ErrorCode.UserNonExist)
                .then(userInfo -> TaskChain.Result.of(consPersonalInfo(userId, userInfo))
        );
    }

    public static TaskChain.Procedure<ErrorCode, Void> ensureUserExist(UserId userId) {
        return TaskChain.Procedure.fromOptional(componentFactory.user.userInfo(userId), ErrorCode.UserNonExist)
                .then(__ -> TaskChain.Result.of(null));
    }

    public static String fetchFileUrlAndUnwrap(FileFactory fileFactory, StaticResourceConfig.FileType fileType, FileId fileId, Consumer<ErrorCode> nilCallBack) {
        return fetchFileUrl(fileFactory, fileType, fileId).reduce(e -> {
            nilCallBack.accept(e);
            return null;
        }, i -> i);
    }
    public static TaskChain.Procedure<ErrorCode, String> fetchFileUrl(FileFactory fileFactory, StaticResourceConfig.FileType fileType, FileId fileId) {
        return TaskChain.Procedure.fromOptional(fileFactory.fileURI(fileId), ErrorCode.FileNotFound)
                .then(uri -> TaskChain.Result.of(componentFactory.staticResourceConfig.makeUrl(fileType, URI.create(uri))));
    }

    public static TaskChain.Procedure<ErrorCode, FileId> storeBase64File(FileFactory fileFactory, String base64File) {
        byte[] file = Base64.getDecoder().decode(base64File);
        return TaskChain.Procedure.fromOptional(fileFactory.createFile(file), ErrorCode.SaveFileFailed)
                .then(fileId -> TaskChain.Result.of(fileId, () -> fileFactory.Remove(fileId)));
    }

    public static String lastHeatTimeWindow() {
        return heatTimeWindow(Instant.now().minus(5, ChronoUnit.HOURS));
    }

    public static String currentHeatTimeWindow() {
        return heatTimeWindow(Instant.now());
    }

    public static String heatTimeWindow(Instant instant) {
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        int hWindow = ldt.getHour() - (ldt.getHour() % 5);
        return String.format("%02d%02d%02d", ldt.getMonthValue(), ldt.getDayOfMonth(), hWindow);
    }

    public static String lastActiveTimeWindow() {
        return activeTimeWindow(Instant.now().minus(1, ChronoUnit.HOURS));
    }

    public static String currentActiveTimeWindow() {
        return activeTimeWindow(Instant.now());
    }

    public static String activeTimeWindow(Instant instant) {
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return String.format("%02d%02d%02d", ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour());
    }

    public static String getGroupId(PostType postType) {
        return String.format("%02d", postType.ordinal());
    }

    public static PostType parseGroupId(String string) {
        return PostType.values()[Integer.parseInt(string.substring(0, 2))];
    }

    public enum PostType {
        SchoolHeat,
        Entertainment,
        LearningResource
    }

    public enum SortedBy {
        ActiveTimeAsc,
        ActiveTimeDes,
        HeatAsc,
        HeatDes,
        NatureAsc,
        NatureDes
    }
}
