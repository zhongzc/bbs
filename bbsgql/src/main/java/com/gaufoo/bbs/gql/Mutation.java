package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.gaufoo.bbs.application.AccountAndPassword;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.PersonalInformation;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.AppFound;
import com.gaufoo.bbs.gql.util.Utils;
import graphql.schema.DataFetchingEnvironment;

import static com.gaufoo.bbs.application.types.AccountAndPassword.ChangePasswordResult;
import static com.gaufoo.bbs.application.types.AccountAndPassword.ConfirmPasswordResult;
import static com.gaufoo.bbs.application.types.Authentication.*;
import static com.gaufoo.bbs.application.types.PersonalInformation.EditPersonInfoResult;
import static com.gaufoo.bbs.application.types.PersonalInformation.PersonInfoInput;
import static com.gaufoo.bbs.application.types.Found.*;

public class Mutation implements GraphQLMutationResolver {
    public Boolean reset() {
        AppFound.reset();
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

    private static Error authError = Error.of(ErrorCode.NotLoggedIn);
}
