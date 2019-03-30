package com.gaufoo.bbsgql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.resTypes.SignUpResult;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

@Component
public class Mutation implements GraphQLMutationResolver {
    String test(String testStr, DataFetchingEnvironment env) {
        return testStr;
    }

    SignUpResult signUp(String username, String password, String nickname, DataFetchingEnvironment env) {
        return Authentication.signUp(username, password, nickname);
    }
}
