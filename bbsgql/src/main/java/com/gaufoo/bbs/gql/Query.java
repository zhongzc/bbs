package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

import static com.gaufoo.bbs.application.types.PersonalInformation.*;
import static com.gaufoo.bbs.application.types.Authentication.*;

import java.util.List;

public class Query implements GraphQLQueryResolver {
    public PersonInfoResult personInfo(String id) {
        return PersonalInformation.personInfo(id);
    }

    public List<String> allMajors() {
        return PersonalInformation.allMajors();
    }

    public List<String> allSchools() {
        return PersonalInformation.allSchools();
    }

    public List<String> majorsIn(String school) {
        return PersonalInformation.majorsIn(school);
    }

    public CurrentUserResult currentUser(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                Authentication::currentUser
        ).orElse(authError);
    }

    private static Error authError = Error.of(ErrorCode.NotLoggedIn);
}
