package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components.authenticator.common.Attachable;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.validator.Validator;

import static com.gaufoo.bbs.util.TaskChain.*;

public interface Authenticator {

    Procedure<Attachable> signUp(String username, String password);

    Procedure<UserToken> login(String username, String password);

    Procedure<Permission> getLoggedUser(UserToken userToken);

    void logout(UserToken userToken);

    Procedure<ResetToken> reqResetPassword(String username);

    Procedure<Boolean> resetPassword(ResetToken resetToken, String newPassword);

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
