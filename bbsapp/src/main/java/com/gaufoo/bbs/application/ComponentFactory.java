package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components._repositories.*;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.like.LikeComponent;
import com.gaufoo.bbs.components.lostfound.LostFound;
import com.gaufoo.bbs.components.scutMajor.MajorFactory;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.validator.Validator;

import java.nio.file.*;

public class ComponentFactory {
    private static Path tempPath;
    public static Path profilesRcPath;
    public static Path lostFoundRcPath;

    static  {
        tempPath = Paths.get(System.getProperty("user.home"), "bbs-temp");
        profilesRcPath = tempPath.resolve("profiles");
        lostFoundRcPath = tempPath.resolve("lostAndFound");

        profilesRcPath.toFile().mkdirs();
        lostFoundRcPath.toFile().mkdirs();
    }

    private static void clearUp() {
        Utils.deleteFileRecursively(tempPath);
    }

    public static final UserFactory user =
            UserFactory.defau1t("usrFty",
                    UserFactoryMemoryRepository.get("usrFtyMryRep"), IdGenerator.seqInteger("usrId"));

    public static final Authenticator authenticator =
            Authenticator.defau1t("auth",
                    AuthenticatorMemoryRepository.get("authMryRep"),
                    Validator.email(), Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20)),
                    TokenGenerator.defau1t("authToken", TokenGeneratorMemoryRepository.get("authTokenMryRep")));

    public static final FileFactory userProfiles =
            FileFactory.defau1t("userProfiles",
                    FileFactoryFileSystemRepository.get("fileDskRep", profilesRcPath), IdGenerator.seqInteger("usrImgId"));

    public static final MajorFactory major = MajorFactory.defau1t("major");

    public static final LostFound lostFound =
            LostFound.defau1t("lstFnd", LostFoundMemoryRepository.get("lstFndMryRep"),
                    IdGenerator.seqInteger("lstId"), IdGenerator.seqInteger("fndId"));

    public static final FileFactory lostFoundImages =
            FileFactory.defau1t("lostFoundImages",
                    FileFactoryFileSystemRepository.get("lostFileMryRep", lostFoundRcPath), IdGenerator.seqInteger("lostImgId"));


    public static final  LikeComponent like=LikeComponent.defau1t("like", LikeComponentMemoryRepository.get("likeMryRep"), IdGenerator.seqInteger("likeId"));

//    public static final LearningResource learningResource=LearningResource.defau1t("learnResource", LearningResourceRepository.)

    public static void main(String[] args) {
        clearUp();
    }
}
