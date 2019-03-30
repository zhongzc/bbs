package com.gaufoo.bbs.components.authenticator;

import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.ResetToken;
import com.gaufoo.bbs.components.authenticator.common.UserToken;

public interface AuthenticatorRepository {

    boolean contains(String username);

    boolean saveUser(String username, String password, Permission permission);

    boolean saveUserToken(UserToken token, Permission permission);

    boolean saveResetToken(ResetToken token, String username);

    String getPassword(String username);

    boolean setPassword(String username, String password);

    Permission getPermissionByUsername(String username);

    Permission getPermissionByToken(UserToken token);

    String getUsernameByResetToken(ResetToken token);

    void deleteUserToken(UserToken token);

    void deleteUserTokenByUsername(String username);

    void deleteResetToken(ResetToken token);

    String getRepositoryName();
}
