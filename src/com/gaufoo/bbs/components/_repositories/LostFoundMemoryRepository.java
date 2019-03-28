package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.lostfound.LostFoundRepository;
import com.gaufoo.bbs.components.lostfound.common.*;

import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Stream;

public class LostFoundMemoryRepository implements LostFoundRepository {
    private final String repositoryName;
    private final Map<String, LostInfo> losts = new Hashtable<>();
    private final Map<String, FoundInfo> founds = new Hashtable<>();

    private LostFoundMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveLost(LostId id, LostInfo info) {
        if (losts.containsKey(id.value)) return false;
        losts.put(id.value, info);
        return true;
    }

    @Override
    public boolean saveFound(FoundId id, FoundInfo info) {
        if (founds.containsKey(id.value)) return false;
        founds.put(id.value, info);
        return true;
    }

    @Override
    public boolean updateLost(LostId id, LostInfo info) {
        if (!losts.containsKey(id.value)) return false;
        losts.replace(id.value, info);
        return true;
    }

    @Override
    public boolean updateFound(FoundId id, FoundInfo info) {
        if (!founds.containsKey(id.value)) return false;
        founds.replace(id.value, info);
        return true;
    }

    @Override
    public LostInfo getLostInfo(LostId id) {
        return losts.get(id.value);
    }

    @Override
    public FoundInfo getFoundInfo(FoundId id) {
        return founds.get(id.value);
    }

    @Override
    public Stream<LostId> getAllLosts() {
        return losts.keySet().stream().map(LostId::of);
    }

    @Override
    public Stream<FoundId> getAllFounds() {
        return founds.keySet().stream().map(FoundId::of);
    }

    @Override
    public void deleteLost(LostId id) {
        losts.remove(id.value);
    }

    @Override
    public void deleteFound(FoundId id) {
        founds.remove(id.value);
    }

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static LostFoundRepository get(String repositoryName) {
        return new LostFoundMemoryRepository(repositoryName);
    }
}
