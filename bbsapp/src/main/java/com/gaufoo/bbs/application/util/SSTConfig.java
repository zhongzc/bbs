package com.gaufoo.bbs.application.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SSTConfig {
    public final Path baseDir;
    public final String authenticator;

    private SSTConfig(Path baseDir, String authenticator) {
        this.baseDir = baseDir;
        this.authenticator = authenticator;
    }

    public static SSTConfig defau1t() {
        Path baseDir = Paths.get(System.getProperty("user.home"))
                .resolve("bbs-temp");
        return new SSTConfig(baseDir, "authenticator");
    }
}
