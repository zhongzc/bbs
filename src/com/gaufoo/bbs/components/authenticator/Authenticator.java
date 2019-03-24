package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.validator.Validator;

public interface Authenticator {

    void signUp(String username, String password, Permission permission) throws AuthenticatorException;

    UserToken login(String username, String password) throws AuthenticatorException;

    Permission getLoggedUser(UserToken userToken) throws AuthenticatorException;

    void logout(UserToken userToken);

    ResetToken reqResetPassword(String username) throws AuthenticatorException;

    void resetPassword(ResetToken resetToken, String newPassword) throws AuthenticatorException;


    /**
     * 一系列小对象的构造函数
     */
    static Permission permission(String userId, Role role) {
        return new Permission(userId, role);
    }

    static UserToken userToken(String value) {
        return new UserToken(value);
    }

    static ResetToken resetToken(String value) {
        return new ResetToken(value);
    }

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
