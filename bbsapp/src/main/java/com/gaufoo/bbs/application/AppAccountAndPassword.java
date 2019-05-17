package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;

import static com.gaufoo.bbs.application.types.Authentication.LoginInput;
import static com.gaufoo.bbs.application.types.AccountAndPassword.*;
import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class AppAccountAndPassword {
    public static ConfirmPasswordResult confirmPassword(LoginInput input, String userToken) {
        if (!componentFactory.authenticator.isAuthenticated(input.username, input.password)) {
            return Error.of(ErrorCode.AuthenticateFailed);
        }

        return Commons.fetchUserId(UserToken.of(userToken))
                .then(__ -> componentFactory.authenticator.reqResetPassword(input.username).mapF(ErrorCode::fromAuthError))
                .reduce(Error::of, r -> (ResetPassToken)() -> r.value);
    }

    public static ChangePasswordResult changePassword(String newPassword, String resetToken) {
        return componentFactory.authenticator.resetPassword(ResetToken.of(resetToken), newPassword)
                .reduce(ae -> Error.of(ErrorCode.fromAuthError(ae)), i -> Ok.build());
    }
}
