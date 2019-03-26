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
    private final Map<LostId, LostInfo> losts = new Hashtable<>();
    private final Map<FoundId, FoundInfo> founds = new Hashtable<>();

    @Override
    public boolean saveLost(LostId id, LostInfo info) {
        if (losts.containsKey(id)) return false;
        else {
            losts.put(id, info);
        }
        return true;
    }

    @Override
    public boolean saveFound(FoundId id, FoundInfo info) {
        if (founds.containsKey(id)) return false;
        else {
            founds.put(id, info);
        }
        return true;
    }

    @Override
    public boolean updateLost(LostId id, LostInfo info) {
        if (!losts.containsKey(id)) return false;
        else {
            losts.put(id, info);
        }
        return true;
    }

    @Override
    public boolean updateFound(FoundId id, FoundInfo info) {
        if (!founds.containsKey(id)) return false;
        else {
            founds.put(id, info);
        }
        return true;
    }

    @Override
    public LostInfo getLostInfo(LostId id) {
        return losts.get(id);
    }

    @Override
    public FoundInfo getFoundInfo(FoundId id) {
        return founds.get(id);
    }

    @Override
    public Stream<LostId> getAllLosts() {
        return losts.keySet().stream();
    }

    @Override
    public Stream<FoundId> getAllFounds() {
        return founds.keySet().stream();
    }

    @Override
    public void deleteLost(LostId id) {
        losts.remove(id);
    }

    @Override
    public void deleteFound(FoundId id) {
        founds.remove(id);
    }

    private static LostFoundRepository instance = new LostFoundMemoryRepository();

    public static LostFoundRepository get() {
        return instance;
    }
}
