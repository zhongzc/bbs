package com.gaufoo.bbs.gql.util;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Optional;

@Slf4j
public class AuthenticationInterceptor {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Authenticate {}

    private static class AuthenticationInterceptException extends RuntimeException {
        AuthenticationInterceptException(String errMsg) {
            super(errMsg);
        }
    }

    public static MethodInterceptor interceptor = (invocation) -> {
        Method method = invocation.getMethod();

        boolean requiredAuthGuard = method.getAnnotation(Authenticate.class) != null;
        if (!requiredAuthGuard) return invocation.proceed();

        DataFetchingEnvironment env = extractEnvFromArgs(invocation.getArguments())
                .orElseThrow(() -> new AuthenticationInterceptException(
                        "method " + method.getName() + " is marked " +
                        "as @Authenticated, but no DataFetchingEnvironment found")
                );

        if (!Utils.getAuthToken(env).isPresent()) {
            return Error.of(ErrorCode.Authenticate, "用户未登录");
        } else return invocation.proceed();
    };

    public static String unwrap(DataFetchingEnvironment env) {
        //noinspection OptionalGetWithoutIsPresent
        return Utils.getAuthToken(env).get();
    }

    private static Optional<DataFetchingEnvironment> extractEnvFromArgs(Object[] args) {
        for (val arg : args) {
            if (DataFetchingEnvironment.class.isAssignableFrom(arg.getClass())) {
                return Optional.of((DataFetchingEnvironment) arg);
            }
        }
        return Optional.empty();
    }
}
