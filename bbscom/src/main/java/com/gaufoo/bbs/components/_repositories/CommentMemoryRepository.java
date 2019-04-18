package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.comment.CommentRepository;
import com.gaufoo.bbs.components.comment.common.CommentId;
import com.gaufoo.bbs.components.comment.common.CommentInfo;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommentMemoryRepository implements CommentRepository {
    private final static Gson gson = new Gson();
    private final String repositoryName;

    // CommentId -> CommentInfo
    private final Map<String, String> map = new ConcurrentHashMap<>();

    private CommentMemoryRepository(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    @Override
    public boolean saveComment(CommentId id, CommentInfo replyInfo) {
        if (map.containsKey(id.value)) return false;
        map.put(id.value, gson.toJson(replyInfo));
        return true;
    }

    @Override
    public CommentInfo getComment(CommentId id) {
        return gson.fromJson(map.get(id.value), CommentInfo.class);
    }

    @Override
    public boolean updateComment(CommentId id, CommentInfo commentInfo) {
        if (!map.containsKey(id.value)) return false;
        map.put(id.value, gson.toJson(commentInfo));
        return true;
    }

    @Override
    public void deleteComment(CommentId id) {
        map.remove(id.value);
    }

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static CommentRepository get(String repositoryName) {
        return new CommentMemoryRepository(repositoryName);
    }
}
