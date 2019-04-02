package com.gaufoo.bbs.application.resTypes;

public class LogInPayload implements LogInResult {
    private String token;

    public LogInPayload(String token) {
        this.token = token;
    }

    public static LogInPayload of(String token) {
        return new LogInPayload(token);
    }

    public String getToken() {
        return token;
    }
}
