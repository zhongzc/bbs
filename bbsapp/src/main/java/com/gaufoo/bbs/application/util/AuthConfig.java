package com.gaufoo.bbs.application.util;

import com.gaufoo.bbs.components.validator.Validator;

public class AuthConfig {
    public static Validator<String> usernameVal() {
        return Validator.email();
    }

    public static Validator<String> passwordVal() {
        return Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20));
    }
}
