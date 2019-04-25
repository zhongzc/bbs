package com.gaufoo.bbs.application.types;

public interface AccountAndPassword {
    interface ConfirmPasswordResult {}
    interface ChangePasswordResult {}

    interface ResetPassToken extends ConfirmPasswordResult {
        String getResetToken();
    }
}
