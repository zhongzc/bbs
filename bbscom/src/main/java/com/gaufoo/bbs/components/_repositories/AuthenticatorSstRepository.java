package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.AuthenticatorRepository;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.util.Tuple;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import static com.gaufoo.bbs.util.SstUtils.*;

public class AuthenticatorSstRepository implements AuthenticatorRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private final SST usernameToPasswordAndPermission;
    private final SST tokenToPermission;
    private final SST resetTokenToUsername;

    private AuthenticatorSstRepository(String repositoryName, Path storingDir) {
        this.repositoryName = repositoryName;
        String unamePwdPms = "username-password_permission";
        this.usernameToPasswordAndPermission = SST.of(unamePwdPms, storingDir.resolve(unamePwdPms));

        String tknPms = "token-permission";
        this.tokenToPermission = SST.of(tknPms, storingDir.resolve(tknPms));

        String rstTknUname = "resetToken-username";
        this.resetTokenToUsername = SST.of(rstTknUname, storingDir.resolve(rstTknUname));

    }

    @Override
    public boolean contains(String username) {
        return waitFuture(usernameToPasswordAndPermission.get(username)
                .thenApply(Optional::isPresent)).orElse(false);
    }

    @Override
    public boolean saveUser(String username, String password, Permission permission) {
        String passPermJson = gson.toJson(Tuple.of(password, permission));

        return setEntry(usernameToPasswordAndPermission, username, passPermJson);
    }

    @Override
    public boolean saveUserToken(UserToken token, Permission permission) {
        String permissionJson = gson.toJson(permission);

        return setEntry(tokenToPermission, token.value, permissionJson);
    }

    @Override
    public boolean saveResetToken(ResetToken token, String username) {
        return setEntry(resetTokenToUsername, token.value, username);
    }

    @Override
    public String getPassword(String username) {
        return getEntry(usernameToPasswordAndPermission, username,
                pwdPmsJson -> jsonToPwdPms(pwdPmsJson).left);
    }

    @Override
    public boolean setPassword(String username, String password) {
        Tuple<String, Permission> oldTup = getEntry(usernameToPasswordAndPermission, username,
                AuthenticatorSstRepository::jsonToPwdPms);

        if (oldTup == null) return false;
        String newTup = gson.toJson(oldTup.modLeft(password));

        return setEntry(usernameToPasswordAndPermission, username, newTup);
    }

    @Override
    public Permission getPermissionByUsername(String username) {
        return getEntry(usernameToPasswordAndPermission, username,
                pwdPmsJson -> jsonToPwdPms(pwdPmsJson).right);
    }

    @Override
    public Permission getPermissionByToken(UserToken token) {
        return getEntry(tokenToPermission, token.value,
                permStr -> gson.fromJson(permStr, Permission.class));
    }

    @Override
    public String getUsernameByResetToken(ResetToken token) {
        return getEntry(resetTokenToUsername, token.value, Function.identity());
    }

    @Override
    public void deleteUser(String username) {
        String permission = gson.toJson(getPermissionByUsername(username));

        if (permission != null) {
            removeEntryWithValue(tokenToPermission, permission);
        }

        removeEntryWithValue(resetTokenToUsername, username);

        removeEntryWithKey(usernameToPasswordAndPermission, username);
    }

    @Override
    public void deleteUserToken(UserToken token) {
        removeEntryWithKey(tokenToPermission, token.value);
    }

    @Override
    public void deleteUserTokenByUsername(String username) {
        String permission = gson.toJson(getPermissionByUsername(username));

        if (permission != null) removeEntryWithValue(tokenToPermission, permission);
    }

    @Override
    public void deleteResetToken(ResetToken token) {
        removeEntryWithKey(resetTokenToUsername, token.value);
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    private static Tuple<String, Permission> jsonToPwdPms(String json) {
        return gson.fromJson(json, new TypeToken<Tuple<String, Permission>>(){}.getType());
    }

    public static AuthenticatorSstRepository get(String repositoryName, Path storingDir) {
        return new AuthenticatorSstRepository(repositoryName, storingDir);
    }

    @Override
    public void shutdown() {
        waitAllFuturesPar(
                usernameToPasswordAndPermission.shutdown(),
                tokenToPermission.shutdown(),
                resetTokenToUsername.shutdown()
        );
    }

    public static void main(String[] args) {
        AuthenticatorSstRepository authenticatorSstRepository = new AuthenticatorSstRepository("tst", Paths.get("resources").resolve("tst"));
        UserToken userToken = UserToken.of("1234");
        boolean result = authenticatorSstRepository.saveUserToken(userToken, Permission.of("0000", Authenticator.Role.USER));
        System.out.println("saveUserToken: " + result);

        System.out.println(authenticatorSstRepository.getPermissionByToken(userToken));
    }
}
