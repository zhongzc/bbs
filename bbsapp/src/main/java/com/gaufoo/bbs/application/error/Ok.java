package com.gaufoo.bbs.application.error;

import com.gaufoo.bbs.application.types.*;

public class Ok implements
        Authentication.LogoutResult,
        AccountAndPassword.ChangePasswordResult,
        Found.DeleteFoundResult,
        Found.ClaimFoundResult,
        Found.CancelClaimFoundResult,
        Lost.DeleteLostResult,
        Lost.ClaimLostResult,
        Lost.CancelClaimLostResult,
        Lecture.DeleteLectureResult
{
    private Boolean ok;

    public static Ok build() {
        return new Ok();
    }

    Boolean getOk() {
        return true;
    }
}
