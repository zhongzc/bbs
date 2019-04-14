package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.reply.ReplyRepository;
import com.gaufoo.bbs.components.reply.common.ReplyId;
import com.gaufoo.bbs.components.reply.common.ReplyInfo;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReplyMemoryRepository implements ReplyRepository {
    private final static Gson gson = new Gson();
    private final String repositoryName;

    // ReplyId -> ReplyInfo
    private final Map<String, String> map = new ConcurrentHashMap<>();

    private ReplyMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveReply(ReplyId id, ReplyInfo replyInfo) {
        if (map.containsKey(id.value)) return false;
        map.put(id.value, gson.toJson(replyInfo));
        return true;
    }

    @Override
    public ReplyInfo getReply(ReplyId id) {
        return gson.fromJson(map.get(id.value), ReplyInfo.class);
    }

    @Override
    public boolean updateReply(ReplyId id, ReplyInfo replyInfo) {
        if (!map.containsKey(id.value)) return false;
        map.put(id.value, gson.toJson(replyInfo));
        return true;
    }

    @Override
    public void deleteReply(ReplyId id) {
        map.remove(id.value);
    }

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static ReplyRepository get(String repositoryName) {
        return new ReplyMemoryRepository(repositoryName);
    }
}
