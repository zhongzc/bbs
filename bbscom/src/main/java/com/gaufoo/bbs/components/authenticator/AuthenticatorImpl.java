package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components.authenticator.common.*;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.validator.Validator;

import java.time.*;

import static com.gaufoo.bbs.util.TaskChain.*;

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
    public Procedure<Attachable> signUp(String username, String password) {
        if (!usernameValidator.validate(username)) return Fail.of("用户名不合法");
        if (!passwordValidator.validate(password)) return Fail.of("密码不合法");
        if (repository.contains(username)) return Fail.of("用户名已存在");

        return Result.of(permission -> {
            if (!repository.saveUser(username, password, permission)) return Fail.of("注册失败");
            else return Result.of(true);
        });
    }

    @Override
    public Procedure<UserToken> login(String username, String password) {
        if (!repository.contains(username)) return Fail.of("用户名不存在");
        if (!repository.getPassword(username).equals(password)) return Fail.of("密码错误");

        Permission permission = repository.getPermissionByUsername(username);
        String userId = permission.userId;
        String token = tokenGenerator.genToken(userId, Instant.now().plus(Duration.ofDays(30)));
        UserToken userToken = UserToken.of(token);

        if (!repository.saveUserToken(userToken, permission)) return Fail.of("登录失败");

        return Result.of(userToken);
    }

    @Override
    public Procedure<Permission> getLoggedUser(UserToken userToken) {
        if (tokenGenerator.isExpired(userToken.value)) {
            repository.deleteUserToken(userToken);
            return Fail.of("登录已过期");
        }
        Permission permission = repository.getPermissionByToken(userToken);
        return Procedure.ofNullable(permission, "登录信息无效");
    }


    @Override
    public void logout(UserToken userToken) {
        tokenGenerator.expire(userToken.value);
        repository.deleteUserToken(userToken);
    }

    @Override
    public Procedure<ResetToken> reqResetPassword(String username) {
        if (!repository.contains(username)) return Fail.of("用户名不存在");

        String token = tokenGenerator.genToken(username, Instant.now().plus(Duration.ofHours(1)));
        ResetToken resetToken = ResetToken.of(token);
        if (!repository.saveResetToken(resetToken, username)) {
            return Fail.of("请求重设密码失败");
        }

        return Result.of(resetToken);
    }

    @Override
    public Procedure<Boolean> resetPassword(ResetToken resetToken, String newPassword) {
        if (tokenGenerator.isExpired(resetToken.value)) {
            repository.deleteResetToken(resetToken);
            return Fail.of("操作已超时");
        }

        String username = repository.getUsernameByResetToken(resetToken);
        if (username == null) return Fail.of("重置信息无效");
        if (!passwordValidator.validate(newPassword)) return Fail.of("密码不合法");

        if (!repository.setPassword(username, newPassword)) return Fail.of("重置失败");
        repository.deleteResetToken(resetToken);
        repository.deleteUserTokenByUsername(username);
        return Result.of(true);
    }

    @Override
    public void remove(String username) {
        repository.deleteUser(username);
    }

    @Override
    public boolean isAuthenticated(String username, String password) {
        return repository.contains(username) && repository.getPassword(username).equals(password);
    }

}
