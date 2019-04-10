package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.AccountAndPassword;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.LostAndFound;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

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
        return authenticatedGuard(Authentication::logOut, Authentication.LogOutError::of, env);
    }

    AccountAndPassword.ConfirmPasswordResult confirmPassword(String username, String password, DataFetchingEnvironment env) {
        return authenticatedGuard(
                tkn -> AccountAndPassword.confirmPassword(tkn, username, password),
                AccountAndPassword.ConfirmPasswordError::of, env);
    }

    AccountAndPassword.ChangePasswordError changePassword(String resetToken, String newPassword, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> AccountAndPassword.changePassword(userToken, resetToken, newPassword),
                AccountAndPassword.ChangePasswordError::of, env);
    }

    PersonalInformation.ModifyPersonInfoResult uploadUserProfile(String base64Image, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> PersonalInformation.uploadUserProfile(userToken, base64Image),
                PersonalInformation.ModifyPersonInfoError::of, env);
    }

    PersonalInformation.ModifyPersonInfoResult changeAcademy(String academy, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> PersonalInformation.changeAcademy(userToken, academy),
                PersonalInformation.ModifyPersonInfoError::of, env);
    }

    PersonalInformation.ModifyPersonInfoResult changeMajor(String major, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> PersonalInformation.changeMajor(userToken, major),
                PersonalInformation.ModifyPersonInfoError::of, env);
    }

    PersonalInformation.ModifyPersonInfoResult changeGender(String gender, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> PersonalInformation.changeGender(userToken, gender),
                PersonalInformation.ModifyPersonInfoError::of, env);
    }

    PersonalInformation.ModifyPersonInfoResult changeGrade(String grade, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> PersonalInformation.changeGrade(userToken, grade),
                PersonalInformation.ModifyPersonInfoError::of, env);
    }

    PersonalInformation.ModifyPersonInfoResult changeIntroduction(String introduction, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> PersonalInformation.changeIntroduction(userToken, introduction),
                PersonalInformation.ModifyPersonInfoError::of, env);
    }

    PersonalInformation.ModifyPersonInfoResult changeNickname(String nickname, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> PersonalInformation.changeNickname(userToken, nickname),
                PersonalInformation.ModifyPersonInfoError::of, env);
    }


    LostAndFound.ItemInfoResult publishFound(LostAndFound.ItemInfoInput itemInfo, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> LostAndFound.publishFound(userToken, itemInfo),
                LostAndFound.ItemInfoError::of, env);
    }

    LostAndFound.ItemInfoResult publishLost(LostAndFound.ItemInfoInput itemInfo, DataFetchingEnvironment env) {
        return authenticatedGuard(
                userToken -> LostAndFound.publishLost(userToken, itemInfo),
                LostAndFound.ItemInfoError::of, env);
    }

    private static <E> E authenticatedGuard(Function<String, E> transformer, Function<String, E> errorConstructor,
                                            DataFetchingEnvironment env) {
        Optional<String> oToken = Utils.getAuthToken(env);
        if (!oToken.isPresent()) return errorConstructor.apply("用户未登录");
        return transformer.apply(oToken.get());
    }
}
