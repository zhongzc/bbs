package com.gaufoo.bbs.application;
import com.gaufoo.bbs.application.error.BError;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.scutMajor.common.Major;
import com.gaufoo.bbs.components.scutMajor.common.School;
import com.gaufoo.bbs.components.user.common.UserInfo;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.types.Authentication.*;
import static com.gaufoo.bbs.util.TaskChain.Procedure;
import static com.gaufoo.bbs.util.TaskChain.Result;

public class Authentication {
    public static CurrentUserResult currentUser(String userToken) {
        Procedure<ErrorCode, PersonalInformation.PersonalInfo> res = Commons.fetchUserId(UserToken.of(userToken))
                .then(Commons::fetchPersonalInfo);
        if (res.isSuccessful()) return res.retrieveResult().get();
        return BError.of(res.retrieveError().get());
    }

    public static SignupResult signup(SignupInput input) {
        Procedure<ErrorCode, PersonalInformation.PersonalInfo> res = componentFactory.authenticator.signUp(input.username, input.password)
                .mapE(ErrorCode::fromAuthError)
                .then(attach -> Procedure.fromOptional(componentFactory.user.createUser(createUserInfo(input.nickname)), ErrorCode.CreateUserFailed)
                        .then(userId -> Result.of(userId, () -> componentFactory.user.remove(userId)))
                        .then(userId -> attach.attach(Permission.of(userId.value, Authenticator.Role.USER))
                                .mapE(ErrorCode::fromAuthError)
                                .then(ok -> Commons.fetchPersonalInfo(userId))));
        if (res.isSuccessful()) return res.retrieveResult().get();
        else return BError.of(res.retrieveError().get());
    }


    private static UserInfo createUserInfo(String nickname) {
        return UserInfo.of(nickname, "", UserInfo.Gender.secret, "", emptyMajorCode(), "");
    }

    private static String emptyMajorCode() {
        return componentFactory.major.generateMajorCode(componentFactory.major.getMajorValue(School.无, Major.无).get()).value;
    }
}
