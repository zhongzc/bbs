package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.*;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.LearningResource;
import com.gaufoo.bbs.application.types.Lecture;
import com.gaufoo.bbs.application.types.Lost;
import com.gaufoo.bbs.application.types.SchoolHeat;
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
        return true;
    }

    public EditPersonInfoResult editPersonInfo(PersonInfoInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                userToken -> PersonalInformation.editPersonInfo(input, userToken)
        ).orElse(authError);
    }

    public SignupResult signup(SignupInput input) {
        return Authentication.signup(input);
    }

    public LoginResult login(LoginInput input) {
        return Authentication.login(input);
    }

    public LogoutResult logout(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                Authentication::logout
        ).orElse(authError);
    }

    public ConfirmPasswordResult confirmPassword(LoginInput input, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AccountAndPassword.confirmPassword(input, tkn)
        ).orElse(authError);
    }

    public ChangePasswordResult changePassword(String newPassword, String resetToken, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AccountAndPassword.changePassword(newPassword, resetToken)
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

    public LearningResource.DeleteLearningResourceCommentReplyResult deleteLearningResourceCommentReply(String learnResourceId, String commentIdStr,
                                                                                                        String replyIdStr,  DataFetchingEnvironment env) {
        return Utils.getAuthToken(env).map(
                tkn -> AppLearningResource.deleteLearningResourceCommentReply(learnResourceId, commentIdStr, replyIdStr, tkn)
        ).orElse(authError);
    }

    private static Error authError = Error.of(ErrorCode.NotLoggedIn);
}
