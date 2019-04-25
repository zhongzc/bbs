package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.TaskChain;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.PersonalInformation.consPersonalInfo;

public class Commons {
    public static TaskChain.Procedure<ErrorCode, UserId> fetchUserId(UserToken userToken) {
        return componentFactory.authenticator.getLoggedUser(userToken)
                .then(perm -> TaskChain.Result.of(UserId.of(perm.userId)))
                .mapF(ErrorCode::fromAuthError);
    }

    public static TaskChain.Procedure<ErrorCode, PersonalInformation.PersonalInfo> fetchPersonalInfo(UserId userId) {
        return TaskChain.Procedure.fromOptional(componentFactory.user.userInfo(userId), ErrorCode.UserNonExist)
                .then(userInfo -> TaskChain.Result.of(consPersonalInfo(userId, userInfo))
        );
    }

    public static TaskChain.Procedure<ErrorCode, String> fetchPictureUrl(FileFactory fileFactory, StaticResourceConfig.FileType fileType, FileId fileId) {
        return TaskChain.Procedure.fromOptional(fileFactory.fileURI(fileId), ErrorCode.FileNotFound)
                .then(uri -> TaskChain.Result.of(componentFactory.staticResourceConfig.makeUrl(fileType, URI.create(uri))));
    }

    public static String lastHeatTimeWindow(Instant now) {
        return DateTimeFormatter.ofPattern("MMddHH").withZone(ZoneId.systemDefault()).format(now.minus(5, ChronoUnit.HOURS));
    }

    public static String currentHeatTimeWindow(Instant now) {
        return DateTimeFormatter.ofPattern("MMddHH").withZone(ZoneId.systemDefault()).format(now);
    }

    public static String lastActiveTimeWindow(Instant now) {
        return DateTimeFormatter.ofPattern("MMddHH").withZone(ZoneId.systemDefault()).format(now.minus(1, ChronoUnit.HOURS));
    }

    public static String currentActiveTimeWindow(Instant now) {
        return DateTimeFormatter.ofPattern("MMddHH").withZone(ZoneId.systemDefault()).format(now);
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
}
