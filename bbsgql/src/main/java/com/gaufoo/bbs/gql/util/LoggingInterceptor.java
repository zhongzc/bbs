package com.gaufoo.bbs.gql.util;

import graphql.schema.DataFetchingEnvironment;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LoggingInterceptor {
    private static Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    public static MethodInterceptor interceptor = (invocation) -> {
        Method method = invocation.getMethod();

        log.debug("{}: {}", method.getName(), stringifyArgs(invocation.getArguments()));
        return invocation.proceed();
    };

    private static String stringifyArgs(Object[] args) {
        return Arrays.stream(args).map(obj -> {
            if (DataFetchingEnvironment.class.isAssignableFrom(obj.getClass())) {
                String token = Utils.getAuthToken((DataFetchingEnvironment) obj).orElse("");
                return "token=\"" + token + "\"";
            } else {
                return obj.toString();
            }
        }).collect(Collectors.joining(", "));
    }
}
