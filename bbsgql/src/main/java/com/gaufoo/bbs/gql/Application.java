package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.*;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class Application implements WebMvcConfigurer {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${user-profile-images-mapping}")
    private String profileImgMapping;

    @Value("${lost-and-found-images-mapping}")
    private String lostFoundMapping;

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
                Authentication.SignUpError.class,
                Authentication.SignUpPayload.class,
                Authentication.LogInError.class,
                Authentication.LogInPayload.class,
                Authentication.GetIdError.class,
                Authentication.GetIdPayload.class,
                AccountAndPassword.ConfirmPasswordError.class,
                AccountAndPassword.ConfirmPasswordPayload.class,
                PersonalInformation.PersonalInfoError.class,
                PersonalInformation.PersonalInfo.class,
                PersonalInformation.MajorsInError.class,
                PersonalInformation.MajorsInPayload.class,
                PersonalInformation.ModifyPersonInfoSuccess.class,
                PersonalInformation.ModifyPersonInfoError.class,
                LostAndFound.LostFoundError.class,
                LostAndFound.PublishItemSuccess.class,
                LostAndFound.ModifyItemSuccess.class,
                LostAndFound.FoundItemInfo.class,
                LostAndFound.LostItemInfo.class,
                LostAndFound.AllLostSuccess.class,
                LostAndFound.AllFoundSuccess.class,
                SchoolHeats.SchoolHeatError.class,
                LearnResource.LearnResourceInfo.class,
                LearnResource.LearnResourceInfoError.class,
        };
        SchemaParserDictionary schemaParserDictionary = new SchemaParserDictionary();
        for(Class<?> clazz: subUnionTypes) {
            schemaParserDictionary.add(clazz);
        }
        return schemaParserDictionary;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        StaticResourceConfig config = StaticResourceConfig.defaultPartialConfig()
                .addMapping(FileType.UserProfileImage, profileImgMapping)
                .addMapping(FileType.LostFoundImage, lostFoundMapping).build().get();

        ComponentFactory.componentFactory = new ComponentFactory(config);

        List<String> allUrlPrefixes = new LinkedList<>();
        List<String> allFolderPaths = new LinkedList<>();
        config.allFileTypes().forEach(fileType -> {
            logger.info("mapped url: " + config.urlPrefixOf(fileType) + "/**");
            logger.info("mapped folder: " + config.folderPathOf(fileType).toString());
            allUrlPrefixes.add(config.urlPrefixOf(fileType) + "/**");
            allFolderPaths.add(config.folderPathOf(fileType).toUri().toString());
        });

        registry.addResourceHandler(allUrlPrefixes.toArray(new String[0]))
                .addResourceLocations(allFolderPaths.toArray(new String[0]));
    }

    @PostConstruct
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.debug("ComponentFactory is shutting down..");
            ComponentFactory.componentFactory.shutdown();
        }));
        logger.debug("added shutdown hook");
    }
}
