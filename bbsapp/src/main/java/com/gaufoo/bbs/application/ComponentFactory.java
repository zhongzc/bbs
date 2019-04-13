package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.SSTPathConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig.FileType;
import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components._repositories.*;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.learningResource.LearningResource;
import com.gaufoo.bbs.components.like.LikeComponent;
import com.gaufoo.bbs.components.lostfound.LostFound;
import com.gaufoo.bbs.components.scutMajor.MajorFactory;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.validator.Validator;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentFactory {
    public static ComponentFactory componentFactory = null;

    public final StaticResourceConfig staticRcConfig;

    public final UserFactory user;
    public final Authenticator authenticator;
    public final FileFactory userProfiles;
    public final MajorFactory major;
    public final LostFound lostFound;
    public final FileFactory lostFoundImages;
    public final  LikeComponent like;
    public final LearningResource learnResource;
    public ComponentFactory(StaticResourceConfig staticRcConfig) {
        this.staticRcConfig = staticRcConfig;
        SSTPathConfig sstPathConfig = SSTPathConfig.defau1t();

        List<Path> allFolderPaths = staticRcConfig.allFileTypes().stream()
                .map(staticRcConfig::folderPathOf)
                .collect(Collectors.toList());
        allFolderPaths.addAll(sstPathConfig.allSSTPaths());

        Path userProfileFolder = staticRcConfig.folderPathOf(FileType.UserProfileImage);
        Path lostFoundFolder = staticRcConfig.folderPathOf(FileType.LostFoundImage);

        clearFolders(allFolderPaths);
        createFoldersIfAbsent(allFolderPaths);

        user = UserFactory.defau1t("usrFty",
                UserFactorySstRepository.get("usrFtySstRep", sstPathConfig.userFactory()),
                IdGenerator.seqInteger("usrId"));

        authenticator = Authenticator.defau1t("auth",
                AuthenticatorSstRepository.get("authSstRep", sstPathConfig.auth()),
                Validator.email(), Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20)),
                TokenGenerator.defau1t("authToken",
                        TokenGeneratorSstRepository.get("authTokenSstRep", sstPathConfig.authTokenGen())));

        userProfiles = FileFactory.defau1t("userProfiles",
                FileFactoryFileSystemRepository.get("fileDskRep",userProfileFolder),
                IdGenerator.seqInteger("usrImgId"));

        major = MajorFactory.defau1t("major");

        lostFound = LostFound.defau1t("lstFnd",
                LostFoundSstRepository.get("lstFndMryRep", sstPathConfig.lostFound()),
                IdGenerator.seqInteger("lstId"), IdGenerator.seqInteger("fndId"));

        lostFoundImages =
                FileFactory.defau1t("lostFoundImages",
                        FileFactoryFileSystemRepository.get("lostFileMryRep",
                                lostFoundFolder), IdGenerator.seqInteger("lostImgId"));

        like = LikeComponent.defau1t("like",
                LikeComponentSstRepository.get("likeMryRep", sstPathConfig.like()),
                IdGenerator.seqInteger("likeId"));

        learnResource= LearningResource.defau1t("learnResource",
                LearningResourceSstRepository.get("learnResMryRep", sstPathConfig.learnResource()),
                IdGenerator.seqInteger("resourceId"));
    }

    public void shutdown() {
        authenticator.shutdown();
    }

    private void clearFolders(List<Path> paths) {
        paths.forEach(Utils::deleteFileRecursively);
    }

    private void createFoldersIfAbsent(List<Path> paths) {
        paths.forEach(path -> path.toFile().mkdirs());
    }

}
