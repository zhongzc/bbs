package com.gaufoo.bbs.application.error;

import com.gaufoo.bbs.application.types.*;

public class Error implements
        PersonalInformation.PersonInfoResult,
        PersonalInformation.EditPersonInfoResult,
        Authentication.CurrentUserResult,
        Authentication.SignupResult,
        Authentication.LoginResult,
        Authentication.LogoutResult,
        AccountAndPassword.ConfirmPasswordResult,
        AccountAndPassword.ChangePasswordResult,
        Found.FoundInfoResult,
        Found.CreateFoundResult,
        Found.DeleteFoundResult,
        Found.ClaimFoundResult,
        Found.CancelClaimFoundResult,
        Lost.LostInfoResult,
        Lost.CreateLostResult,
        Lost.DeleteLostResult,
        Lost.ClaimLostResult,
        Lost.CancelClaimLostResult,
        Lecture.LectureInfoResult,
        Lecture.CreateLectureResult,
        Lecture.EditLectureResult,
        Lecture.DeleteLectureResult,
        SchoolHeat.CreateSchoolHeatResult,
        SchoolHeat.DeleteSchoolHeatResult,
        SchoolHeat.SchoolHeatInfoResult,
        SchoolHeat.SchoolHeatsOfAuthorResult,
        SchoolHeat.CreateSchoolHeatCommentResult,
        SchoolHeat.DeleteSchoolHeatCommentResult,
        SchoolHeat.CreateSchoolHeatCommentReplyResult,
        SchoolHeat.DeleteSchoolHeatCommentReplyResult,
        LearningResource.LearningResourceInfoResult,
        LearningResource.CreateLearningResourceResult,
        LearningResource.DeleteLearningResourceResult,
        LearningResource.CreateLearningResourceCommentResult,
        LearningResource.DeleteLearningResourceCommentResult,
        LearningResource.AllLearningResourceResult,
        LearningResource.CreateLearningResourceCommentReplyResult,
        LearningResource.DeleteLearningResourceCommentReplyResult,
        LearningResource.LearningResourcesOfAuthorResult,
        Entertainment.CreateEntertainmentResult,
        Entertainment.DeleteEntertainmentResult,
        Entertainment.EntertainmentInfoResult,
        Entertainment.EntertainmentsOfAuthorResult,
        Entertainment.CreateEntertainmentCommentResult,
        Entertainment.DeleteEntertainmentCommentResult,
        Entertainment.CreateEntertainmentCommentReplyResult,
        Entertainment.DeleteEntertainmentCommentReplyResult,
        Latest.LatestsResult,
        News.NewsInfoResult,
        News.CreateNewsResult,
        News.DeleteNewsResult,
        News.EditNewsResult
{
    private Integer errCode;
    private String msg;

    private Error(Integer errorCode) {
        this.errCode = errorCode;
    }

    public static Error of(ErrorCode errorCode) {
        return new Error(errorCode.innerVal);
    }

    public Integer getErrCode() {
        return errCode;
    }

    public String getMsg() {
        return ErrorCode.getMapping().get(errCode);
    }
}
