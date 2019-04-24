package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.gaufoo.bbs.application.PersonalInformation;

public class Query implements GraphQLQueryResolver {
    public PersonalInformation.PersonInfoResult personInfo(String id) {
        return PersonalInformation.personInfo(id);
    }


}
