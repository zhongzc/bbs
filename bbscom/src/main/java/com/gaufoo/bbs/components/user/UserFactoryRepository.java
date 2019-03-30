package com.gaufoo.bbs.components.user;

import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;

public interface UserFactoryRepository {
    boolean saveUser(UserId id, UserInfo userInfo);

    UserInfo getUserInfo(UserId userId);

    boolean updateUser(UserId userId, UserInfo info);

    void deleteUser(UserId userId);

    String getRepositoryName();
}
