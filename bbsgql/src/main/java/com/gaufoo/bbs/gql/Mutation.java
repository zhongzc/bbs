package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.AccountAndPassword;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

@Component
public class Mutation implements GraphQLMutationResolver {
    String test(String testStr, DataFetchingEnvironment env) {
        return testStr;
    }

    Authentication.SignUpResult signUp(String username, String password, String nickname, DataFetchingEnvironment env) {
        return Authentication.signUp(username, password, nickname);
    }

    Authentication.LogInResult logIn(String username, String password) {
        return Authentication.logIn(username, password);
    }

    Authentication.LogOutError logOut(DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(Authentication::logOut)
                .orElse(Authentication.LogOutError.of("用户未登录"));
    }

    AccountAndPassword.ConfirmPasswordResult confirmPassword(String username, String password, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(tkn -> AccountAndPassword.confirmPassword(tkn, username, password))
                .orElse(AccountAndPassword.ConfirmPasswordError.of("用户未登录"));
    }

    AccountAndPassword.ChangePasswordError changePassword(String resetToken, String newPassword, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken ->
                    AccountAndPassword.changePassword(userToken, resetToken, newPassword))
                .orElse(AccountAndPassword.ChangePasswordError.of("用户未登录"));
    }

    PersonalInformation.ModifyPersonInfoError uploadUserProfile(String base64Image, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken -> PersonalInformation.uploadUserProfile(userToken, base64Image))
                .orElse(PersonalInformation.ModifyPersonInfoError.of("用户未登录"));
    }

    PersonalInformation.ModifyPersonInfoError changeAcademy(String academy, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken -> PersonalInformation.changeAcademy(userToken, academy))
                .orElse(PersonalInformation.ModifyPersonInfoError.of("用户未登录"));
    }

    PersonalInformation.ModifyPersonInfoError changeMajor(String major, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken -> PersonalInformation.changeMajor(userToken, major))
                .orElse(PersonalInformation.ModifyPersonInfoError.of("用户未登录"));
    }

    PersonalInformation.ModifyPersonInfoError changeGender(String gender, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken -> PersonalInformation.changeGender(userToken, gender))
                .orElse(PersonalInformation.ModifyPersonInfoError.of("用户未登录"));
    }

    PersonalInformation.ModifyPersonInfoError changeGrade(String grade, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken -> PersonalInformation.changeGrade(userToken, grade))
                .orElse(PersonalInformation.ModifyPersonInfoError.of("用户未登录"));
    }

    PersonalInformation.ModifyPersonInfoError changeIntroduction(String introduction, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken -> PersonalInformation.changeIntroduction(userToken, introduction))
                .orElse(PersonalInformation.ModifyPersonInfoError.of("用户未登录"));
    }

    PersonalInformation.ModifyPersonInfoError changeNickname(String nickname, DataFetchingEnvironment env) {
        return Utils.getAuthToken(env)
                .map(userToken -> PersonalInformation.changeNickname(userToken, nickname))
                .orElse(PersonalInformation.ModifyPersonInfoError.of("用户未登录"));
    }
}
