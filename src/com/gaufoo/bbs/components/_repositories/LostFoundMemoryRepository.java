package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.lostfound.LostFoundRepository;
import com.gaufoo.bbs.components.lostfound.common.*;

import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Stream;

public class LostFoundMemoryRepository implements LostFoundRepository {
    private final Map<String, LostInput> losts = new Hashtable<>();
    private final Map<String, FoundInput> founds = new Hashtable<>();

    @Override
    public boolean saveLost(LostId id, LostInternal info) {
        return false;
    }

    @Override
    public boolean saveFound(FoundId id, FoundInternal info) {
        return false;
    }

    @Override
    public boolean updateLost(LostId id, LostInternal info) {
        return false;
    }

    @Override
    public boolean updateFound(FoundId id, FoundInternal info) {
        return false;
    }

    @Override
    public LostInternal getLostInfo(LostId id) {
        return null;
    }

    @Override
    public FoundInternal getFoundInfo(FoundId id) {
        return null;
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

    private static LostFoundRepository instance = new LostFoundMemoryRepository();

    public static LostFoundRepository get() {
        return instance;
    }
}
