package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components.authenticator.common.*;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.validator.Validator;

import java.time.*;

import static com.gaufoo.bbs.util.TaskChain.*;

class AuthenticatorImpl implements Authenticator {
    private final AuthenticatorRepository repository;
    private final Validator<String> usernameValidator;
    private final Validator<String> passwordValidator;
    private final TokenGenerator tokenGenerator;

    AuthenticatorImpl(AuthenticatorRepository repository,
                      Validator<String> usernameValidator,
                      Validator<String> passwordValidator,
                      TokenGenerator tokenGenerator) {
        this.repository = repository;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Procedure<AuthError, Attachable> signUp(String username, String password) {
        if (!usernameValidator.validate(username)) return Fail.of(AuthError.UsernameInvalid);
        if (!passwordValidator.validate(password)) return Fail.of(AuthError.PasswordInvalid);
        if (repository.contains(username)) return Fail.of(AuthError.UsernameDuplicate);

        return Result.of(permission -> {
            if (!repository.saveUser(username, password, permission)) return Fail.of(AuthError.RegisterFailed);
            else return Result.of(true);
        });
    }

    @Override
    public Procedure<AuthError, Attachable> createSuperUser(String username, String password) {
        if (repository.contains(username)) return Fail.of(AuthError.UsernameDuplicate);
        return Result.of(permission -> {
            if (!repository.saveUser(username, password, permission)) return Fail.of(AuthError.ResetFailed);
            else return Result.of(true);
        });
    }

    @Override
    public Procedure<AuthError, UserToken> login(String username, String password) {
        if (!repository.contains(username)) return Fail.of(AuthError.UsernameNotFound);
        if (!repository.getPassword(username).equals(password)) return Fail.of(AuthError.WrongPassword);

        Permission permission = repository.getPermissionByUsername(username);
        String userId = permission.userId;
        String token = tokenGenerator.genToken(userId, Instant.now().plus(Duration.ofDays(30)));
        UserToken userToken = UserToken.of(token);

        if (!repository.saveUserToken(userToken, permission)) return Fail.of(AuthError.LoginFailed);

        return Result.of(userToken);
    }

    @Override
    public Procedure<AuthError, Permission> getLoggedUser(UserToken userToken) {
        if (tokenGenerator.isExpired(userToken.value)) {
            repository.deleteUserToken(userToken);
            return Fail.of(AuthError.LoginTokenExpired);
        }
        Permission permission = repository.getPermissionByToken(userToken);
        return Procedure.ofNullable(permission, AuthError.LoginInfoInvalid);
    }


    @Override
    public void logout(UserToken userToken) {
        tokenGenerator.expire(userToken.value);
        repository.deleteUserToken(userToken);
    }

    @Override
    public Procedure<AuthError, ResetToken> reqResetPassword(String username) {
        if (!repository.contains(username)) return Fail.of(AuthError.UsernameNotFound);

        String token = tokenGenerator.genToken(username, Instant.now().plus(Duration.ofHours(1)));
        ResetToken resetToken = ResetToken.of(token);
        if (!repository.saveResetToken(resetToken, username)) {
            return Fail.of(AuthError.RequestResetPasswordFailed);
        }

        return Result.of(resetToken);
    }

    @Override
    public Procedure<AuthError, Boolean> resetPassword(ResetToken resetToken, String newPassword) {
        if (tokenGenerator.isExpired(resetToken.value)) {
            repository.deleteResetToken(resetToken);
            return Fail.of(AuthError.OperationTimedOut);
        }

        String username = repository.getUsernameByResetToken(resetToken);
        if (username == null) return Fail.of(AuthError.ResetTokenInvalid);
        if (!passwordValidator.validate(newPassword)) return Fail.of(AuthError.PasswordInvalid);

        if (!repository.setPassword(username, newPassword)) return Fail.of(AuthError.ResetFailed);
        repository.deleteResetToken(resetToken);
        repository.deleteUserTokenByUsername(username);
        return Result.of(true);
    }

    @Override
    public void remove(String username) {
        repository.deleteUser(username);
    }

    @Override
    public boolean isAuthenticated(String username, String password) {
        return repository.contains(username) && repository.getPassword(username).equals(password);
    }

}
