package com.gaufoo.bbs.components.file;

import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.db.TenGoKV;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileFactoryFileSystemRepository implements FileFactoryRepository {
    private final Path directory;
    private final TenGoKV<FileId, String> db;

    public FileFactoryFileSystemRepository(Path directory) {
        this.directory = directory;
        this.db = TenGoKV.TenGoKVBuilder.<FileId, String>get()
                .withPath(directory.resolve(".fileInfo"))
                .keySerializer(i -> i.value, 8)
                .valueSerializer(i -> i)
                .keyShaper(FileId::of)
                .valueShaper(i -> i)
                .withAggregate(Collections.emptyList())
                .build();
    }

    @Override
    public boolean saveFile(FileId fileId, byte[] file, String filename) {
        if (this.db.getValue(fileId) != null) return false;
        Path filepath = directory.resolve(filename);
        try {
            Files.write(filepath, file, StandardOpenOption.CREATE_NEW);
            return this.db.saveValue(fileId, filename);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getFilename(FileId fileId) {
        return this.db.getValue(fileId);
    }

    @Override
    public String getURI(FileId fileId) {
        String s = directory.resolve(getFilename(fileId)).toUri().toString();
        return s;
    }

    @Override
    public boolean delete(FileId id) {
        try {
            String filename = getFilename(id);
            if (this.db.deleteValue(id)) {
                Files.delete(directory.resolve(filename));
                return true;
            } else return false;
        } catch (IOException ignored) {
            return false;
        }
    }

    @Override
    public void shutdown() {
        this.db.shutdown();
    }

    public static FileFactoryRepository get(Path directory) {
        return new FileFactoryFileSystemRepository(directory);
    }
}
