package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components._repositories.AuthenticatorMemoryRepository;
import com.gaufoo.bbs.components._repositories.TokenGeneratorMemoryRepository;
import com.gaufoo.bbs.components._repositories.UserFactoryMemoryRepository;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
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
}
