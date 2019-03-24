package com.gaufoo.bbs.components.authenticator.common;

public class ResetToken {
    private final String value;

    public ResetToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
