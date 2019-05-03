package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.ComponentFactory;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.*;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.user.common.UserInfo;
import com.gaufoo.bbs.gql.util.LoggingInterceptor;
import com.gaufoo.bbs.util.TaskChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class Application implements WebMvcConfigurer {
    private static Logger log = LoggerFactory.getLogger(Application.class);
    @Value("${user-profile-images-mapping}")
    private String profileImgMapping;
    @Value("${lost-and-found-images-mapping}")
    private String lostFoundMapping;
    @Value("${content-images-mapping}")
    private String contentMapping;
    @Value("${attached-files-mapping}")
    private String attachFilesMapping;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Bean
    SchemaParserDictionary schemaParserDictionary() {
        Class<?>[] subUnionTypes = {
                PersonalInformation.PersonalInfo.class,
                Authentication.LoggedInToken.class,
                Ok.class,
                AccountAndPassword.ResetPassToken.class,
                Found.MultiFoundInfos.class,
                Found.FoundInfo.class,
                Lost.MultiLostInfos.class,
                Lost.LostInfo.class,
                SchoolHeat.SchoolHeatInfo.class,
                SchoolHeat.MultiSchoolHeats.class,
                SchoolHeat.SchoolHeatInput.class,
                Content.Picture.class,
                Content.Paragraph.class,
                Comment.CommentInfo.class,
                Comment.ReplyInfo.class,
                Lecture.MultiLectures.class,
                Lecture.LectureInfo.class,
                LearningResource.LearningResourceInfo.class,
                LearningResource.MultiLearningResources.class,
                Entertainment.EntertainmentInfo.class,
                Entertainment.MultiEntertainments.class,
                Latest.Latests.class,
                Hot.Hots.class,
        };
        SchemaParserDictionary schemaParserDictionary = new SchemaParserDictionary();
        for(Class<?> clazz: subUnionTypes) {
            schemaParserDictionary.add(clazz);
        }
        schemaParserDictionary.add("Error", Error.class);
        return schemaParserDictionary;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ComponentFactory.clearFiles();  // fixme: remove this in production

        StaticResourceConfig config = StaticResourceConfig.defaultPartialConfig()
                .addMapping(StaticResourceConfig.FileType.UserProfileImage, profileImgMapping)
                .addMapping(StaticResourceConfig.FileType.LostFoundImage, lostFoundMapping)
                .addMapping(StaticResourceConfig.FileType.ContentImages, contentMapping)
                .addMapping(StaticResourceConfig.FileType.AttachFiles, attachFilesMapping).build();

        ComponentFactory.componentFactory = new ComponentFactory(config);
        addAdminUser();

        List<String> allUrlPrefixes = new LinkedList<>();
        List<String> allFolderPaths = new LinkedList<>();
        config.allFileTypes().forEach(fileType -> {
            log.info("mapped url: " + config.urlPrefixOf(fileType) + "/**");
            log.info("mapped folder: " + config.folderPathOf(fileType).toString());
            allUrlPrefixes.add(config.urlPrefixOf(fileType) + "/**");
            allFolderPaths.add(config.folderPathOf(fileType).toUri().toString());
        });

        registry.addResourceHandler(allUrlPrefixes.toArray(new String[0]))
                .addResourceLocations(allFolderPaths.toArray(new String[0]));
    }

    @PostConstruct
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.debug("ComponentFactory is shutting down..");
            try {
                ComponentFactory.componentFactory.shutdown();
                ComponentFactory.clearFiles();  // fixme: remove this in production
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        log.debug("added shutdown hook");
    }

    private void addAdminUser() {
        String username = "admin";
        String password = "letmein";  // fixme: insecure
        String nickname = "admin";
        log.debug("post construct {}", ComponentFactory.componentFactory);
        Boolean success = ComponentFactory.componentFactory.authenticator.createSuperUser(username, password)
                .mapF(ErrorCode::fromAuthError)
                .then(attachable -> TaskChain.Procedure.fromOptional(
                        ComponentFactory.componentFactory.user.createUser(UserInfo.of(nickname, null, null, null, null, null, Instant.now())),
                        ErrorCode.CreateUserFailed)
                        .then(userId -> attachable.attach(Permission.of(userId.value, Authenticator.Role.ADMIN))
                                .mapF(ErrorCode::fromAuthError)))
                .reduce(e -> false, i -> i);
        if (!success) log.warn("unable to create super user");
        else log.debug("created super user: " + username);
    }

    @Bean
    public Mutation createMutation() {
        ProxyFactory proxyFactory = new ProxyFactory(new Mutation());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(LoggingInterceptor.interceptor);
        return (Mutation)proxyFactory.getProxy();
    }

    @Bean Query createQuery() {
        ProxyFactory proxyFactory = new ProxyFactory(new Query());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(LoggingInterceptor.interceptor);
        return (Query)proxyFactory.getProxy();
    }
}
