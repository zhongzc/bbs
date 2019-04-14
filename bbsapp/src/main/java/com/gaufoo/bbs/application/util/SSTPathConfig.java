package com.gaufoo.bbs.application.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SSTPathConfig {
    private final Path baseDir;
    private final String authenticator;
    private final String authenticatorTokenGenerator;
    private final String userFactory;
    private final String lostFound;
    private final String like;
    private final String learnResource;
    private final String schoolHeat;

    private SSTPathConfig(Path baseDir, String authenticator, String authenticatorTokenGenerator, String userFactory, String lostFound, String like, String learnResource, String schoolHeat) {
        this.baseDir = baseDir;
        this.authenticator = authenticator;
        this.authenticatorTokenGenerator = authenticatorTokenGenerator;
        this.userFactory = userFactory;
        this.lostFound = lostFound;
        this.like = like;
        this.learnResource = learnResource;
        this.schoolHeat = schoolHeat;
    }

    public Path auth() {
        return baseDir.resolve(authenticator);
    }

    public Path authTokenGen() {
        return baseDir.resolve(authenticatorTokenGenerator);
    }

    public Path userFactory() {
        return baseDir.resolve(userFactory);
    }

    public Path lostFound() {
        return baseDir.resolve(lostFound);
    }

    public Path like() {
        return baseDir.resolve(like);
    }

    public Path learnResource() {
        return baseDir.resolve(learnResource);
    }

    public Path schoolHeat() {
        return baseDir.resolve(schoolHeat);
    }

    public List<Path> allSSTPaths() {
        Path[] paths = new Path[]{
                auth(),
                authTokenGen(),
                userFactory(),
                lostFound(),
                like(),
                learnResource(),
                schoolHeat(),
        };
        return Arrays.asList(paths);
    }


    public static SSTPathConfig defau1t() {
        Path baseDir = Paths.get(System.getProperty("user.home"))
                .resolve("bbs-temp");
        return new SSTPathConfig(baseDir,
                "authenticator",
                "authTokenGenerator",
                "userFactory",
                "lostFound",
                "like",
                "learnResource",
                "schoolHeat");
    }
}
