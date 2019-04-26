package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.AppFound;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

import static com.gaufoo.bbs.application.types.Authentication.CurrentUserResult;
import static com.gaufoo.bbs.application.types.Found.AllFoundsResult;
import static com.gaufoo.bbs.application.types.Found.FoundInfoResult;
import static com.gaufoo.bbs.application.types.PersonalInformation.PersonInfoResult;

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

    public List<String> allCourses() {
        return PersonalInformation.allCourses();
    }

    public List<String> majorsIn(String school) {
        return PersonalInformation.majorsIn(school);
    }

    public CurrentUserResult currentUser(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                Authentication::currentUser
        ).orElse(authError);
    }

    public AllFoundsResult allFounds(Long skip, Long first) {
        return AppFound.allFounds(skip, first);
    }

    public FoundInfoResult foundInfo(String id) {
        return AppFound.foundInfo(id);
    }

    private static Error authError = Error.of(ErrorCode.NotLoggedIn);
}
