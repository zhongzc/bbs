package com.gaufoo.bbs.components.authenticator.exceptions;

public class PasswordInvalid extends AuthenticatorException {
    public PasswordInvalid(String errorMessage) {
        super(errorMessage);
    }
}
