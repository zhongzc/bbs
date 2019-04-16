package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.reply.CommentRepository;
import com.gaufoo.bbs.components.reply.common.CommentId;
import com.gaufoo.bbs.components.reply.common.CommentInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;

public class CommentSstRepository implements CommentRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private final SST idToInfo;

    private CommentSstRepository(String repositoryName, Path storingPath) {
        this.repositoryName = repositoryName;
        this.idToInfo = SST.of(repositoryName, storingPath.resolve(repositoryName));
    }

    @Override
    public boolean saveComment(CommentId id, CommentInfo commentInfo) {
        return SstUtils.setEntry(idToInfo, id.value, gson.toJson(commentInfo));
    }

    @Override
    public CommentInfo getComment(CommentId id) {
        return SstUtils.getEntry(idToInfo, id.value,
                info -> gson.fromJson(info, CommentInfo.class));
    }

    @Override
    public boolean updateComment(CommentId id, CommentInfo commentInfo) {
        return saveComment(id, commentInfo);
    }

    @Override
    public void deleteComment(CommentId id) {
        SstUtils.removeEntryWithKey(idToInfo, id.value);
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static CommentRepository get(String repositoryName, Path storingPath) {
        return new CommentSstRepository(repositoryName, storingPath);
    }
}
