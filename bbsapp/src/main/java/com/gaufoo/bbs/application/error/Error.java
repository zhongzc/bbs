package com.gaufoo.bbs.application.error;

import com.gaufoo.bbs.application.*;

public class Error {
    private ErrorCode errCode;
    private String errMsg;

    private Error(ErrorCode errorCode, String errMsg) {
        this.errCode = errorCode;
        this.errMsg = errMsg;
    }

    public static Error of(ErrorCode errorCode) {
        return new Error(errorCode, "");
    }

    public static Error of(ErrorCode errorCode, String errorMsg) {
        return new Error(errorCode, errorMsg);
    }

    public ErrorCode getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
