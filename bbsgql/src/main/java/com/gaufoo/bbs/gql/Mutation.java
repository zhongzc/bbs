package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

public class Mutation implements GraphQLMutationResolver {

    Error needsAuth(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(token ->
                Error.of(ErrorCode.Authenticate, token)
        ).orElse(authError);
    }

    private static Error authError = Error.of(ErrorCode.Authenticate, "用户未登录");
}
