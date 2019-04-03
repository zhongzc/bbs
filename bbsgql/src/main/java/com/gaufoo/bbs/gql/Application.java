package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.Authentication;
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
        return new SchemaParserDictionary()
                .add(Authentication.SignUpError.class)
                .add(Authentication.SignUpPayload.class)
                .add(Authentication.LogInError.class)
                .add(Authentication.LogInPayload.class);
    }
}
