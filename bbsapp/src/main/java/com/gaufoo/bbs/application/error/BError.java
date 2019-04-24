package com.gaufoo.bbs.application.error;

import com.gaufoo.bbs.application.types.Authentication;
import com.gaufoo.bbs.application.types.PersonalInformation;

public class BError implements
        PersonalInformation.PersonInfoResult,
        PersonalInformation.EditPersonInfoResult,
        Authentication.CurrentUserResult,
        Authentication.SignupResult
{
    private Integer errCode;
    private String msg;

    private BError(Integer errorCode) {
        this.errCode = errorCode;
    }

    public static BError of(ErrorCode errorCode) {
        return new BError(errorCode.innerVal);
    }

    public Integer getErrCode() {
        return errCode;
    }

    public String getMsg() {
        return ErrorCode.getMapping().get(errCode);
    }
}
