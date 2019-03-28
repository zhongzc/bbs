package com.gaufoo.bbs.application.resTypes;

public class SignUpPayload implements SignUpResult {
    private final String token;

    public SignUpPayload(String token) {
        this.token = token;
    }

    public static SignUpPayload of(String token) {
        return new SignUpPayload(token);
    }

    public String getToken() {
        return token;
    }
}
