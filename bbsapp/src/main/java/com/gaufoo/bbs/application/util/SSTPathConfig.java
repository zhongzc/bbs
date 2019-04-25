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
    private final String lost;
    private final String found;
    private final String entertainment;
    private final String lecture;
    private final String learningResource;
    private final String schoolHeat;
    private final String comment;
    private final String reply;
    private final String id;
    private final String content;
    private final String active;
    private final String heat;
    private final String news;

    private SSTPathConfig(Path baseDir, String authenticator, String authenticatorTokenGenerator, String userFactory, String lost, String found, String entertainment, String lecture, String learningResource, String schoolHeat, String comment, String reply, String id, String content, String active, String heat, String news) {
        this.baseDir = baseDir;
        this.authenticator = authenticator;
        this.authenticatorTokenGenerator = authenticatorTokenGenerator;
        this.userFactory = userFactory;
        this.lost = lost;
        this.found = found;
        this.entertainment = entertainment;
        this.lecture = lecture;
        this.learningResource = learningResource;
        this.schoolHeat = schoolHeat;
        this.comment = comment;
        this.reply = reply;
        this.id = id;
        this.content = content;
        this.active = active;
        this.heat = heat;
        this.news = news;
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

    public Path news() {
        return baseDir.resolve(news);
    }

    public Path lost() {
        return baseDir.resolve(lost);
    }

    public Path found() {
        return baseDir.resolve(found);
    }

    public Path learningResource() {
        return baseDir.resolve(learningResource);
    }

    public Path schoolHeat() {
        return baseDir.resolve(schoolHeat);
    }

    public Path entertainment() {
        return baseDir.resolve(entertainment);
    }

    public Path lecture() {
        return baseDir.resolve(lecture);
    }

    public Path id() {
        return baseDir.resolve(id);
    }

    public Path comment() {
        return baseDir.resolve(comment);
    }

    public Path reply() {
        return baseDir.resolve(reply);
    }

    public Path content() {
        return baseDir.resolve(content);
    }

    public Path active() {
        return baseDir.resolve(active);
    }

    public Path heat() {
        return baseDir.resolve(heat);
    }

    public List<Path> allSSTPaths() {
        Path[] paths = new Path[]{
                auth(),
                authTokenGen(),
                userFactory(),
                lost(),
                found(),
                learningResource(),
                schoolHeat(),
                entertainment(),
                lecture(),
                id(),
                comment(),
                reply(),
                content(),
                active(),
                heat()
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
                "lost",
                "found",
                "entertainment",
                "lecture",
                "learningResource",
                "schoolHeat",
                "comment",
                "reply",
                "id", "content", "active", "heat", "news");
    }

}
