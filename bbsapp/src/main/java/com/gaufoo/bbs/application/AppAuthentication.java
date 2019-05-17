package com.gaufoo.bbs.application;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.user.common.UserInfo;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.types.Authentication.*;
import static com.gaufoo.bbs.util.TaskChain.Procedure;
import static com.gaufoo.bbs.util.TaskChain.Result;

public class AppAuthentication {
    public static CurrentUserResult currentUser(String userToken) {
        return Commons.fetchUserId(UserToken.of(userToken))
                .then(Commons::fetchPersonalInfo)
                .reduce(Error::of, r -> r);
    }

    public static SignupResult signup(SignupInput input) {
        return componentFactory.authenticator.signUp(input.username, input.password)
                .mapF(ErrorCode::fromAuthError)
                .then(attachable -> Procedure.fromOptional(componentFactory.user.createUser(createUserInfo(input.nickname)), ErrorCode.CreateUserFailed)
                        .then(userId -> Result.of(userId, () -> componentFactory.user.remove(userId)))
                        .then(userId -> attachable.attach(Permission.of(userId.value, Authenticator.Role.USER))
                                .mapF(ErrorCode::fromAuthError)))
                .then(x -> componentFactory.authenticator.login(input.username, input.password)
                        .mapF(ErrorCode::fromAuthError))
                .then(userToken -> Result.of((LoggedInToken)() -> userToken.value))
                .reduce(Error::of, r -> r);
    }

    public static LoginResult login(LoginInput input) {
        return componentFactory.authenticator.login(input.username, input.password)
                .mapF(ErrorCode::fromAuthError)
                .then(userToken -> Result.of((LoggedInToken)() -> userToken.value))
                .reduce(Error::of, r -> r);
    }

    public static LogoutResult logout(String userToken) {
        componentFactory.authenticator.logout(UserToken.of(userToken));
        return Ok.build();
    }

    private static UserInfo createUserInfo(String nickname) {
        return UserInfo.of(nickname, null, null, null, null, null);
    }
}
