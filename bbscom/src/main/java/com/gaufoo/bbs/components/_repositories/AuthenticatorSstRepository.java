package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.authenticator.AuthenticatorRepository;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.sst.SST;

public class AuthenticatorSstRepository implements AuthenticatorRepository {
    SST sst = SST.of("it works!");

    @Override
    public boolean contains(String username) {
        return false;
    }

    @Override
    public boolean saveUser(String username, String password, Permission permission) {
        return false;
    }

    @Override
    public boolean saveUserToken(UserToken token, Permission permission) {
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
        return null;
    }
}
