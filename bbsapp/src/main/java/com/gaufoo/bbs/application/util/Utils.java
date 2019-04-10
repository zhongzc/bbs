package com.gaufoo.bbs.application.util;

import com.gaufoo.bbs.application.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Utils {
    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static void deleteFileRecursively(Path path) {
        try {
            Files.walkFileTree(path, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.warn("deleteFileRecursively - failed, error: {}, path: {}", e.getMessage(), path);
        }
    }


    public static String makeUrl(String uri, StaticResourceConfig.FileType fileType) {
        if (uri == null || uri.isEmpty()) return "";
        return ComponentFactory.componentFactory.config.urlPrefixOf(fileType) + "/" +
                Paths.get(URI.create(uri)).toFile().getName();
    }
}
