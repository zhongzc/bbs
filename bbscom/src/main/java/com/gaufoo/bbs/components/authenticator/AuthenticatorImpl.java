package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components._repositories.AuthenticatorMemoryRepository;
import com.gaufoo.bbs.components._repositories.TokenGeneratorMemoryRepository;
import com.gaufoo.bbs.components.authenticator.common.*;
import com.gaufoo.bbs.components.authenticator.exceptions.*;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.validator.Validator;

import java.time.*;

class AuthenticatorImpl implements Authenticator {
    private final String componentName;
    private final AuthenticatorRepository repository;
    private final Validator<String> usernameValidator;
    private final Validator<String> passwordValidator;
    private final TokenGenerator tokenGenerator;

    AuthenticatorImpl(String componentName,
                      AuthenticatorRepository repository,
                      Validator<String> usernameValidator,
                      Validator<String> passwordValidator,
                      TokenGenerator tokenGenerator) {
        this.componentName = componentName;
        this.repository = repository;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Attachable signUp(String username, String password) throws AuthenticatorException {
        if (!usernameValidator.validate(username)) throw new UsernameInvalid("用户名不合法");
        if (!passwordValidator.validate(password)) throw new PasswordInvalid("密码不合法");
        if (repository.contains(username)) throw new UsernameInvalid("用户名已存在");

        return permission -> {
            if (!repository.saveUser(username, password, permission)) throw new CommonException("注册失败");
        };
    }

    @Override
    public UserToken login(String username, String password) throws AuthenticatorException {
        if (!repository.contains(username)) throw new UsernameInvalid("用户名不存在");
        if (!repository.getPassword(username).equals(password)) throw new PasswordInvalid("密码错误");

        Permission permission = repository.getPermissionByUsername(username);
        String userId = permission.userId;
        String token = tokenGenerator.genToken(userId, Instant.now().plus(Duration.ofDays(30)));
        UserToken userToken = UserToken.of(token);

        if  (!repository.saveUserToken(userToken, permission)) throw new CommonException("登录失败");

        return userToken;
    }

    @Override
    public Permission getLoggedUser(UserToken userToken) throws AuthenticatorException {
        if (tokenGenerator.isExpired(userToken.value)) {
            repository.deleteUserToken(userToken);
            throw new ExpireException("登录已过期");
        }
        Permission permission = repository.getPermissionByToken(userToken);
        if (permission == null) throw new TokenException("登录信息无效");

        return permission;
    }

    @Override
    public void logout(UserToken userToken) {
        tokenGenerator.expire(userToken.value);
        repository.deleteUserToken(userToken);
    }

    @Override
    public ResetToken reqResetPassword(String username) throws AuthenticatorException {
        if (!repository.contains(username)) throw new UsernameInvalid("用户名不存在");

        String token = tokenGenerator.genToken(username, Instant.now().plus(Duration.ofHours(1)));
        ResetToken resetToken = ResetToken.of(token);
        if (!repository.saveResetToken(resetToken, username)) {
            throw new CommonException("请求重设密码失败");
        }

        return resetToken;
    }

    @Override
    public void resetPassword(ResetToken resetToken, String newPassword) throws AuthenticatorException {
        if (tokenGenerator.isExpired(resetToken.value)) {
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

    @Override
    public void remove(String username) {
        repository.deleteUser(username);
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) throws AuthenticatorException {
        // 用户名和密码合法性验证器
        Validator<String> usernameV = Validator.email();
        Validator<String> passwordV = Validator.containsDigit()
                .compose(Validator.containsLower())
                .compose(Validator.containsUpper())
                .compose(Validator.maxLength(20))
                .compose(Validator.minLength(8))
                .compose(Validator.nonContainsSpace());
        // token生成器
        TokenGenerator tokenGenerator = TokenGenerator.defau1t("", TokenGeneratorMemoryRepository.get(""));
        // 认证器
        Authenticator authenticator = Authenticator.defau1t(
                "",
                AuthenticatorMemoryRepository.get(""),
                usernameV, passwordV, tokenGenerator
        );

        // 注册
        authenticator.signUp("gaufoo@123.com", "Abc123456").attach(
                Permission.of("001", Authenticator.Role.USER));
        // 登录
        UserToken token = authenticator.login("gaufoo@123.com", "Abc123456");
        System.out.println(token.value);   // 获得token
        System.out.println(authenticator.getLoggedUser(token).userId); // 从token获得id
        // 修改密码
        ResetToken rtoken = authenticator.reqResetPassword("gaufoo@123.com");
        authenticator.resetPassword(rtoken, "123456Abc");

        // 改密码后，原登录信息无效
        // System.out.println(authenticator.getLoggedUser(token).getUserId());

        // 重新登录
        UserToken token2 = authenticator.login("gaufoo@123.com", "123456Abc");
        System.out.println(token2.value);
        System.out.println(authenticator.getLoggedUser(token2).userId);
        // 取消登录
        authenticator.logout(token2);

        // 取消登录后，原登录信息无效
        // System.out.println(authenticator.getLoggedUser(token2).getUserId());
    }
}
