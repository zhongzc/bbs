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
        Validator<String> usernameV = Validator.email();
        Validator<String> passwordV = Validator.containsDigit()
                .compose(Validator.containsLower())
                .compose(Validator.containsUpper())
                .compose(Validator.maxLengthValidator(20))
                .compose(Validator.minLengthValidator(8))
                .compose(Validator.nonContainsSpace());

        TokenGenerator tokenGenerator = TokenGenerator.defau1t(TokenGeneratorMemoryRepository.get());
        Authenticator authenticator = Authenticator.defau1t(
                AuthenticatorMemoryRepository.get(),
                usernameV, passwordV, tokenGenerator
        );

        authenticator.signUp("gaufoo@123.com", "Abc123456",
                Authenticator.permission("001", Authenticator.Role.USER));

        UserToken token = authenticator.login("gaufoo@123.com", "Abc123456");
        System.out.println(token.getValue());
        System.out.println(authenticator.getLoggedUser(token).getUserId());

        ResetToken rtoken = authenticator.reqResetPassword("gaufoo@123.com");
        authenticator.resetPassword(rtoken, "123456Abc");

        // 改密码后，原登录信息无效
        // System.out.println(authenticator.getLoggedUser(token).getUserId());

        UserToken token2 = authenticator.login("gaufoo@123.com", "123456Abc");

        System.out.println(token2.getValue());
        System.out.println(authenticator.getLoggedUser(token2).getUserId());

    }
}
