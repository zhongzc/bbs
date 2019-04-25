package com.gaufoo.bbs.gql;

import com.coxautodev.graphql.tools.SchemaParserDictionary;
import com.gaufoo.bbs.application.ComponentFactory;
import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.AccountAndPassword;
import com.gaufoo.bbs.application.types.Authentication;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.application.util.StaticResourceConfig.FileType;
import com.gaufoo.bbs.gql.util.LoggingInterceptor;
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
        StaticResourceConfig config = StaticResourceConfig.defaultPartialConfig()
                .addMapping(FileType.UserProfileImage, profileImgMapping)
                .addMapping(FileType.LostFoundImage, lostFoundMapping)
                .addMapping(FileType.ContentImages, contentMapping).build();

        ComponentFactory.componentFactory = new ComponentFactory(config);

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        log.debug("added shutdown hook");
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
