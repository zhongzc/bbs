package com.gaufoo.bbs.application.error;

import com.gaufoo.bbs.application.types.AccountAndPassword;
import com.gaufoo.bbs.application.types.Authentication;
import com.gaufoo.bbs.application.types.Found;
import com.gaufoo.bbs.application.types.Lost;

public class Ok implements
        Authentication.LogoutResult,
        AccountAndPassword.ChangePasswordResult,
        Found.DeleteFoundResult,
        Found.ClaimFoundResult,
        Found.CancelClaimFoundResult,
        Lost.DeleteLostResult,
        Lost.ClaimLostResult,
        Lost.CancelClaimLostResult
{
    private Boolean ok;

    public static Ok build() {
        return new Ok();
    }

    Boolean getOk() {
        return true;
    }
}
