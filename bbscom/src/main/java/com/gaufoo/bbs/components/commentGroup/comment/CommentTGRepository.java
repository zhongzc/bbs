package com.gaufoo.bbs.components.commentGroup.comment;

import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.db.TenGoKV;
import com.gaufoo.db.common.Index;
import com.gaufoo.db.common.IndexFactor;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

public class CommentTGRepository implements CommentRepository {
    private final TenGoKV<CommentId, CommentInfo> db;
    private final TenGoKV<ReplyId, CommentId> rdb;
    private TenGoKV.IndexHandler<ReplyId, CommentId> hd;

    private CommentTGRepository(Path storingPath) {
        Gson gson = new Gson();
        this.db = TenGoKV.TenGoKVBuilder.<CommentId, CommentInfo>get()
            .withPath(storingPath)
            .keySerializer(k -> k.value, 8)
            .valueSerializer(gson::toJson)
            .keyShaper(CommentId::of)
            .valueShaper(v -> gson.fromJson(v, CommentInfo.class))
            .withAggregate(Collections.emptyList())
            .build();
        this.rdb = TenGoKV.TenGoKVBuilder.<ReplyId, CommentId>get()
                .withPath(storingPath.resolve("rpy"))
                .keySerializer(k -> k.value, 8)
                .valueSerializer(v -> v.value)
                .keyShaper(ReplyId::of)
                .valueShaper(CommentId::of)
                .withAggregate(Collections.emptyList())
                .withIndex(Index.<ReplyId, CommentId>of()
                        .groupBy(IndexFactor.of((k, v) -> v.value, 8), Collections.singletonList(Index.Aggregate.Count)).build())
                .takeHandler(hd -> this.hd = hd)
                .build();
    }


    @Override
    public boolean saveComment(CommentId id, CommentInfo commentInfo) {
        return this.db.saveValue(id, commentInfo);
    }

    @Override
    public CommentInfo getComment(CommentId id) {
        return this.db.getValue(id);
    }

    @Override
    public boolean addReply(CommentId commentId, ReplyId replyId) {
        return this.rdb.saveValue(replyId, commentId);
    }

    @Override
    public Stream<ReplyId> getAllReplies(CommentId id) {
        return this.rdb.getAllKeysAsc(hd, hd.getGroupChain().group(id.value).endGroup());
    }

    @Override
    public Long getRepliesCount(CommentId id) {
        return this.rdb.getCount(hd, hd.getGroupChain().group(id.value).endGroup());
    }

    @Override
    public boolean deleteComment(CommentId id) {
        return this.rdb.deleteAll(hd, hd.getGroupChain().group(id.value).endGroup())
                && this.db.deleteValue(id);
    }

    @Override
    public boolean deleteReply(CommentId commentId, ReplyId replyId) {
        return this.rdb.deleteValue(replyId);
    }

    @Override
    public void shutdown() {
        this.rdb.shutdown();
        this.db.shutdown();
    }

    public static CommentRepository get(Path storingPath) {
        return new CommentTGRepository(storingPath);
    }
}
