package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.file.FileFactoryRepository;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.util.Tuple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileFactoryMemoryRepository implements FileFactoryRepository {
    private final String repositoryName;
    private final Map<String, Tuple<byte[], String>> map = new ConcurrentHashMap<>();

    public FileFactoryMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveFile(FileId fileId, byte[] file, String filename) {
        if (map.containsKey(fileId.value)) return false;
        map.put(filename, Tuple.of(file, filename));

        return true;
    }

    @Override
    public String getFilename(FileId fileId) {
        Tuple<byte[], String > t = map.get(fileId.value);
        if (t != null) return t.right;
        else return null;
    }

    @Override
    public String getURI(FileId fileId) {
        return "mockURI";
    }

    @Override
    public void delete(FileId id) {
        map.remove(id.value);
    }

    public static FileFactoryRepository get(String repositoryName) {
        return new FileFactoryMemoryRepository(repositoryName);
    }
}
