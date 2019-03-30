package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.user.UserFactoryRepository;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;

import java.util.Hashtable;
import java.util.Map;

public class UserFactoryMemoryRepository implements UserFactoryRepository {
    private final String repositoryName;
    private final Map<String, UserInfo> map = new Hashtable<>();

    private UserFactoryMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveUser(UserId id, UserInfo userInfo) {
        if (map.containsKey(id.value)) return false;
        map.put(id.value, userInfo);
        return true;
    }

    @Override
    public UserInfo getUserInfo(UserId userId) {
        return map.get(userId.value);
    }

    @Override
    public boolean updateUser(UserId userId, UserInfo info) {
        if (!map.containsKey(userId.value)) return false;
        map.replace(userId.value, info);
        return true;
    }

    @Override
    public void deleteUser(UserId userId) {
        map.remove(userId.value);
    }

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static UserFactoryRepository get(String repositoryName) {
        return new UserFactoryMemoryRepository(repositoryName);
    }
}
