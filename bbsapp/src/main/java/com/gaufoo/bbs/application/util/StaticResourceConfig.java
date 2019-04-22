package com.gaufoo.bbs.application.util;

import com.gaufoo.bbs.util.Tuple;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.gaufoo.bbs.application.util.StaticResourceConfig.FileType.LostFoundImage;
import static com.gaufoo.bbs.application.util.StaticResourceConfig.FileType.UserProfileImage;
import static com.gaufoo.bbs.application.util.StaticResourceConfig.FileType.ContentImages;

public class StaticResourceConfig {
    public enum FileType {
        UserProfileImage,
        LostFoundImage,
        ContentImages
    }

    private final Path resourceFolder;
    private final Map<FileType, Tuple<String, String>> uriMapper;

    private StaticResourceConfig(Path resourceFolder, Map<FileType, Tuple<String, String>> uriMapper) {
        this.resourceFolder = resourceFolder;
        this.uriMapper = uriMapper;
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
            return new StaticResourceConfig(resourceFolder, uriMapper);
        }
    }

    public static Builder defaultPartialConfig() {
        Path resourceFolder = Paths.get(System.getProperty("user.home"), "bbs-temp");
        List<Tuple<FileType, String>> tuples = new LinkedList<Tuple<FileType, String>>() {{
            add(Tuple.of(UserProfileImage, "profiles"));
            add(Tuple.of(LostFoundImage, "lostAndFound"));
            add(Tuple.of(ContentImages, "contentImg"));
            // add more mappings here
        }};

        Map<FileType, Tuple<String, String>> uriMapper = new HashMap<>();
        tuples.forEach(tup -> uriMapper.put(tup.left, Tuple.of(tup.right, "")));

        return new Builder(resourceFolder, uriMapper);
    }
}
