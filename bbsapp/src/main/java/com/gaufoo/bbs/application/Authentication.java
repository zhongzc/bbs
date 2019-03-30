package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.resTypes.SignUpError;
import com.gaufoo.bbs.application.resTypes.SignUpPayload;
import com.gaufoo.bbs.application.resTypes.SignUpResult;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Attachable;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;

import java.util.Optional;

public class Authentication {
    public static SignUpResult signUp(String username, String password, String nickname) {
        try {
            Attachable needUser = ComponentFactory.authenticator.signUp(username, password);

            Optional<UserId> userId = ComponentFactory.user.createUser(UserInfo.of(nickname, null, UserInfo.Gender.secret, null, null, null));
            if (!userId.isPresent()) {
                ComponentFactory.authenticator.remove(username);
                return SignUpError.of("用户创建失败");
            } else {
                needUser.attach(Permission.of(userId.get().value, Authenticator.Role.USER));
            }

        } catch (AuthenticatorException e) {
            return SignUpError.of(e.getMessage());
        }

        try {
            UserToken token = ComponentFactory.authenticator.login(username, password);
            return SignUpPayload.of(token.value);
        } catch (AuthenticatorException e) {
            return SignUpError.of(e.getMessage());
        }
    }
}
