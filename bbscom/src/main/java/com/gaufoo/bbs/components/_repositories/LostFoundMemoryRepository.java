package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components._depr_lostfound.LostFoundRepository;
import com.gaufoo.bbs.components._depr_lostfound.common.*;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class LostFoundMemoryRepository implements LostFoundRepository {
    private final static Gson gson = new Gson();

    // LostId -> LostInfo
    private final Map<String, String> losts = new ConcurrentHashMap<>();

    // FoundId -> FoundInfo
    private final Map<String, String> founds = new ConcurrentHashMap<>();

    private LostFoundMemoryRepository() { }

    @Override
    public boolean saveLost(LostId id, LostInfo info) {
        if (losts.containsKey(id.value)) return false;
        losts.put(id.value, gson.toJson(info));
        return true;
    }

    @Override
    public boolean saveFound(FoundId id, FoundInfo info) {
        if (founds.containsKey(id.value)) return false;
        founds.put(id.value, gson.toJson(info));
        return true;
    }

    @Override
    public boolean updateLost(LostId id, LostInfo info) {
        if (!losts.containsKey(id.value)) return false;
        losts.put(id.value, gson.toJson(info));
        return true;
    }

    @Override
    public boolean updateFound(FoundId id, FoundInfo info) {
        if (!founds.containsKey(id.value)) return false;
        founds.put(id.value, gson.toJson(info));
        return true;
    }

    @Override
    public LostInfo getLostInfo(LostId id) {
        return gson.fromJson(losts.get(id.value), LostInfo.class);
    }

    @Override
    public FoundInfo getFoundInfo(FoundId id) {
        return gson.fromJson(founds.get(id.value), FoundInfo.class);
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

    public static LostFoundRepository get() {
        return new LostFoundMemoryRepository();
    }
}
