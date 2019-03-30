package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.resTypes.SignUpError;
import com.gaufoo.bbs.application.resTypes.SignUpPayload;
import com.gaufoo.bbs.application.resTypes.SignUpResult;
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
                .add(SignUpResult.class)
                .add(SignUpError.class)
                .add(SignUpPayload.class);
    }
}
