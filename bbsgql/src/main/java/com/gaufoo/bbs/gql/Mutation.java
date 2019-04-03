package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

@Component
public class Mutation implements GraphQLMutationResolver {
    String test(String testStr, DataFetchingEnvironment env) {
        return testStr;
    }

    Authentication.SignUpResult signUp(String username, String password, String nickname, DataFetchingEnvironment env) {
        return Authentication.signUp(username, password, nickname);
    }

    Authentication.LogInResult logIn(String username, String password) {
        return Authentication.logIn(username, password);
    }

    Authentication.LogOutError logOut(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(tkn -> {
                    Authentication.logOut(tkn);
                    return (Authentication.LogOutError)null;
                })
                .orElse(Authentication.LogOutError.of("尚未登录"));
    }
}
