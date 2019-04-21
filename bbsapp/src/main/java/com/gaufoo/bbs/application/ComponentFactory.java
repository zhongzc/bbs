package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.SSTPathConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig.FileType;
import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components._depr_learningResource.LearningResourceSstRepository;
import com.gaufoo.bbs.components._depr_lostfound.LostFoundSstRepository;
import com.gaufoo.bbs.components._depr_schoolHeat.SchoolHeatSstRepository;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.AuthenticatorSstRepository;
import com.gaufoo.bbs.components.commentGroup.comment.CommentSstRepository;
import com.gaufoo.bbs.components.commentGroup.comment.reply.ReplySstRepository;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.file.FileFactoryFileSystemRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.idGenerator.IdRepository;
import com.gaufoo.bbs.components._depr_learningResource.LearningResource;
import com.gaufoo.bbs.components._depr_lostfound.LostFound;
import com.gaufoo.bbs.components.commentGroup.comment.Comment;
import com.gaufoo.bbs.components._depr_schoolHeat.SchoolHeat;
import com.gaufoo.bbs.components.idGenerator.IdSstRepository;
import com.gaufoo.bbs.components.scutMajor.MajorFactory;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.tokenGenerator.TokenGeneratorSstRepository;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.user.UserFactorySstRepository;
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

        idRepository = IdSstRepository.get(sstPathConfig.id());

        user = UserFactory.defau1t(
                UserFactorySstRepository.get(sstPathConfig.userFactory()),
                IdGenerator.seqInteger("usrId", idRepository));

        authenticator = Authenticator.defau1t(
                AuthenticatorSstRepository.get(sstPathConfig.auth()),
                Validator.email(), Validator.nonContainsSpace().compose(Validator.minLength(8)).compose(Validator.maxLength(20)),
                TokenGenerator.defau1t(TokenGeneratorSstRepository.get(sstPathConfig.authTokenGen())));

        userProfiles = FileFactory.defau1t(
                FileFactoryFileSystemRepository.get(userProfileFolder),
                IdGenerator.seqInteger("usrImgId", idRepository));

        major = MajorFactory.defau1t();

        lostFound = LostFound.defau1t(
                LostFoundSstRepository.get(sstPathConfig.lostFound()),
                IdGenerator.seqInteger("lstId", idRepository), IdGenerator.seqInteger("fndId", idRepository));

        lostFoundImages = FileFactory.defau1t(FileFactoryFileSystemRepository.get(lostFoundFolder),
                IdGenerator.seqInteger("lostImgId", idRepository));

        learnResource = LearningResource.defau1t(
                LearningResourceSstRepository.get(sstPathConfig.learningResource()),
                IdGenerator.seqInteger("resourceId", idRepository));

        schoolHeat = SchoolHeat.defau1t(
                SchoolHeatSstRepository.get(sstPathConfig.schoolHeat()),
                IdGenerator.seqInteger("postId", idRepository));

        comment = Comment.defau1t(
                IdGenerator.seqInteger("commentId", idRepository),
                IdGenerator.seqInteger("replyId", idRepository),
                CommentSstRepository.get(sstPathConfig.comment()),
                ReplySstRepository.get(sstPathConfig.reply()));
    }

    public void shutdown() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable[] shutdowns = {
                user::shutdown,
                authenticator::shutdown,
                userProfiles::shutdown,
                lostFound::shutdown,
                lostFoundImages::shutdown,
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
