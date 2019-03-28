package com.gaufoo.bbs.components.user;

import com.gaufoo.bbs.components._repositories.UserFactoryMemoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.components.user.common.UserInfo;

import java.util.Optional;

public class UserFactoryImpl implements UserFactory {
    private final String componentName;
    private final UserFactoryRepository repository;
    private final IdGenerator idGenerator;

    UserFactoryImpl(String componentName, UserFactoryRepository repository, IdGenerator idGenerator) {
        this.componentName = componentName;
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<UserId> createUser(UserInfo userInfo) {
        UserId id = UserId.of(idGenerator.generateId());

        if (repository.saveUser(id, userInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserInfo> userInfo(UserId userId) {
        return Optional.ofNullable(repository.getUserInfo(userId));
    }

    @Override
    public boolean changeNickname(UserId userId, String newNickname) {
        return Optional.ofNullable(repository.getUserInfo(userId)).map(u ->
                repository.updateUser(userId, u.modNickname(newNickname))
        ).orElse(false);
    }

    @Override
    public boolean changeProfilePicIdentifier(UserId userId, String newProfilePicIdentifier) {
        return Optional.ofNullable(repository.getUserInfo(userId)).map(u ->
                repository.updateUser(userId, u.modProfilePicIdentifier(newProfilePicIdentifier))
        ).orElse(false);
    }

    @Override
    public boolean changeGender(UserId userId, UserInfo.Gender newGender) {
        return Optional.ofNullable(repository.getUserInfo(userId)).map(u ->
                repository.updateUser(userId, u.modGender(newGender))
        ).orElse(false);
    }

    @Override
    public boolean changeGrade(UserId userId, String newGrade) {
        return Optional.ofNullable(repository.getUserInfo(userId)).map(u ->
                repository.updateUser(userId, u.modGrade(newGrade))
        ).orElse(false);
    }

    @Override
    public boolean changeMajorCode(UserId userId, String newMajorCode) {
        return Optional.ofNullable(repository.getUserInfo(userId)).map(u ->
                repository.updateUser(userId, u.modMajorCode(newMajorCode))
        ).orElse(false);
    }

    @Override
    public boolean changeIntroduction(UserId userId, String newIntroduction) {
        return Optional.ofNullable(repository.getUserInfo(userId)).map(u ->
                repository.updateUser(userId, u.modIntroduction(newIntroduction))
        ).orElse(false);
    }

    @Override
    public void remove(UserId userId) {
        repository.deleteUser(userId);
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) {
        UserFactory userFactory = UserFactory.defau1t("", UserFactoryMemoryRepository.get(""), IdGenerator.seqInteger(""));
        Optional<UserId> id = userFactory.createUser(UserInfo.of("n", "ppi", UserInfo.Gender.male, "e", "cc", "aa"));
        System.out.println(id);

        System.out.println(userFactory.userInfo(id.get()));

        if (userFactory.changeGender(id.get(), UserInfo.Gender.female)) {
            System.out.println(userFactory.userInfo(id.get()));
        }
    }
}
