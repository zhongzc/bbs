package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.user.UserFactoryRepository;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;

import static com.gaufoo.bbs.util.SstUtils.*;

public class UserFactorySstRepository implements UserFactoryRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private final SST idToInfo;

    private UserFactorySstRepository(String repositoryName, Path storingDir) {
        this.repositoryName = repositoryName;
        idToInfo = SST.of(repositoryName, storingDir.resolve(repositoryName));
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
        removeEntryWithKey(idToInfo, userId.value);
    }

    @Override
    public void shutdown() {
        waitAllFuturesPar(idToInfo.shutdown());
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    public static UserFactorySstRepository get(String repositoryName, Path storingDir) {
        return new UserFactorySstRepository(repositoryName, storingDir);
    }
}
