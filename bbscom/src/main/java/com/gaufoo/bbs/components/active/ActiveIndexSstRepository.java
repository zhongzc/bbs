package com.gaufoo.bbs.components.active;

import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.util.IndexableSST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.time.Instant;
import java.util.stream.Stream;

public class ActiveIndexSstRepository implements ActiveRepository {
    private Gson gson = new Gson();
    private IndexableSST<String, ActiveInfo> sst;

    private IndexableSST.ExtractorId clusterExId;

    public ActiveIndexSstRepository(Path storingPath) {
        this.sst = IndexableSST.<String, ActiveInfo>builder()
                .keySerializer(i -> i).keyShaper(i -> i)
                .valueSerializer(gson::toJson).valueShaper(info -> gson.fromJson(info, ActiveInfo.class))
                .withClustering(ActiveIndexSstRepository::extract, 28).takeId(id -> clusterExId = id)
                .storingPath(storingPath)
                .build();
    }

    private static String extract(String groupAndActId, ActiveInfo info) {
        String groupId = groupAndActId.substring(0, 2);
        String activeId = groupAndActId.substring(2);
        return formatAG(groupId) + format(info.time) + activeId;
    }


    private static String format(Instant time) {
        return String.format("%014d", time.toEpochMilli());
    }

    private static String formatAG(String activeGroup) {
        return String.format("%14s", activeGroup);
    }

    @Override
    public boolean saveActive(String activeGroup, String id, ActiveInfo activeInfo) {
        return sst.saveValue(formatAG(activeGroup) + id, activeInfo);
    }

    @Override
    public ActiveInfo getActive(String activeGroup, String id) {
        return sst.getValue(formatAG(activeGroup) + id);
    }

    @Override
    public boolean updateActive(String activeGroup, String id, ActiveInfo activeInfo) {
        String key = formatAG(activeGroup) + id;
        return sst.deleteValue(key) && sst.saveValue(key, activeInfo);
    }

    @Override
    public Stream<String> getAllAsc(String activeGroup) {
        return sst.getAllValuesAscBy(clusterExId, formatAG(activeGroup));
    }

    @Override
    public Stream<String> getAllDes(String activeGroup) {
        return sst.getAllValuesDesBy(clusterExId, formatAG(activeGroup));
    }

    @Override
    public boolean delete(String activeGroup, String id) {
        return sst.deleteValue(formatAG(activeGroup) + id);
    }

    @Override
    public boolean delete(String activeGroup) {
        // TODO: no idea
        return false;
    }
}
