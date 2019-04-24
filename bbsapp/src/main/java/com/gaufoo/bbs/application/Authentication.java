package com.gaufoo.bbs.application;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.AuthError;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.types.Authentication.*;
import static com.gaufoo.bbs.util.TaskChain.Procedure;
import static com.gaufoo.bbs.util.TaskChain.Result;

public class Authentication {
    public static CurrentUserResult currentUser(String userToken) {
        Procedure<ErrorCode, PersonalInformation.PersonalInfo> res = Commons.fetchUserId(UserToken.of(userToken))
                .then(Commons::fetchPersonalInfo);
        if (res.isSuccessful()) return res.retrieveResult().get();
        return Error.of(res.retrieveError().get());
    }

    public static SignupResult signup(SignupInput input) {
        Procedure<ErrorCode, LoggedInToken> result = componentFactory.authenticator.signUp(input.username, input.password)
                .mapE(ErrorCode::fromAuthError)
                .then(attachable -> Procedure.fromOptional(componentFactory.user.createUser(createUserInfo(input.nickname)), ErrorCode.CreateUserFailed)
                        .then(userId -> Result.of(userId, () -> componentFactory.user.remove(userId)))
                        .then(userId -> attachable.attach(Permission.of(userId.value, Authenticator.Role.USER))
                                .mapE(ErrorCode::fromAuthError)))
                .then(x -> componentFactory.authenticator.login(input.username, input.password)
                        .mapE(ErrorCode::fromAuthError))
                .then(userToken -> Result.of(() -> userToken.value));

        if (result.isSuccessful()) return result.retrieveResult().get();
        else return Error.of(result.retrieveError().get());
    }

    public static LoginResult login(LoginInput input) {
        Procedure<ErrorCode, LoggedInToken> result = componentFactory.authenticator.login(input.username, input.password)
                .mapE(ErrorCode::fromAuthError)
                .then(userToken -> Result.of(() -> userToken.value));

        if (result.isSuccessful()) return result.retrieveResult().get();
        else return Error.of(result.retrieveError().get());
    }

    public static LogoutResult logout(String userToken) {
        componentFactory.authenticator.logout(UserToken.of(userToken));
        return Ok.build();
    }

    private static UserInfo createUserInfo(String nickname) {
        return UserInfo.of(nickname, null, null, null, null, null);
    }
}
