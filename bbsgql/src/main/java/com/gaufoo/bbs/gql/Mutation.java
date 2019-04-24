package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.application.error.BError;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

public class Mutation implements GraphQLMutationResolver {
    public PersonalInformation.EditPersonInfoResult editPersonInfo(PersonalInformation.PersonInfoInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                userToken -> PersonalInformation.editPersonInfo(input, userToken)
        ).orElse(authError);
    }

    private static BError authError = BError.of(ErrorCode.NotLoggedIn);
}
