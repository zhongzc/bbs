package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Attachable;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.scutMajor.common.Major;
import com.gaufoo.bbs.components.scutMajor.common.MajorValue;
import com.gaufoo.bbs.components.scutMajor.common.School;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Authentication {
    private static final Logger log = LoggerFactory.getLogger(Authentication.class);

    public static SignUpResult signUp(String username, String password, String nickname) {
        log.debug("sign up - username: {}, nickname: {}", username, nickname);

        try {
            Attachable needUser = ComponentFactory.authenticator.signUp(username, password);

            Optional<UserId> userId = ComponentFactory.user.createUser(UserInfo.of(nickname, null,
                    UserInfo.Gender.secret, null, defaultMajorCodeValue(), null));
            if (!userId.isPresent()) {
                log.debug("sign up - failed, error: {}, username: {}, nickname: {}", "用户创建失败", username, nickname);
                return SignUpError.of("用户创建失败");
            } else {
                needUser.attach(Permission.of(userId.get().value, Authenticator.Role.USER));
            }

        } catch (AuthenticatorException e) {
            log.debug("sign up - failed, error: {}, username: {}, nickname: {}", e.getMessage(), username, nickname);
            return SignUpError.of(e.getMessage());
        }

        try {
            UserToken token = ComponentFactory.authenticator.login(username, password);
            log.debug("sign up - successfully, token: {}, username: {}, nickname: {}", token, username, nickname);
            return SignUpPayload.of(token.value);
        } catch (AuthenticatorException e) {
            log.debug("sign up - failed, error: {}, username: {}, nickname: {}", e.getMessage(), username, nickname);
            return SignUpError.of(e.getMessage());
        }
    }

    private static String defaultMajorCodeValue() {
        MajorValue defaultMajorValue =  MajorValue.of(School.无, Major.无);
        return ComponentFactory.major.generateMajorCode(defaultMajorValue).value;
    }

    public static LogInResult logIn(String username, String password) {
        log.debug("login, username: {}, password: {}", username, password);

        try {
            UserToken token = ComponentFactory.authenticator.login(username, password);
            log.debug("login - successfully, token: {}, username: {}", token, username);
            return LogInPayload.of(token.value);
        } catch (AuthenticatorException e) {
            log.debug("login - failed, error: {}, username: {}", e.getMessage(), username);
            return LogInError.of(e.getMessage());
        }
    }

    public static LogOutError logOut(String token) {
        log.debug("logOut, token: {}", token);

        ComponentFactory.authenticator.logout(UserToken.of(token));
        return null;
    }

    public static GetIdResult getLoggedUserId(String token) {
        try {
            return GetIdPayload.of(ComponentFactory.authenticator.getLoggedUser(UserToken.of(token)).userId);
        } catch (AuthenticatorException e) {
            return GetIdError.of(e.getMessage());
        }
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

    public static class GetIdError implements GetIdResult {
        private final String error;

        private GetIdError(String error) {
            this.error = error;
        }

        public static GetIdError of(String error) {
            return new GetIdError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class GetIdPayload implements GetIdResult {
        private final String userid;

        private GetIdPayload(String userid) {
            this.userid = userid;
        }

        public static GetIdPayload of(String userid) {
            return new GetIdPayload(userid);
        }

        public String getUserid() {
            return userid;
        }
    }

    public interface GetIdResult {}
}
