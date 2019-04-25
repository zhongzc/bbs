package com.gaufoo.bbs.components.user;

import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;

import static com.gaufoo.bbs.util.SstUtils.*;

public class UserFactorySstRepository implements UserFactoryRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;

    private UserFactorySstRepository(Path storingDir) {
        idToInfo = SST.of("user", storingDir);
    }

    @Override
    public boolean saveUser(UserId id, UserInfo userInfo) {
        return setEntry(idToInfo, id.value, gson.toJson(userInfo));
    }

    @Override
    public UserInfo getUserInfo(UserId userId) {
        return getEntry(idToInfo, userId.value,
                userInfo -> gson.fromJson(userInfo, UserInfo.class));
    }

    @Override
    public boolean updateUser(UserId userId, UserInfo info) {
        return setEntry(idToInfo, userId.value, gson.toJson(info));
    }

    @Override
    public void deleteUser(UserId userId) {
        removeEntryByKey(idToInfo, userId.value);
    }

    @Override
    public void shutdown() {
        waitFuture(idToInfo.shutdown());
    }

    public static UserFactorySstRepository get(Path storingDir) {
        return new UserFactorySstRepository(storingDir);
    }
}
