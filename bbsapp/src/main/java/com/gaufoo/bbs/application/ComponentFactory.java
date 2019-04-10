package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components._repositories.*;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.learningResource.LearningResource;
import com.gaufoo.bbs.components.learningResource.LearningResourceRepository;
import com.gaufoo.bbs.components.like.LikeComponent;
import com.gaufoo.bbs.components.lostfound.LostFound;
import com.gaufoo.bbs.components.scutMajor.MajorFactory;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.validator.Validator;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

import com.gaufoo.bbs.application.util.StaticResourceConfig.FileType;

public class ComponentFactory {
    public static ComponentFactory componentFactory;

    public final StaticResourceConfig config;

    public final UserFactory user;
    public final Authenticator authenticator;
    public final FileFactory userProfiles;
    public final MajorFactory major;
    public final LostFound lostFound;
    public final FileFactory lostFoundImages;
    public final  LikeComponent like;
    public final LearningResource learnResource;
    public ComponentFactory(StaticResourceConfig config) {
        this.config = config;

        List<Path> allFolderPaths = config.allFileTypes().stream()
                .map(config::folderPathOf)
                .collect(Collectors.toList());
        clearFolders(allFolderPaths);
        createFoldersIfAbsent(allFolderPaths);

        Path userProfileFolder = config.folderPathOf(FileType.UserProfileImage);
        Path lostFoundFolder = config.folderPathOf(FileType.LostFoundImage);

        user = UserFactory.defau1t("usrFty",
                UserFactoryMemoryRepository.get("usrFtyMryRep"), IdGenerator.seqInteger("usrId"));

        authenticator = Authenticator.defau1t("auth",
                AuthenticatorMemoryRepository.get("authMryRep"),
                Validator.email(), Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20)),
                TokenGenerator.defau1t("authToken", TokenGeneratorMemoryRepository.get("authTokenMryRep")));

        userProfiles = FileFactory.defau1t("userProfiles",
                FileFactoryFileSystemRepository.get("fileDskRep",
                        userProfileFolder), IdGenerator.seqInteger("usrImgId"));

        major = MajorFactory.defau1t("major");

        lostFound = LostFound.defau1t("lstFnd", LostFoundMemoryRepository.get("lstFndMryRep"),
                        IdGenerator.seqInteger("lstId"), IdGenerator.seqInteger("fndId"));

        lostFoundImages =
                FileFactory.defau1t("lostFoundImages",
                        FileFactoryFileSystemRepository.get("lostFileMryRep",
                                lostFoundFolder), IdGenerator.seqInteger("lostImgId"));

        like = LikeComponent.defau1t("like", LikeComponentMemoryRepository.get("likeMryRep"), IdGenerator.seqInteger("likeId"));
        learnResource= LearningResource.defau1t("learnResource",LearningResourceMemoryRepository.get("learnResMryRep"),IdGenerator.seqInteger("resourceId"));
    }

    private void clearFolders(List<Path> paths) {
        paths.forEach(Utils::deleteFileRecursively);
    }

    private void createFoldersIfAbsent(List<Path> paths) {
        paths.forEach(path -> path.toFile().mkdirs());
    }



}
