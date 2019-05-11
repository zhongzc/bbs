package com.gaufoo.bbs.components.active;

import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.google.gson.Gson;
import com.gaufoo.db.TenGoKV;
import com.gaufoo.db.TenGoKV.TenGoKVBuilder;
import com.gaufoo.db.common.Index;
import com.gaufoo.db.common.IndexFactor;

import java.nio.file.Path;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.gaufoo.bbs.util.Util.formatStrWithLen;

public class ActiveTGRepository implements ActiveRepository {
    private final TenGoKV<ActiveId, ActiveInfo> db;
    private TenGoKV.IndexHandler<ActiveId, ActiveInfo> hd;
    private final int maxActiveGroupLen;
    private final int maxIdLen;
    private final BiFunction<String, String, ActiveId> consId;

    private ActiveTGRepository(Path storing, int maxActiveGroupLen, int maxIdLen) {
        final Gson gson = new Gson();
        this.db = TenGoKVBuilder.<ActiveId, ActiveInfo>get().withPath(storing)
                .keySerializer(i -> i.activeGroup + i.id, maxActiveGroupLen + maxIdLen)
                .valueSerializer(gson::toJson)
                .keyShaper(i -> ActiveId.of(i.substring(0, maxActiveGroupLen), i.substring(maxActiveGroupLen)))
                .valueShaper(s -> gson.fromJson(s, ActiveInfo.class))
                .withAggregate(Collections.emptyList())
                .withIndex(Index.<ActiveId, ActiveInfo>of()
                        .groupBy(IndexFactor.of((k, v) -> k.activeGroup, maxActiveGroupLen), Collections.emptyList())
                        .sortBy(IndexFactor.of((k, v) -> String.format("%014d", v.time.toEpochMilli()), 14)).build())
                .takeHandler(d -> this.hd = d)
                .build();
        this.maxActiveGroupLen = maxActiveGroupLen;
        this.maxIdLen = maxIdLen;
        this.consId = (ag, id) -> ActiveId.of(formatStrWithLen(ag, maxActiveGroupLen), formatStrWithLen(id, maxIdLen));
    }

    @Override
    public boolean saveActive(String activeGroup, String id, ActiveInfo activeInfo) {
        return this.db.saveValue(consId.apply(activeGroup, id), activeInfo);
    }

    @Override
    public ActiveInfo getActive(String activeGroup, String id) {
        return this.db.getValue(consId.apply(activeGroup, id));
    }

    @Override
    public boolean updateActive(String activeGroup, String id, ActiveInfo activeInfo) {
        return this.db.deleteValue(consId.apply(activeGroup, id)) &&
                this.db.saveValue(consId.apply(activeGroup, id), activeInfo);
    }

    @Override
    public Stream<String> getAllAsc(String activeGroup, int idLen) {
        return this.db.getAllKeysAsc(hd, hd.getGroupChain().group(formatStrWithLen(activeGroup, maxActiveGroupLen)).endGroup())
                .map(i -> i.id.substring(maxIdLen - idLen));
    }

    @Override
    public Stream<String> getAllDes(String activeGroup, int idLen) {
        return this.db.getAllKeysDes(hd, hd.getGroupChain().group(formatStrWithLen(activeGroup, maxActiveGroupLen)).endGroup())
                .map(i -> i.id.substring(maxIdLen - idLen));
    }

    @Override
    public boolean delete(String activeGroup, String id) {
        return this.db.deleteValue(consId.apply(activeGroup, id));
    }

    @Override
    public boolean delete(String activeGroup) {
        return this.db.deleteAll(hd, hd.getGroupChain().group(formatStrWithLen(activeGroup, maxActiveGroupLen)).endGroup());
    }

    @Override
    public void shutdown() {
        db.shutdown();
    }

    public static ActiveRepository get(Path storingPath, int maxActiveGroupLen, int maxIdLen) {
        return new ActiveTGRepository(storingPath, maxActiveGroupLen, maxIdLen);
    }

    private static class ActiveId {
        public final String activeGroup;
        public final String id;

        private ActiveId(String activeGroup, String id) {
            this.activeGroup = activeGroup;
            this.id = id;
        }

        public static ActiveId of(String activeGroup, String id) {
            return new ActiveId(activeGroup, id);
        }
    }
}
