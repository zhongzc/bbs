package com.gaufoo.bbs.application.error;

public enum ErrorCode {
    Authenticate,

    LostItemNonExist,
    FoundItemNonExist,
    PostNonExist,
    ResourceNonExist,
    UserNonExist,

    CreateUserFailed,
    CreateCommentFailed,
    CreateReplyFailed,

    PublishResourceFailed,

    ParseMajorFailed,
    ParseAcademyFailed,
    ParseGenderFailed,
    ModifyPersonInfoFailed,
}
