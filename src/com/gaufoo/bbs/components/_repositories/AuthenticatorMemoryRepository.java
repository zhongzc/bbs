package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.authenticator.AuthenticatorRepository;
import com.gaufoo.bbs.components.authenticator.common.*;
import com.gaufoo.bbs.util.Tuple;

import java.util.Hashtable;
import java.util.Map;

public class AuthenticatorMemoryRepository implements AuthenticatorRepository {
    private final Map<String, Tuple<String, Permission>> usernameToPwdPermission = new Hashtable<>();
    private final Map<UserToken, Permission> userTokenToPermission = new Hashtable<>();
    private final Map<ResetToken, String> resetTokenToUsername = new Hashtable<>();

    private AuthenticatorMemoryRepository() { }

    @Override
    public boolean contains(String username) {
        return usernameToPwdPermission.containsKey(username);
    }

    @Override
    public boolean saveUser(String username, String password, Permission permission) {
        usernameToPwdPermission.put(username, Tuple.of(password, permission));
        return true;
    }

    @Override
    public boolean saveUserToken(UserToken token, Permission permission) {
        userTokenToPermission.put(token, permission);
        return true;
    }

    @Override
    public boolean saveResetToken(ResetToken token, String username) {
        resetTokenToUsername.put(token, username);
        return true;
    }

    @Override
    public String getPassword(String username) {
        Tuple<String, Permission> t =  usernameToPwdPermission.get(username);
        if (t == null) return null;
        else return t.x;
    }

    @Override
    public boolean setPassword(String username, String password) {
        Tuple<String, Permission> t =  usernameToPwdPermission.get(username);
        if (t == null) return false;
        usernameToPwdPermission.replace(username, Tuple.of(password, t.y));

        return true;
    }

    @Override
    public Permission getPermissionByUsername(String username) {
        Tuple<String, Permission> t =  usernameToPwdPermission.get(username);
        if (t == null) return null;
        else return t.y;
    }

    @Override
    public Permission getPermissionByToken(UserToken token) {
        return userTokenToPermission.get(token);
    }

    @Override
    public String getUsernameByResetToken(ResetToken token) {
        return resetTokenToUsername.get(token);
    }

    @Override
    public void deleteUserToken(UserToken token) {
        userTokenToPermission.remove(token);
    }

    @Override
    public void deleteResetToken(ResetToken token) {
        resetTokenToUsername.remove(token);
    }

    private static AuthenticatorMemoryRepository instance = new AuthenticatorMemoryRepository();

    public static AuthenticatorRepository get() {
        return instance;
    }
}
