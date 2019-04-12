package com.gaufoo.bbs.components.user;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;

import java.util.Optional;

public interface UserFactory {
    Optional<UserId> createUser(UserInfo userInfo);

    Optional<UserInfo> userInfo(UserId userId);

    boolean changeNickname(UserId userId, String newNickname);

    boolean changeProfilePicIdentifier(UserId userId, String newProfilePicIdentifier);

    boolean changeGender(UserId userId, UserInfo.Gender newGender);

    boolean changeGrade(UserId userId, String newGrade);

    boolean changeMajorCode(UserId userId, String newMajor);

    boolean changeIntroduction(UserId userId, String newIntroduction);

    void remove(UserId userId);

    void shutdown();

    String getName();

    static UserFactory defau1t(String componentName, UserFactoryRepository repository, IdGenerator idGenerator) {
        return new UserFactoryImpl(componentName, repository, idGenerator);
    }
}
