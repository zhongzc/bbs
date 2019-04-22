package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.gql.util.AuthenticationInterceptor;
import com.gaufoo.bbs.gql.util.AuthenticationInterceptor.Authenticate;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mutation implements GraphQLMutationResolver {

    @Authenticate
    Error needsAuth(DataFetchingEnvironment env) {
        String token = AuthenticationInterceptor.unwrap(env);
        return Error.of(ErrorCode.Authenticate, token);
    }

}
