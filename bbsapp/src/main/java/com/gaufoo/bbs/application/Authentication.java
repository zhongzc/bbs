package com.gaufoo.bbs.application;

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

    public static LogInResult logIn(String username, String password) {
        log.info(String.format("login: %s#%s", username, password));

        try {
            UserToken token = ComponentFactory.authenticator.login(username, password);
            log.info(String.format("login successfully: %s %s#%s", token, username, password));
            return LogInPayload.of(token.value);
        } catch (AuthenticatorException e) {
            log.warn(String.format("login failed: %s %s#%s", e.getMessage(), username, password));
            return LogInError.of(e.getMessage());
        }
    }

    public static void logOut(String token) {
        ComponentFactory.authenticator.logout(UserToken.of(token));
    }

    public static class LogInError implements LogInResult {
        private String error;

        public LogInError(String error) {
            this.error = error;
        }

        public static LogInError of(String error) {
            return new LogInError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class LogInPayload implements LogInResult {
        private String token;

        public LogInPayload(String token) {
            this.token = token;
        }

        public static LogInPayload of(String token) {
            return new LogInPayload(token);
        }

        public String getToken() {
            return token;
        }
    }

    public interface LogInResult {
    }

    public static class LogOutError {
        private String error;

        public LogOutError(String error) {
            this.error = error;
        }

        public static LogOutError of(String error) {
            return new LogOutError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class SignUpError implements SignUpResult {
        private final String error;

        public SignUpError(String error) {
            this.error = error;
        }

        public static SignUpError of(String error) {
            return new SignUpError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class SignUpPayload implements SignUpResult {
        private final String token;

        public SignUpPayload(String token) {
            this.token = token;
        }

        public static SignUpPayload of(String token) {
            return new SignUpPayload(token);
        }

        public String getToken() {
            return token;
        }
    }

    public interface SignUpResult {
    }
}
