package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {
    String test(String testStr, DataFetchingEnvironment env) {
        return testStr;
    }
}
