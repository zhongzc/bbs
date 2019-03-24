package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components.authenticator.common.*;
import com.gaufoo.bbs.components.authenticator.exceptions.*;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.validator.Validator;

import java.time.*;

class AuthenticatorImpl implements Authenticator {
    private final AuthenticatorRepository repository;
    private final Validator<String> usernameValidator;
    private final Validator<String> passwordValidator;
    private final TokenGenerator tokenGenerator;

    AuthenticatorImpl(AuthenticatorRepository repository,
                      Validator<String> usernameValidator,
                      Validator<String> passwordValidator,
                      TokenGenerator tokenGenerator) {
        this.repository = repository;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public void signUp(String username, String password, Permission permission) throws AuthenticatorException {
        if (!usernameValidator.validate(username)) throw new UsernameInvalid("用户名不合法");
        if (!passwordValidator.validate(password)) throw new PasswordInvalid("密码不合法");
        if (repository.contains(username)) throw new UsernameInvalid("用户名已存在");
        if (!repository.saveUser(username, password, permission)) throw new CommonException("注册失败");
    }

    @Override
    public UserToken login(String username, String password) throws AuthenticatorException {
        if (!repository.contains(username)) throw new UsernameInvalid("用户名不存在");
        if (!repository.getPassword(username).equals(password)) throw new PasswordInvalid("密码错误");

        Permission permission = repository.getPermissionByUsername(username);
        String userId = permission.getUserId();
        String token = tokenGenerator.genToken(userId, Instant.now().plus(Duration.ofDays(30)));
        UserToken userToken = Authenticator.userToken(token);

        if  (!repository.saveUserToken(userToken, permission)) throw new CommonException("登录失败");

        return userToken;
    }

    @Override
    public Permission getLoggedUser(UserToken userToken) throws AuthenticatorException {
        if (tokenGenerator.isExpired(userToken.getValue())) {
            repository.deleteUserToken(userToken);
            throw new ExpireException("登录已过期");
        }
        Permission permission = repository.getPermissionByToken(userToken);
        if (permission == null) throw new TokenException("登录信息无效");

        return permission;
    }

    @Override
    public void logout(UserToken userToken) {
        tokenGenerator.expire(userToken.getValue());
        repository.deleteUserToken(userToken);
    }

    @Override
    public ResetToken reqResetPassword(String username) throws AuthenticatorException {
        if (!repository.contains(username)) throw new UsernameInvalid("用户名不存在");

        String token = tokenGenerator.genToken(username, Instant.now().plus(Duration.ofHours(1)));
        ResetToken resetToken = Authenticator.resetToken(token);
        if (!repository.saveResetToken(resetToken, username)) {
            throw new CommonException("请求重设密码失败");
        }

        return resetToken;
    }

    @Override
    public void resetPassword(ResetToken resetToken, String newPassword) throws AuthenticatorException {
        if (tokenGenerator.isExpired(resetToken.getValue())) {
            repository.deleteResetToken(resetToken);
            throw new ExpireException("操作已超时");
        }

        String username = repository.getUsernameByResetToken(resetToken);
        if (username == null) throw new TokenException("重置信息无效");
        if (!passwordValidator.validate(newPassword)) throw new PasswordInvalid("密码不合法");

        if (!repository.setPassword(username, newPassword)) throw new CommonException("重置无效");
        repository.deleteResetToken(resetToken);
        repository.deleteUserTokenByUsername(username);
    }
}
