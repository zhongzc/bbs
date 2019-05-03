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
        Lecture.DeleteLectureResult,
        SchoolHeat.DeleteSchoolHeatResult,
        SchoolHeat.DeleteSchoolHeatCommentResult,
        SchoolHeat.DeleteSchoolHeatCommentReplyResult,
        LearningResource.DeleteLearningResourceResult,
        LearningResource.DeleteLearningResourceCommentResult,
        LearningResource.DeleteLearningResourceCommentReplyResult,
        Entertainment.DeleteEntertainmentResult,
        Entertainment.DeleteEntertainmentCommentResult,
        Entertainment.DeleteEntertainmentCommentReplyResult
{
    private Boolean ok;

    public static Ok build() {
        return new Ok();
    }

    Boolean getOk() {
        return true;
    }
}
