package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Query implements GraphQLQueryResolver {
    String test(String testStr, DataFetchingEnvironment env) {
        return testStr;
    }

    PersonalInformation.PersonalInfoResult userInfo(String userId) {
        return PersonalInformation.userInfo(userId);
    }
    List<String> allAcademies() {
        return PersonalInformation.allAcademies();
    }
    List<String> allMajors() {
        return PersonalInformation.allMajors();
    }
    PersonalInformation.MajorsInResult majorsIn(String academy) {
        return PersonalInformation.majorsIn(academy);
    }


    Authentication.GetIdResult loggedId(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(Authentication::getLoggedUserId)
                .orElse(Authentication.GetIdError.of("用户未登录"));
    }
}
