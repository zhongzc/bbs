package com.gaufoo;

import com.gaufoo.bbs.components._repositories.AuthenticatorMemoryRepository;
import com.gaufoo.bbs.components._repositories.TokenGeneratorMemoryRepository;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.validator.Validator;

public class Main {
    public static void main(String[] args) throws AuthenticatorException {
        // 用户名和密码合法性验证器
        Validator<String> usernameV = Validator.email();
        Validator<String> passwordV = Validator.containsDigit()
                .compose(Validator.containsLower())
                .compose(Validator.containsUpper())
                .compose(Validator.maxLengthValidator(20))
                .compose(Validator.minLengthValidator(8))
                .compose(Validator.nonContainsSpace());
        // token生成器
        TokenGenerator tokenGenerator = TokenGenerator.defau1t(TokenGeneratorMemoryRepository.get());
        // 认证器
        Authenticator authenticator = Authenticator.defau1t(
                AuthenticatorMemoryRepository.get(),
                usernameV, passwordV, tokenGenerator
        );

        // 注册
        authenticator.signUp("gaufoo@123.com", "Abc123456",
                Authenticator.permission("001", Authenticator.Role.USER));
        // 登录
        UserToken token = authenticator.login("gaufoo@123.com", "Abc123456");
        System.out.println(token.getValue());   // 获得token
        System.out.println(authenticator.getLoggedUser(token).getUserId()); // 从token获得id
        // 修改密码
        ResetToken rtoken = authenticator.reqResetPassword("gaufoo@123.com");
        authenticator.resetPassword(rtoken, "123456Abc");

        // 改密码后，原登录信息无效
        // System.out.println(authenticator.getLoggedUser(token).getUserId());

        // 重新登录
        UserToken token2 = authenticator.login("gaufoo@123.com", "123456Abc");
        System.out.println(token2.getValue());
        System.out.println(authenticator.getLoggedUser(token2).getUserId());
        // 取消登录
        authenticator.logout(token2);

        // 取消登录后，原登录信息无效
        // System.out.println(authenticator.getLoggedUser(token2).getUserId());
    }
}
