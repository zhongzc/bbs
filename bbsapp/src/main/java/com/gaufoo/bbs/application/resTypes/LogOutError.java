package com.gaufoo.bbs.application.resTypes;

public class LogOutError {
    private String error;

    public LogOutError(String error) {
        this.error = error;
    }

    public static LogOutError of(String error) {
        return new LogOutError(error);
    }

    public String getError() {
        return error;
    }
}
