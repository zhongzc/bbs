package com.gaufoo.bbs.application.resTypes;

public class SignUpError implements SignUpResult {
    private final String error;

    public SignUpError(String error) {
        this.error = error;
    }

    public static SignUpError of(String error) {
        return new SignUpError(error);
    }

    public String getError() {
        return error;
    }
}
