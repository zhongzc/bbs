package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components.authenticator.common.*;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.validator.Validator;

import static com.gaufoo.bbs.util.TaskChain.*;

public interface Authenticator {

    Procedure<AuthError, Attachable> signUp(String username, String password);

    Procedure<AuthError, UserToken> login(String username, String password);

    Procedure<AuthError, Permission> getLoggedUser(UserToken userToken);

    void logout(UserToken userToken);

    Procedure<AuthError, ResetToken> reqResetPassword(String username);

    Procedure<AuthError, Boolean> resetPassword(ResetToken resetToken, String newPassword);

    void remove(String username);

    boolean isAuthenticated(String username, String password);

    enum Role {
        USER,
        ADMIN,
    }

    /**
     * 默认实现
     */
    static Authenticator defau1t(AuthenticatorRepository repository,
                                 Validator<String> usernameValidator,
                                 Validator<String> passwordValidator,
                                 TokenGenerator tokenGenerator) {
        return new AuthenticatorImpl(repository, usernameValidator, passwordValidator, tokenGenerator);
    }
}
