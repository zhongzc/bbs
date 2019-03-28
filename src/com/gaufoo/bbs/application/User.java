package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components._repositories.UserFactoryMemoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.user.UserFactory;

public class User {
    public final UserFactory userFactory = UserFactory.defau1t("usrFty", UserFactoryMemoryRepository.get("usrFtyMryRep"), IdGenerator.seqInteger("usrId"));

    private static User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }
}
