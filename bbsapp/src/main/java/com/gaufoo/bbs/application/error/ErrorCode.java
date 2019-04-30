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
    CancelClaimFailed(32),
    LostPostNonExist(33),
    CreateLostImageFailed(34),
    PublishLostFailed(35),
    DeleteLostFailed(36),
    ClaimLostFailed(37),
    LectureNotfound(38),
    CreateContentFailed(39),
    PublishLectureFailed(40),
    DeleteLectureFailed(41),
    UpdateLectureFailed(42),
    ParseCourseError(43),
    CreateCommentGroupFailed(44),
    PublishLearningResourceFailed(45),
    ClearActiveAndHeatFailed(46),
    CreateActiveAndHeatFailed(47),
    LearningResourceNonExist(48),
    DeleteLearningResourceFailed(49),
    DeleteContentFailed(50),
    AddCommentFailed(51),
    CommentInfoNotFound(52),
    DeleteCommentFailed(53),
    AlterHeatFailed(54),
    LatestActiveNotFound(55),
    UnableToTouch(56),
    AddReplyFailed(57),
    ReplyInfoNotFound(58),
    DeleteReplyFailed(59),
    PublishEntertainmentFailed(60),

    PostNonExist(96),
    ContentNonExist(97),
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
        put(ErrorCode.FoundPostNonExist, "找不到寻物");
        put(ErrorCode.CreateFoundImageFailed, "创建寻物图片失败");
        put(ErrorCode.PublishFoundFailed, "创建寻物失败");
        put(ErrorCode.DeleteFoundFailed, "删除寻物失败");
        put(ErrorCode.ClaimFoundFailed, "认领寻物失败");
        put(ErrorCode.PermissionDenied, "无操作权限");
        put(ErrorCode.CancelClaimFailed, "取消认领错误");
        put(ErrorCode.LostPostNonExist, "找不到失物");
        put(ErrorCode.CreateLostImageFailed, "创建失物图片失败");
        put(ErrorCode.PublishLostFailed, "创建失物失败");
        put(ErrorCode.DeleteLostFailed, "删除失物物失败");
        put(ErrorCode.ClaimLostFailed, "认领失物失败");
        put(ErrorCode.LectureNotfound, "找不到讲座");
        put(ErrorCode.CreateContentFailed, "无法创建文本");
        put(ErrorCode.PublishLectureFailed, "发布讲座信息失败");
        put(ErrorCode.DeleteLectureFailed, "删除讲座信息失败");
        put(ErrorCode.UpdateLectureFailed, "更新讲座信息失败");
        put(ErrorCode.ParseCourseError, "无法解析课程");
        put(ErrorCode.CreateCommentGroupFailed, "创建评论组失败");
        put(ErrorCode.PublishLearningResourceFailed, "发布学习资源失败");
        put(ErrorCode.ClearActiveAndHeatFailed, "清除活跃或热度失败");
        put(ErrorCode.CreateActiveAndHeatFailed, "创建活跃或热度失败");
        put(ErrorCode.LearningResourceNonExist, "学习资源不存在");
        put(ErrorCode.DeleteLearningResourceFailed, "删除学习资源失败");
        put(ErrorCode.DeleteContentFailed, "删除内容失败");
        put(ErrorCode.AddCommentFailed, "插入评论失败");
        put(ErrorCode.CommentInfoNotFound, "找不到评论信息");
        put(ErrorCode.DeleteCommentFailed, "删除评论失败");
        put(ErrorCode.AlterHeatFailed, "修改热度失败");
        put(ErrorCode.LatestActiveNotFound, "找不到最近活跃信息");
        put(ErrorCode.UnableToTouch, "无法更新最近活跃");
        put(ErrorCode.AddReplyFailed, "添加回复失败");
        put(ErrorCode.ReplyInfoNotFound, "找不到回复信息");
        put(ErrorCode.DeleteReplyFailed, "删除回复失败");
        put(ErrorCode.PublishEntertainmentFailed, "发布休闲娱乐帖子失败");

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
