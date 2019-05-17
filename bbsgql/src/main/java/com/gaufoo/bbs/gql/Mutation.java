package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.*;
import com.gaufoo.bbs.application.AppAccountAndPassword;
import com.gaufoo.bbs.application.AppAuthentication;
import com.gaufoo.bbs.application.AppPersonalInformation;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.*;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

import static com.gaufoo.bbs.application.types.AccountAndPassword.ChangePasswordResult;
import static com.gaufoo.bbs.application.types.AccountAndPassword.ConfirmPasswordResult;
import static com.gaufoo.bbs.application.types.Authentication.*;
import static com.gaufoo.bbs.application.types.Found.*;
import static com.gaufoo.bbs.application.types.PersonalInformation.EditPersonInfoResult;
import static com.gaufoo.bbs.application.types.PersonalInformation.PersonInfoInput;

public class Mutation implements GraphQLMutationResolver {
    public Boolean reset() {
        AppFound.reset();
        AppLost.reset();
        AppSchoolHeat.reset();
        AppLecture.reset();
        AppLearningResource.reset();
        AppEntertainment.reset();
        AppNews.reset();
        return true;
    }

    public EditPersonInfoResult editPersonInfo(PersonInfoInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                userToken -> AppPersonalInformation.editPersonInfo(input, userToken)
        ).orElse(authError);
    }

    public SignupResult signup(SignupInput input) {
        return AppAuthentication.signup(input);
    }

    public LoginResult login(LoginInput input) {
        return AppAuthentication.login(input);
    }

    public LogoutResult logout(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                AppAuthentication::logout
        ).orElse(authError);
    }

    public ConfirmPasswordResult confirmPassword(LoginInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppAccountAndPassword.confirmPassword(input, tkn)
        ).orElse(authError);
    }

    public ChangePasswordResult changePassword(String newPassword, String resetToken, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppAccountAndPassword.changePassword(newPassword, resetToken)
        ).orElse(authError);
    }

    public News.CreateNewsResult createNews(News.NewsInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppNews.createNews(input, tkn)
        ).orElse(authError);
    }

    public News.EditNewsResult editNews(String id, News.NewsInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppNews.editNews(id, input, tkn)
        ).orElse(authError);
    }

    public News.DeleteNewsResult deleteNews(String id, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppNews.deleteNews(id, tkn)
        ).orElse(authError);
    }

    public CreateFoundResult createFound(FoundInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppFound.createFound(input, tkn)
        ).orElse(authError);
    }

    public DeleteFoundResult deleteFound(String foundId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppFound.deleteFound(foundId, tkn)
        ).orElse(authError);
    }

    public ClaimFoundResult claimFound(String foundId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppFound.claimFound(foundId, tkn)
        ).orElse(authError);
    }

    public CancelClaimFoundResult cancelClaimFound(String foundId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppFound.cancelClaimFound(foundId, tkn)
        ).orElse(authError);
    }

    public Lost.CreateLostResult createLost(Lost.LostInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLost.createLost(input, tkn)
        ).orElse(authError);
    }

    public Lost.DeleteLostResult deleteLost(String lostId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLost.deleteLost(lostId, tkn)
        ).orElse(authError);
    }

    public Lost.ClaimLostResult claimLost(String lostId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLost.claimLost(lostId, tkn)
        ).orElse(authError);
    }

    public Lost.CancelClaimLostResult cancelClaimLost(String lostId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLost.cancelClaimLost(lostId, tkn)
        ).orElse(authError);
    }

    public SchoolHeat.CreateSchoolHeatResult createSchoolHeat(SchoolHeat.SchoolHeatInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppSchoolHeat.createSchoolHeat(input, tkn)
        ).orElse(authError);
    }

    public SchoolHeat.DeleteSchoolHeatResult deleteSchoolHeat(String id, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppSchoolHeat.deleteSchoolHeat(id, tkn)
        ).orElse(authError);
    }

    public SchoolHeat.CreateSchoolHeatCommentResult createSchoolHeatComment(SchoolHeat.SchoolHeatCommentInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppSchoolHeat.createSchoolHeatComment(input, tkn)
        ).orElse(authError);
    }

    public SchoolHeat.DeleteSchoolHeatCommentResult deleteSchoolHeatComment(String schoolHeatId, String commengId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppSchoolHeat.deleteSchoolHeatComment(schoolHeatId, commengId, tkn)
        ).orElse(authError);
    }

    public SchoolHeat.CreateSchoolHeatCommentReplyResult createSchoolHeatCommentReply(SchoolHeat.SchoolHeatReplyInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppSchoolHeat.createSchoolHeatCommentReply(input, tkn)
        ).orElse(authError);
    }

    public SchoolHeat.DeleteSchoolHeatCommentReplyResult deleteSchoolHeatCommentReply(String schoolHeatId, String commentId, String replyId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppSchoolHeat.deleteSchoolHeatCommentReply(schoolHeatId, commentId, replyId, tkn)
        ).orElse(authError);
    }

    public Lecture.CreateLectureResult createLecture(Lecture.LectureInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLecture.createLecture(input, tkn)
        ).orElse(authError);
    }

    public Lecture.EditLectureResult editLecture(String lectureId, Lecture.LectureOptionalInput lectureInput, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLecture.editLecture(lectureId, lectureInput, tkn)
        ).orElse(authError);
    }

    public Lecture.DeleteLectureResult deleteLecture(String lectureId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLecture.deleteLecture(lectureId, tkn)
        ).orElse(authError);
    }

    public LearningResource.CreateLearningResourceResult createLearningResource(LearningResource.LearningResourceInput learningResourceInput, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLearningResource.createLearningResource(learningResourceInput, tkn)
        ).orElse(authError);
    }

    public LearningResource.DeleteLearningResourceResult deleteLearningResource(String learningResourceId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLearningResource.deleteLearningResource(learningResourceId, tkn)
        ).orElse(authError);
    }

    public LearningResource.CreateLearningResourceCommentResult createLearningResourceComment(LearningResource.LearningResourceCommentInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLearningResource.createLearningResourceComment(input, tkn)
        ).orElse(authError);
    }

    public LearningResource.DeleteLearningResourceCommentResult deleteLearningResourceComment(String learningResourceId, String commentId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLearningResource.deleteLearningResourceComment(learningResourceId, commentId, tkn)
        ).orElse(authError);
    }

    public LearningResource.CreateLearningResourceCommentReplyResult createLearningResourceCommentReply(LearningResource.LearningResourceReplyInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLearningResource.createLearningResourceCommentReply(input, tkn)
        ).orElse(authError);
    }

    public LearningResource.DeleteLearningResourceCommentReplyResult deleteLearningResourceCommentReply(String learnResourceId, String commentIdStr, String replyIdStr,  DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLearningResource.deleteLearningResourceCommentReply(learnResourceId, commentIdStr, replyIdStr, tkn)
        ).orElse(authError);
    }

    public Entertainment.CreateEntertainmentResult createEntertainment(Entertainment.EntertainmentInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppEntertainment.createEntertainment(input, tkn)
        ).orElse(authError);
    }

    public Entertainment.DeleteEntertainmentResult deleteEntertainment(String entertainmentId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppEntertainment.deleteEntertainment(entertainmentId, tkn)
        ).orElse(authError);
    }

    public Entertainment.CreateEntertainmentCommentResult createEntertainmentComment(Entertainment.EntertainmentCommentInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppEntertainment.createEntertainmentComment(input, tkn)
        ).orElse(authError);
    }

    public Entertainment.DeleteEntertainmentCommentResult deleteEntertainmentComment(String entertainmentId, String commentId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppEntertainment.deleteEntertainmentComment(entertainmentId, commentId, tkn)
        ).orElse(authError);
    }

    public Entertainment.CreateEntertainmentCommentReplyResult createEntertainmentCommentReply(Entertainment.EntertainmentReplyInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppEntertainment.createEntertainmentCommentReply(input, tkn)
        ).orElse(authError);
    }

    public Entertainment.DeleteEntertainmentCommentReplyResult deleteEntertainmentCommentReply(String entertainmentId, String commentId, String replyId, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppEntertainment.deleteEntertainmentCommentReply(entertainmentId, commentId, replyId, tkn)
        ).orElse(authError);
    }

    private static Error authError = Error.of(ErrorCode.NotLoggedIn);
}
