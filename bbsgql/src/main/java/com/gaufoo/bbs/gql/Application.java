package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


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
                AccountAndPassword.ConfirmPasswordError.class,
                AccountAndPassword.ConfirmPasswordPayload.class,
                Authentication.GetIdError.class,
                Authentication.GetIdPayload.class,
                PersonalInformation.PersonalInfoError.class,
                PersonalInformation.PersonalInfo.class,
                PersonalInformation.MajorsInError.class,
                PersonalInformation.MajorsInPayload.class,
                PersonalInformation.ModifyPersonInfoSuccess.class,
                PersonalInformation.ModifyPersonInfoError.class,
                LostAndFound.ItemInfoError.class,
                LostAndFound.FoundItemInfo.class,
                LostAndFound.LostItemInfo.class,
        };
        SchemaParserDictionary schemaParserDictionary = new SchemaParserDictionary();
        for(Class<?> clazz: subUnionTypes) {
            schemaParserDictionary.add(clazz);
        }
        return schemaParserDictionary;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("profile resource location: {}", ComponentFactory.profilesRcPath.toUri().toString());
        logger.info("lost and found resource location: {}", ComponentFactory.lostFoundRcPath.toUri().toString());

        registry.addResourceHandler(profileImgMapping, lostFoundMapping)
                .addResourceLocations(
                        ComponentFactory.profilesRcPath.toUri().toString(),
                        ComponentFactory.lostFoundRcPath.toUri().toString());
    }

}
