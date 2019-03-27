package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.lostfound.LostFoundRepository;
import com.gaufoo.bbs.components.lostfound.common.FoundId;
import com.gaufoo.bbs.components.lostfound.common.FoundInfo;
import com.gaufoo.bbs.components.lostfound.common.LostId;
import com.gaufoo.bbs.components.lostfound.common.LostInfo;

import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Stream;

public class LostFoundMemoryRepository implements LostFoundRepository {
    private final Map<String, LostInfo> losts = new Hashtable<>();
    private final Map<String, FoundInfo> founds = new Hashtable<>();

    @Override
    public boolean saveLost(LostId id, LostInfo info) {
        if (losts.containsKey(id.value)) return false;
        else {
            losts.put(id.value, info);
        }
        return true;
    }

    @Override
    public boolean saveFound(FoundId id, FoundInfo info) {
        if (founds.containsKey(id.value)) return false;
        else {
            founds.put(id.value, info);
        }
        return true;
    }

    @Override
    public boolean updateLost(LostId id, LostInfo info) {
        if (!losts.containsKey(id.value)) return false;
        else {
            losts.put(id.value, info);
        }
        return true;
    }

    @Override
    public boolean updateFound(FoundId id, FoundInfo info) {
        if (!founds.containsKey(id.value)) return false;
        else {
            founds.put(id.value, info);
        }
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

    private static LostFoundRepository instance = new LostFoundMemoryRepository();

    public static LostFoundRepository get() {
        return instance;
    }
}
