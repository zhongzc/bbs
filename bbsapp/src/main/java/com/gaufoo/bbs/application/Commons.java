package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.components.authenticator.common.AuthError;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.TaskChain;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.PersonalInformation.consPersonalInfo;

public class Commons {
    public static TaskChain.Procedure<ErrorCode, UserId> fetchUserId(UserToken userToken) {
        TaskChain.Procedure<AuthError, UserId> userIdProc = componentFactory.authenticator.getLoggedUser(userToken)
                .then(perm -> TaskChain.Result.of(UserId.of(perm.userId)));

        if (userIdProc.isSuccessful()) return TaskChain.Result.of(userIdProc.retrieveResult().get());
        else return TaskChain.Fail.of(ErrorCode.fromAuthError(userIdProc.retrieveError().get()));
    }

    public static TaskChain.Procedure<ErrorCode, PersonalInformation.PersonalInfo> fetchPersonalInfo(UserId userId) {
        return TaskChain.Procedure.fromOptional(componentFactory.user.userInfo(userId), ErrorCode.UserNonExist).then(userInfo ->
                TaskChain.Result.of(consPersonalInfo(userId, userInfo))
        );
    }
}
