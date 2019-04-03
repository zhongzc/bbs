package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountAndPassword {
    private static Logger logger = LoggerFactory.getLogger(AccountAndPassword.class);

    public static ConfirmPasswordResult confirmPassword(String userToken, String username, String password) {
        try {
            logger.debug("confirmPassword userToken: {}, username: {}", userToken, username);
            ComponentFactory.authenticator.getLoggedUser(UserToken.of(userToken));

            if (ComponentFactory.authenticator.isAuthenticated(username, password)) {
                ResetToken resetToken = ComponentFactory.authenticator.reqResetPassword(username);
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
            ComponentFactory.authenticator.getLoggedUser(UserToken.of(userToken));
        } catch (AuthenticatorException e) {
            return ChangePasswordError.of(e.getMessage());
        }

        try {
            ComponentFactory.authenticator.resetPassword(ResetToken.of(resetToken), newPassword);
            return null;
        } catch (AuthenticatorException e) {
            return ChangePasswordError.of(e.getMessage());
        }
    }

    // TODO: change username
//    public static void ChangeEmail(String userToken, String resetToken, String newEmail) {
//
//    }


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

}
