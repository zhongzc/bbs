package com.gaufoo.bbs.application.util;

import com.gaufoo.bbs.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.gaufoo.bbs.application.util.StaticResourceConfig.FileType.*;

public class StaticResourceConfig {
    private static Logger log = LoggerFactory.getLogger(StaticResourceConfig.class);
    public enum FileType {
        UserProfileImage,
        LostFoundImage,
        ContentImages,
        AttachFiles,
    }

    private final Path resourceFolder;
    private final Map<FileType, Tuple<String, String>> uriMapper;

    private StaticResourceConfig(Path resourceFolder, Map<FileType, Tuple<String, String>> uriMapper) {
        this.resourceFolder = resourceFolder;
        this.uriMapper = uriMapper;
    }

    public Path baseDir() {
        return resourceFolder;
    }

    public Path folderPathOf(FileType fileType) {
        return resourceFolder.resolve(uriMapper.get(fileType).left);
    }

    public String urlPrefixOf(FileType fileType) {
        return uriMapper.get(fileType).right;
    }

    public List<FileType> allFileTypes() {
        return new LinkedList<>(uriMapper.keySet());
    }

    public String makeUrl(FileType fileType, URI fileUri) {
        String fileName = new File(fileUri).getName();
        if (!folderPathOf(fileType).resolve(fileName).equals(Paths.get(fileUri))) {
            log.error("makeUrl({}, {}) Error - fileType mismatch: {} not Eq to {}", fileType, fileUri,
                    folderPathOf(fileType).resolve(fileName).toString(), Paths.get(fileUri).toString());
            return "";
        }
        return urlPrefixOf(fileType) + "/" + fileName;
    }

    public static class Builder {
        private Path resourceFolder;
        private Map<FileType, Tuple<String, String>> uriMapper;

        private Builder(Path resourceFolder, Map<FileType, Tuple<String, String>> uriMapper) {
            this.resourceFolder = resourceFolder;
            this.uriMapper = uriMapper;
        }

        public Builder addMapping(FileType fileType, String webRelativeLocation) {
            Tuple<String, String> oldVal = uriMapper.get(fileType);
            if (oldVal == null) return this;
            uriMapper.put(fileType, oldVal.modRight(webRelativeLocation));
            return this;
        }

        public StaticResourceConfig build() {
            boolean isValid = uriMapper.values().stream()
                    .allMatch(tup -> tup.left != null && tup.right != null &&
                            !tup.left.isEmpty() && !tup.right.isEmpty());
            if (!isValid) {
                throw new RuntimeException("StaticResourceConfig is invalid");
            }
            createFolderIfNecessary();
            return new StaticResourceConfig(resourceFolder, uriMapper);
        }

        private void createFolderIfNecessary() {
            uriMapper.values().forEach(tup -> {
                resourceFolder.resolve(tup.left).toFile().mkdirs();
            });
        }
    }

    public static Builder defaultPartialConfig() {
        Path resourceFolder = Paths.get(System.getProperty("user.home"), "bbs-temp");
        List<Tuple<FileType, String>> tuples = new LinkedList<Tuple<FileType, String>>() {{
            add(Tuple.of(UserProfileImage, "profiles"));
            add(Tuple.of(LostFoundImage, "lostAndFound"));
            add(Tuple.of(ContentImages, "contentImg"));
            add(Tuple.of(AttachFiles, "attachFiles"));
            // add more mappings here
        }};

        Map<FileType, Tuple<String, String>> uriMapper = new HashMap<>();
        tuples.forEach(tup -> uriMapper.put(tup.left, Tuple.of(tup.right, "")));

        return new Builder(resourceFolder, uriMapper);
    }
}
