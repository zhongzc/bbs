package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.AccountAndPassword;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.LostAndFound;
import com.gaufoo.bbs.application.PersonalInformation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Application implements WebMvcConfigurer {

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
                PersonalInformation.ModifyPersonSuccess.class,
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
}
