package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.AccountAndPassword;
import com.gaufoo.bbs.application.Authentication;
import com.gaufoo.bbs.application.PersonalInformation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
                PersonalInformation.PersonalInfoError.class,
                PersonalInformation.PersonalInfo.class,
        };
        SchemaParserDictionary schemaParserDictionary = new SchemaParserDictionary();
        for(Class<?> clazz: subUnionTypes) {
            schemaParserDictionary.add(clazz);
        }
        return schemaParserDictionary;
    }
}
