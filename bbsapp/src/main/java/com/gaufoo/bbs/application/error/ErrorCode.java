package com.gaufoo.bbs.application.error;

import com.gaufoo.bbs.components.authenticator.common.AuthError;

import java.util.*;

public enum ErrorCode {
    UnknownError(0),
    UserNonExist(1),
    ParseGenderError(2),
    ChangeGenderError(3),
    UsernameInvalid(4),
    PasswordInvalid(5),
    UsernameDuplicate(6),
    RegisterFailed(7),
    UsernameNotFound(8),
    WrongPassword(9),
    LoginFailed(10),
    LoginTokenExpired(11),
    LoginInfoInvalid(12),
    RequestResetPasswordFailed(13),
    OperationTimedOut(14),
    ResetTokenInvalid(15),
    ResetFailed(16),
    ParseMajorFailed(17),
    ParseSchoolFailed(18),
    ChangeMajorFailed(19),
    ChangeSchoolFailed(20),
    ChangeImageFailed(21),
    NotLoggedIn(22),
    CreateUserFailed(23),
    AuthenticateFailed(24),
    FileNotFound(25),
    FoundPostNonExist(26),
    CreateFoundImageFailed(27),
    PublishFoundFailed(28),
    DeleteFoundFailed(29),
    ClaimFoundFailed(30),
    PermissionDenied(31),

    CreatePostFailed(98),
    UnsupportedOperation(99),
    SaveFileFailed(100)

    ;final Integer innerVal;
    ErrorCode(Integer bindingVal) {
        innerVal = bindingVal;
    }

    private final static Map<ErrorCode, String> mapping = new HashMap<ErrorCode, String>() {{
        put(ErrorCode.UserNonExist, "用户ID不存在");
        put(ErrorCode.ParseGenderError, "性别解析失败");
        put(ErrorCode.ChangeGenderError, "性别更改失败");
        put(ErrorCode.UsernameInvalid, "用户名不合法");
        put(ErrorCode.PasswordInvalid, "密码不合法");
        put(ErrorCode.UsernameDuplicate, "用户名已存在");
        put(ErrorCode.RegisterFailed, "注册失败");
        put(ErrorCode.UsernameNotFound, "找不到用户名");
        put(ErrorCode.WrongPassword, "密码错误");
        put(ErrorCode.LoginFailed, "登录失败");
        put(ErrorCode.LoginTokenExpired, "登录已过期");
        put(ErrorCode.LoginInfoInvalid, "登录信息无效");
        put(ErrorCode.RequestResetPasswordFailed, "请求重设密码失败");
        put(ErrorCode.OperationTimedOut, "操作超时");
        put(ErrorCode.ParseMajorFailed, "无法解析专业");
        put(ErrorCode.ParseSchoolFailed, "无法解析学院");
        put(ErrorCode.ChangeMajorFailed, "更改专业失败");
        put(ErrorCode.ChangeSchoolFailed, "更改学院失败");
        put(ErrorCode.ChangeImageFailed, "更改头像失败");
        put(ErrorCode.NotLoggedIn, "用户尚未登录");
        put(ErrorCode.CreateUserFailed, "创建用户失败");
        put(ErrorCode.AuthenticateFailed, "认证失败");
        put(ErrorCode.FileNotFound, "找不到文件");
        put(ErrorCode.FoundPostNonExist, "找不到失物");
        put(ErrorCode.CreateFoundImageFailed, "创建寻物图片失败");
        put(ErrorCode.PublishFoundFailed, "创建寻物失败");
        put(ErrorCode.DeleteFoundFailed, "删除寻物失败");
        put(ErrorCode.ClaimFoundFailed, "认领寻物失败");
        put(ErrorCode.PermissionDenied, "无操作权限");

        put(ErrorCode.SaveFileFailed, "创建文件失败");
        // add more error code mapping here
    }};

    public static Optional<ErrorCode> fromInteger(Integer integer) {
        for (ErrorCode ec : ErrorCode.values()) {
            if (ec.innerVal.equals(integer)) return Optional.of(ec);
        }
        return Optional.empty();
    }

    public static Map<Integer, String> getMapping() {
        Map<Integer, String> result = new LinkedHashMap<>();
        mapping.forEach((k, v) -> result.put(k.innerVal, v));
        return Collections.unmodifiableMap(result);
    }

    public static ErrorCode fromAuthError(AuthError authError) {
        switch (authError) {
            case UsernameInvalid: return UsernameInvalid;
            case PasswordInvalid: return PasswordInvalid;
            case UsernameDuplicate: return UsernameDuplicate;
            case RegisterFailed: return RegisterFailed;
            case UsernameNotFound: return UsernameNotFound;
            case WrongPassword: return WrongPassword;
            case LoginFailed: return LoginFailed;
            case LoginTokenExpired: return LoginTokenExpired;
            case LoginInfoInvalid: return LoginInfoInvalid;
            case RequestResetPasswordFailed: return RequestResetPasswordFailed;
            case OperationTimedOut: return OperationTimedOut;
            case ResetTokenInvalid: return ResetTokenInvalid;
            case ResetFailed: return ResetFailed;
            default: return UnknownError;
        }
    }
}
