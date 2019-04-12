package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.AuthenticatorRepository;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class AuthenticatorSstRepository implements AuthenticatorRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private SST usernameToPasswordAndPermission = SST.of("username-password_permission");
    private SST tokenToPermission = SST.of("token-permission");
    private SST resetTokenToUsername = SST.of("resetToken-username");

    public AuthenticatorSstRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean contains(String username) {
        try {
            return usernameToPasswordAndPermission.get(username).thenApply(Optional::isPresent).toCompletableFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveUser(String username, String password, Permission permission) {
        return false;
    }

    @Override
    public boolean saveUserToken(UserToken token, Permission permission) {
        try {
            return tokenToPermission.set(token.value, gson.toJson(permission))
                    .thenApply(Objects::nonNull)
                    .toCompletableFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveResetToken(ResetToken token, String username) {
        return false;
    }

    @Override
    public String getPassword(String username) {
        return null;
    }

    @Override
    public boolean setPassword(String username, String password) {
        return false;
    }

    @Override
    public Permission getPermissionByUsername(String username) {
        return null;
    }

    @Override
    public Permission getPermissionByToken(UserToken token) {
        try {
            return tokenToPermission.get(token.value)
                    .thenApply(oStr -> oStr
                            .map(permissionStr -> gson.fromJson(permissionStr, Permission.class))
                            .orElse(null))
                    .toCompletableFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getUsernameByResetToken(ResetToken token) {
        return null;
    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void deleteUserToken(UserToken token) {

    }

    @Override
    public void deleteUserTokenByUsername(String username) {

    }

    @Override
    public void deleteResetToken(ResetToken token) {

    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    public static void main(String[] args) {
        AuthenticatorSstRepository authenticatorSstRepository = new AuthenticatorSstRepository("tst");
        UserToken userToken = UserToken.of("1234");
        boolean result = authenticatorSstRepository.saveUserToken(userToken, Permission.of("0000", Authenticator.Role.USER));
        System.out.println("saveUserToken: " + result);

        System.out.println(authenticatorSstRepository.getPermissionByToken(userToken));

    }
}
