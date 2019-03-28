package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.resTypes.SignUpError;
import com.gaufoo.bbs.application.resTypes.SignUpPayload;
import com.gaufoo.bbs.application.resTypes.SignUpResult;
import com.gaufoo.bbs.components._repositories.AuthenticatorMemoryRepository;
import com.gaufoo.bbs.components._repositories.TokenGeneratorMemoryRepository;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;
import com.gaufoo.bbs.components.validator.Validator;

import java.util.Optional;

public class Authentication {
    public final Authenticator authenticator = Authenticator.defau1t(
            "auth", AuthenticatorMemoryRepository.get("authMryRep"),
            Validator.email(), Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20)),
            TokenGenerator.defau1t("authToken", TokenGeneratorMemoryRepository.get("authTokenMryRep"))
    );

    private Authentication() { }

    public SignUpResult signUp(String username, String password, String nickname) {
        User user = User.getInstance();
        Optional<UserId> userId = user.userFactory.createUser(UserInfo.of(nickname, null, UserInfo.Gender.secret, null, null, null));
        if (!userId.isPresent()) {
            return SignUpError.of("用户创建失败");
        }

        try {
            authenticator.signUp(username, password, Permission.of(userId.get().value, Authenticator.Role.USER));
        } catch (AuthenticatorException e) {
            return SignUpError.of(e.getMessage());
        }

        UserToken token;

        try {
           token = authenticator.login(username, password);
        } catch (AuthenticatorException e) {
            return SignUpError.of(e.getMessage());
        }

        return SignUpPayload.of(token.value);
    }

    private static Authentication ourInstance = new Authentication();

    public static Authentication getInstance() {
        return ourInstance;
    }

}
