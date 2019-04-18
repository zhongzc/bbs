package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.SSTPathConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig.FileType;
import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components._repositories.*;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.idGenerator.IdRepository;
import com.gaufoo.bbs.components.learningResource.LearningResource;
import com.gaufoo.bbs.components.like.LikeComponent;
import com.gaufoo.bbs.components.lostfound.LostFound;
import com.gaufoo.bbs.components.comment.Comment;
import com.gaufoo.bbs.components.schoolHeat.SchoolHeat;
import com.gaufoo.bbs.components.scutMajor.MajorFactory;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.validator.Validator;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    public final LikeComponent like;
    public final LearningResource learnResource;
    public final SchoolHeat schoolHeat;
    public final Comment comment;
    public final IdRepository idRepository;

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

        idRepository = IdSstRepository.get("idSstRep", sstPathConfig.id());

        user = UserFactory.defau1t("usrFty",
                UserFactorySstRepository.get("usrFtySstRep", sstPathConfig.userFactory()),
                IdGenerator.seqInteger("usrId", idRepository));

        authenticator = Authenticator.defau1t("auth",
                AuthenticatorSstRepository.get("authSstRep", sstPathConfig.auth()),
                Validator.email(), Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20)),
                TokenGenerator.defau1t("authToken",
                        TokenGeneratorSstRepository.get("authTokenSstRep", sstPathConfig.authTokenGen())));

        userProfiles = FileFactory.defau1t("userProfiles",
                FileFactoryFileSystemRepository.get("fileDskRep",userProfileFolder),
                IdGenerator.seqInteger("usrImgId", idRepository));

        major = MajorFactory.defau1t("major");

        lostFound = LostFound.defau1t("lstFnd",
                LostFoundSstRepository.get("lstFndSstRep", sstPathConfig.lostFound()),
                IdGenerator.seqInteger("lstId", idRepository), IdGenerator.seqInteger("fndId", idRepository));

        lostFoundImages = FileFactory.defau1t("lostFoundImages",
                        FileFactoryFileSystemRepository.get("lostFoundDskRep",
                                lostFoundFolder), IdGenerator.seqInteger("lostImgId", idRepository));

        like = LikeComponent.defau1t("like",
                LikeComponentSstRepository.get("likeMryRep", sstPathConfig.like()),
                IdGenerator.seqInteger("likeId", idRepository));

        learnResource = LearningResource.defau1t("learnResource",
                LearningResourceSstRepository.get("learnResMryRep", sstPathConfig.learnResource()),
                IdGenerator.seqInteger("resourceId", idRepository));

        schoolHeat = SchoolHeat.defau1t("schoolHeat",
                SchoolHeatSstRepository.get("schoolHeatSstRep", sstPathConfig.schoolHeat()),
                IdGenerator.seqInteger("postId", idRepository));

        comment = Comment.defau1t("comment",
                IdGenerator.seqInteger("commentId", idRepository),
                IdGenerator.seqInteger("replyId", idRepository),
                CommentSstRepository.get("commentSstRep", sstPathConfig.comment()),
                ReplySstRepository.get("replySstRep", sstPathConfig.reply()));
    }

    public void shutdown() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable[] shutdowns = {
                user::shutdown,
                authenticator::shutdown,
                userProfiles::shutdown,
                lostFound::shutdown,
                lostFoundImages::shutdown,
                like::shutdown,
                learnResource::shutdown,
                schoolHeat::shutdown,
                comment::shutdown,
                idRepository::shutdown,
        };
        for (Runnable s : shutdowns) {
            executor.execute(s);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearFolders(List<Path> paths) {
        paths.forEach(Utils::deleteFileRecursively);
    }

    private void createFoldersIfAbsent(List<Path> paths) {
        paths.forEach(path -> path.toFile().mkdirs());
    }

}
