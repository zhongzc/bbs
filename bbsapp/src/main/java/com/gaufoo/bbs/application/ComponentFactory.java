package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.AuthConfig;
import com.gaufoo.bbs.application.util.IdConfig;
import com.gaufoo.bbs.application.util.SSTPathConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.active.Active;
import com.gaufoo.bbs.components.active.ActiveRepository;
import com.gaufoo.bbs.components.active.ActiveSstRepository;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.AuthenticatorRepository;
import com.gaufoo.bbs.components.authenticator.AuthenticatorSstRepository;
import com.gaufoo.bbs.components.commentGroup.comment.Comment;
import com.gaufoo.bbs.components.commentGroup.comment.CommentRepository;
import com.gaufoo.bbs.components.commentGroup.comment.CommentSstRepository;
import com.gaufoo.bbs.components.commentGroup.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.commentGroup.comment.reply.ReplySstRepository;
import com.gaufoo.bbs.components.content.Content;
import com.gaufoo.bbs.components.content.ContentRepository;
import com.gaufoo.bbs.components.content.ContentSstRepository;
import com.gaufoo.bbs.components.entertainment.Entertainment;
import com.gaufoo.bbs.components.entertainment.EntertainmentRepository;
import com.gaufoo.bbs.components.entertainment.EntertainmentSstRepository;
import com.gaufoo.bbs.components.file.FileFactory;
import com.gaufoo.bbs.components.file.FileFactoryFileSystemRepository;
import com.gaufoo.bbs.components.file.FileFactoryRepository;
import com.gaufoo.bbs.components.found.Found;
import com.gaufoo.bbs.components.found.FoundRepository;
import com.gaufoo.bbs.components.found.FoundSstRepository;
import com.gaufoo.bbs.components.heat.Heat;
import com.gaufoo.bbs.components.heat.HeatRepository;
import com.gaufoo.bbs.components.heat.HeatSstRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.idGenerator.IdRepository;
import com.gaufoo.bbs.components.idGenerator.IdSstRepository;
import com.gaufoo.bbs.components.learningResource.LearningResource;
import com.gaufoo.bbs.components.learningResource.LearningResourceRepository;
import com.gaufoo.bbs.components.learningResource.LearningResourceSstRepository;
import com.gaufoo.bbs.components.lecture.Lecture;
import com.gaufoo.bbs.components.lecture.LectureRepository;
import com.gaufoo.bbs.components.lecture.LectureSstRepository;
import com.gaufoo.bbs.components.lost.Lost;
import com.gaufoo.bbs.components.lost.LostRepository;
import com.gaufoo.bbs.components.lost.LostSstRepository;
import com.gaufoo.bbs.components.news.News;
import com.gaufoo.bbs.components.news.NewsRepository;
import com.gaufoo.bbs.components.news.NewsSstRepository;
import com.gaufoo.bbs.components.schoolHeat.SchoolHeat;
import com.gaufoo.bbs.components.schoolHeat.SchoolHeatRepository;
import com.gaufoo.bbs.components.schoolHeat.SchoolHeatSstRepository;
import com.gaufoo.bbs.components.scutCourse.CourseFactory;
import com.gaufoo.bbs.components.scutMajor.MajorFactory;
import com.gaufoo.bbs.components.tokenGenerator.TokenGenerator;
import com.gaufoo.bbs.components.tokenGenerator.TokenGeneratorRepository;
import com.gaufoo.bbs.components.tokenGenerator.TokenGeneratorSstRepository;
import com.gaufoo.bbs.components.user.UserFactory;
import com.gaufoo.bbs.components.user.UserFactoryRepository;
import com.gaufoo.bbs.components.user.UserFactorySstRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ComponentFactory {

    private interface Shutdownable {
        void shutdown();
    }

    private final List<Shutdownable> reps = new ArrayList<>();

    public final UserFactory user;
    public final Authenticator authenticator;
    public final MajorFactory major;
    public final CourseFactory course;
    public final News news;
    public final Lost lost;
    public final Found found;
    public final SchoolHeat schoolHeat;
    public final Entertainment entertainment;
    public final Lecture lecture;
    public final LearningResource learningResource;
    public final Comment comment;
    public final Content content;
    public final Active active;
    public final Heat heat;
    public final FileFactory userProfiles;
    public final FileFactory lostFoundImages;
    public final FileFactory contentImages;

    public StaticResourceConfig staticResourceConfig;

    public static ComponentFactory componentFactory;

    public ComponentFactory(StaticResourceConfig staticResourceConfig) {
        SSTPathConfig sstPathConfig = SSTPathConfig.defau1t();
        this.staticResourceConfig = staticResourceConfig;
        IdConfig idConfig = IdConfig.defau1t();

        //
        IdRepository idRepository = IdSstRepository.get(sstPathConfig.id());
        reps.add(idRepository::shutdown);

        //
        UserFactoryRepository userFactoryRepository = UserFactorySstRepository.get(sstPathConfig.userFactory());
        reps.add(userFactoryRepository::shutdown);
        this.user = UserFactory.defau1t(userFactoryRepository, IdGenerator.seqInteger(idConfig.user, idRepository));

        //
        TokenGeneratorRepository tokenGeneratorRepository = TokenGeneratorSstRepository.get(sstPathConfig.authTokenGen());
        reps.add(tokenGeneratorRepository::shutdown);
        TokenGenerator tokenGenerator = TokenGenerator.defau1t(tokenGeneratorRepository);

        //
        AuthenticatorRepository authenticatorRepository = AuthenticatorSstRepository.get(sstPathConfig.auth());
        reps.add(authenticatorRepository::shutdown);
        this.authenticator = Authenticator.defau1t(authenticatorRepository, AuthConfig.usernameVal(), AuthConfig.passwordVal(), tokenGenerator);

        //
        this.major = MajorFactory.defau1t();

        //
        this.course = CourseFactory.defau1t();

        //
        NewsRepository newsRepository = NewsSstRepository.get(sstPathConfig.news());
        reps.add(newsRepository::shutdown);
        this.news = News.defau1t(newsRepository, IdGenerator.seqInteger(idConfig.news, idRepository));

        //
        LostRepository lostRepository = LostSstRepository.get(sstPathConfig.lost());
        reps.add(lostRepository::shutdown);
        this.lost = Lost.defau1t(lostRepository, IdGenerator.seqInteger(idConfig.lost, idRepository));

        //
        FoundRepository foundRepository = FoundSstRepository.get(sstPathConfig.found());
        reps.add(foundRepository::shutdown);
        this.found = Found.defau1t(foundRepository, IdGenerator.seqInteger(idConfig.found, idRepository));

        //
        SchoolHeatRepository schoolHeatRepository = SchoolHeatSstRepository.get(sstPathConfig.schoolHeat());
        reps.add(schoolHeatRepository::shutdown);
        this.schoolHeat = SchoolHeat.defau1t(schoolHeatRepository, IdGenerator.seqInteger(idConfig.schoolHeat, idRepository));

        //
        EntertainmentRepository entertainmentRepository = EntertainmentSstRepository.get(sstPathConfig.entertainment());
        reps.add(entertainmentRepository::shutdown);
        this.entertainment = Entertainment.defau1t(entertainmentRepository, IdGenerator.seqInteger(idConfig.entertainment, idRepository));

        //
        LectureRepository lectureRepository = LectureSstRepository.get(sstPathConfig.lecture());
        reps.add(lectureRepository::shutdown);
        this.lecture = Lecture.defau1t(lectureRepository, IdGenerator.seqInteger(idConfig.lecture, idRepository));

        //
        LearningResourceRepository learningResourceRepository = LearningResourceSstRepository.get(sstPathConfig.learningResource());
        reps.add(learningResourceRepository::shutdown);
        this.learningResource = LearningResource.defau1t(learningResourceRepository, IdGenerator.seqInteger(idConfig.learningResource, idRepository));

        //
        CommentRepository commentRepository = CommentSstRepository.get(sstPathConfig.comment());
        reps.add(commentRepository::shutdown);
        ReplyRepository replyRepository = ReplySstRepository.get(sstPathConfig.reply());
        reps.add(replyRepository::shutdown);
        this.comment = Comment.defau1t(IdGenerator.seqInteger(idConfig.comment, idRepository), IdGenerator.seqInteger(idConfig.reply, idRepository), commentRepository, replyRepository);

        //
        ContentRepository contentRepository = ContentSstRepository.get(sstPathConfig.content());
        reps.add(contentRepository::shutdown);
        this.content = Content.defau1t(contentRepository, IdGenerator.seqInteger(idConfig.content, idRepository));

        //
        ActiveRepository activeRepository = ActiveSstRepository.get(sstPathConfig.active());
        reps.add(activeRepository::shutdown);
        this.active = Active.defau1t(activeRepository);

        //
        HeatRepository heatRepository = HeatSstRepository.get(sstPathConfig.heat());
        reps.add(heatRepository::shutdown);
        this.heat = Heat.defau1t(heatRepository);

        //
        FileFactoryRepository lostFoundImagesRepository = FileFactoryFileSystemRepository.get(staticResourceConfig.folderPathOf(StaticResourceConfig.FileType.LostFoundImage));
        reps.add(lostFoundImagesRepository::shutdown);
        this.lostFoundImages = FileFactory.defau1t(lostFoundImagesRepository, IdGenerator.seqInteger(idConfig.lostFoundImages, idRepository));

        //
        FileFactoryRepository userProfilesRepository = FileFactoryFileSystemRepository.get(staticResourceConfig.folderPathOf(StaticResourceConfig.FileType.UserProfileImage));
        reps.add(userProfilesRepository::shutdown);
        this.userProfiles = FileFactory.defau1t(userProfilesRepository, IdGenerator.seqInteger(idConfig.userProfiles, idRepository));

        //
        FileFactoryRepository contentImagesRepository = FileFactoryFileSystemRepository.get(staticResourceConfig.folderPathOf(StaticResourceConfig.FileType.ContentImages));
        reps.add(contentImagesRepository::shutdown);
        this.contentImages = FileFactory.defau1t(contentImagesRepository, IdGenerator.seqInteger(idConfig.contentImages, idRepository));
    }

    public void shutdown() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        this.reps.forEach(s -> executorService.execute(s::shutdown));
        executorService.shutdown();
        executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES);
    }
}
