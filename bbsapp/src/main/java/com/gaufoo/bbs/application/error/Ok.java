package com.gaufoo.bbs.application.error;

import com.gaufoo.bbs.application.types.Authentication;

public class Ok implements
        Authentication.LogoutResult
{
    private Boolean ok;

    public static Ok build() {
        return new Ok();
    }

    Boolean getOk() {
        return true;
    }
}
