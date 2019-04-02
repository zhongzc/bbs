package com.gaufoo.bbs.application.resTypes;

public class LogInError implements LogInResult {
    private String error;

    public LogInError(String error) {
        this.error = error;
    }

    public static LogInError of(String error) {
        return new LogInError(error);
    }

    public String getError() {
        return error;
    }
}
