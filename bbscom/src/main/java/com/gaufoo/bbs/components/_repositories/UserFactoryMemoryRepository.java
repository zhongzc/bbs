package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.user.UserFactoryRepository;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;
import com.google.gson.Gson;

import java.util.Hashtable;
import java.util.Map;

public class UserFactoryMemoryRepository implements UserFactoryRepository {
    private final static Gson gson = new Gson();
    private final String repositoryName;

    // UserId -> UserInfo
    private final Map<String, String> idToInfo = new Hashtable<>();

    private UserFactoryMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveUser(UserId id, UserInfo userInfo) {
        if (idToInfo.containsKey(id.value)) return false;
        idToInfo.put(id.value, gson.toJson(userInfo));
        return true;
    }

    @Override
    public UserInfo getUserInfo(UserId userId) {
        return gson.fromJson(idToInfo.get(userId.value), UserInfo.class);
    }

    @Override
    public boolean updateUser(UserId userId, UserInfo info) {
        if (!idToInfo.containsKey(userId.value)) return false;
        idToInfo.put(userId.value, gson.toJson(info));
        return true;
    }

    @Override
    public void deleteUser(UserId userId) {
        idToInfo.remove(userId.value);
    }

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static UserFactoryRepository get(String repositoryName) {
        return new UserFactoryMemoryRepository(repositoryName);
    }
}
