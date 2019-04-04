package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components._repositories.AuthenticatorMemoryRepository;
import com.gaufoo.bbs.components._repositories.FileFactoryMemoryRepository;
import com.gaufoo.bbs.components._repositories.TokenGeneratorMemoryRepository;
import com.gaufoo.bbs.components._repositories.UserFactoryMemoryRepository;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.file.FileFactoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.scutMajor.MajorFactory;
import com.gaufoo.bbs.components.scutMajor.MajorFactoryImpl;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.validator.Validator;

public class ComponentFactory {
    public static final UserFactory user =
            UserFactory.defau1t("usrFty",
                    UserFactoryMemoryRepository.get("usrFtyMryRep"), IdGenerator.seqInteger("usrId"));

    public static final Authenticator authenticator =
            Authenticator.defau1t("auth",
                    AuthenticatorMemoryRepository.get("authMryRep"),
                    Validator.email(), Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20)),
                    TokenGenerator.defau1t("authToken", TokenGeneratorMemoryRepository.get("authTokenMryRep")));

    public static final FileFactory file =
            FileFactory.defau1t("file", FileFactoryMemoryRepository.get("fileMryRep"), IdGenerator.seqInteger("usrImgId"));

    public static final MajorFactory major = MajorFactory.defau1t("major");
}
