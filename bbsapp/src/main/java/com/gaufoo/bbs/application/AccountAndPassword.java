package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class AccountAndPassword {
    private static Logger logger = LoggerFactory.getLogger(AccountAndPassword.class);

    public static ConfirmPasswordResult confirmPassword(String userToken, String username, String password) {
        try {
            logger.debug("confirmPassword userToken: {}, usernameVal: {}", userToken, username);
            componentFactory.authenticator.getLoggedUser(UserToken.of(userToken));

            if (componentFactory.authenticator.isAuthenticated(username, password)) {
                ResetToken resetToken = componentFactory.authenticator.reqResetPassword(username);
                logger.debug("confirmPassword resetToken: {}", resetToken);
                return ConfirmPasswordPayload.of(resetToken.value);
            }

            logger.debug("confirmPassword not authenticated");
            return ConfirmPasswordError.of("密码错误");
        } catch (AuthenticatorException ex) {
            logger.debug("confirmPassword exception: {}", ex.getMessage());
            return ConfirmPasswordError.of(ex.getMessage());
        }
    }

    public static ChangePasswordError changePassword(String userToken, String resetToken, String newPassword) {
        logger.debug("changePassword userToken: {}, resetToken: {}", userToken, resetToken);

        try {
            componentFactory.authenticator.getLoggedUser(UserToken.of(userToken));
        } catch (AuthenticatorException e) {
            return ChangePasswordError.of(e.getMessage());
        }

        try {
            componentFactory.authenticator.resetPassword(ResetToken.of(resetToken), newPassword);
            return null;
        } catch (AuthenticatorException e) {
            return ChangePasswordError.of(e.getMessage());
        }
    }

    public static class ConfirmPasswordError implements ConfirmPasswordResult {
        private String error;

        public ConfirmPasswordError(String error) {
            this.error = error;
        }

        public static ConfirmPasswordError of(String error) {
            return new ConfirmPasswordError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class ConfirmPasswordPayload implements ConfirmPasswordResult {
        String resetToken;

        public ConfirmPasswordPayload(String resetToken) {
            this.resetToken = resetToken;
        }

        public static ConfirmPasswordPayload of(String resetToken) {
            return new ConfirmPasswordPayload(resetToken);
        }

        public String getResetToken() {
            return resetToken;
        }
    }

    public interface ConfirmPasswordResult {
    }

    public static class ChangePasswordError {
        private String error;

        public ChangePasswordError(String error) {
            this.error = error;
        }

        public static ChangePasswordError of(String error) {
            return new ChangePasswordError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class ChangeUsernameError {
        private String error;

        public ChangeUsernameError(String error) {
            this.error = error;
        }

        public static ChangeUsernameError of(String error) {
            return new ChangeUsernameError(error);
        }

        public String getError() {
            return error;
        }
    }

}
