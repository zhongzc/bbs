package com.gaufoo.bbs.gql.util;

import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.GraphQLContext;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

public class Utils {
    public static Optional<String> getHeader(String header, DataFetchingEnvironment env) {
        GraphQLContext graphQLContext = env.getContext();
        return graphQLContext.getHttpServletRequest()
                .flatMap(req -> Optional.ofNullable(req.getHeader(header)));
    }

    public static Optional<String> getAuthToken(DataFetchingEnvironment env) {
        Optional<String> auth = getHeader("Authorization", env);
        return auth.map(str -> str.replace("Bearer ", ""));
    }

    public static Function<String, String> urlMakerProducer(String prefix) {
        return (uriStr) -> {
            if (uriStr == null || uriStr.isEmpty()) return "";
            return prefix + Paths.get(uriStr).toFile().getName();
        };
    }
}
