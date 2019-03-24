package com.gaufoo.bbs.components.authenticator.common;

public class UserToken {
    private final String value;

    public UserToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
