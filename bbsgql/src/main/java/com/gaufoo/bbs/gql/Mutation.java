package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

import static com.gaufoo.bbs.application.types.PersonalInformation.*;
import static com.gaufoo.bbs.application.types.Authentication.*;

public class Mutation implements GraphQLMutationResolver {
    public EditPersonInfoResult editPersonInfo(PersonInfoInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                userToken -> PersonalInformation.editPersonInfo(input, userToken)
        ).orElse(authError);
    }

    public SignupResult signup(SignupInput input) {
        return Authentication.signup(input);
    }

    public LoginResult login(LoginInput input) {
        return Authentication.login(input);
    }

    public LogoutResult logout(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                Authentication::logout
        ).orElse(authError);
    }

    private static Error authError = Error.of(ErrorCode.NotLoggedIn);
}
