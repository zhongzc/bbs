package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.file.FileFactoryRepository;
import com.gaufoo.bbs.components.file.common.FileId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class FileFactoryFileSystemRepository implements FileFactoryRepository {
    private static final String STATE_FILE_NAME = "state.json";
    private final Gson gson = new Gson();
    private final String repositoryName;
    private final Path directory;
    private final Map<String, String> idToFilename = new Hashtable<>();

    public FileFactoryFileSystemRepository(String repositoryName, Path directory) {
        this.repositoryName = repositoryName;
        this.directory = directory;

        rebuildMap();
    }

    private void rebuildMap() {
        Path stateFilePath = directory.resolve(STATE_FILE_NAME);
        if (stateFilePath.toFile().exists()) {
            try {
                String json = new String(Files.readAllBytes(stateFilePath), StandardCharsets.UTF_8);
                Map<String, String> tmpMap = gson.fromJson(json, new TypeToken<HashMap<String, String>>(){}.getType());
                idToFilename.putAll(tmpMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean saveFile(FileId fileId, byte[] file, String filename) {
        if (idToFilename.containsKey(fileId.value)) return false;
        Path filepath = directory.resolve(fileId.value);
        try {
            Files.write(filepath, file, StandardOpenOption.CREATE_NEW);
            updateStateFile();
            idToFilename.put(fileId.value, filename);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void updateStateFile() {
        Path stateFilePath = directory.resolve(STATE_FILE_NAME);
        try {
            Files.write(stateFilePath, gson.toJson(idToFilename).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFilename(FileId fileId) {
        return idToFilename.get(fileId.value);
    }

    @Override
    public String getURI(FileId fileId) {
        return directory.resolve(fileId.value).toUri().toString();
    }

    @Override
    public void delete(FileId id) {
        try {
            idToFilename.remove(id.value);
            Files.delete(directory.resolve(id.value));
            updateStateFile();
        } catch (IOException ignored) {}
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    public static FileFactoryRepository get(String repositoryName, Path directory) {
        return new FileFactoryFileSystemRepository(repositoryName, directory);
    }
}
