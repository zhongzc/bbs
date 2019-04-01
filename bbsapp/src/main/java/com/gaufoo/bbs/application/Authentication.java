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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Authentication {
    private static final Logger log = LoggerFactory.getLogger(Authentication.class);

    public static SignUpResult signUp(String username, String password, String nickname) {
        log.info(String.format("sign up: %s#%s#%s", username, password, nickname));

        try {
            Attachable needUser = ComponentFactory.authenticator.signUp(username, password);

            Optional<UserId> userId = ComponentFactory.user.createUser(UserInfo.of(nickname, null, UserInfo.Gender.secret, null, null, null));
            if (!userId.isPresent()) {
                log.warn(String.format("sign up failed: %s %s#%s#%s", "用户创建失败", username, password, nickname));
                return SignUpError.of("用户创建失败");
            } else {
                needUser.attach(Permission.of(userId.get().value, Authenticator.Role.USER));
            }

        } catch (AuthenticatorException e) {
            log.warn(String.format("sign up failed: %s %s#%s#%s", e.getMessage(), username, password, nickname));
            return SignUpError.of(e.getMessage());
        }

        try {
            UserToken token = ComponentFactory.authenticator.login(username, password);
            log.info(String.format("sign up successfully: %s %s#%s#%s", token, username, password, nickname));
            return SignUpPayload.of(token.value);
        } catch (AuthenticatorException e) {
            log.warn(String.format("sign up failed: %s %s#%s#%s", e.getMessage(), username, password, nickname));
            return SignUpError.of(e.getMessage());
        }
    }
}
