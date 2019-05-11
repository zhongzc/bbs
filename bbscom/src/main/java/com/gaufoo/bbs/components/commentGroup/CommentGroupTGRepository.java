package com.gaufoo.bbs.components.commentGroup;

import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.db.TenGoKV;
import com.gaufoo.db.common.Index;
import com.gaufoo.db.common.IndexFactor;

import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

public class CommentGroupTGRepository implements CommentGroupRepository {
    private final TenGoKV<CommentId, CommentGroupId> db;
    private TenGoKV.IndexHandler<CommentId, CommentGroupId> hd;

    private CommentGroupTGRepository(Path storingPath) {
        this.db = TenGoKV.TenGoKVBuilder.<CommentId, CommentGroupId>get()
                .withPath(storingPath)
                .keySerializer(k -> k.value, 8)
                .valueSerializer(v -> v.value)
                .keyShaper(CommentId::of)
                .valueShaper(CommentGroupId::of)
                .withAggregate(Collections.emptyList())
                .withIndex(Index.<CommentId, CommentGroupId>of()
                        .groupBy(IndexFactor.of((ci, cgi) -> cgi.value, 8),
                                Collections.singletonList(Index.Aggregate.Count)).build())
                .takeHandler(hd -> this.hd = hd)
                .build();
    }

    @Override
    public boolean cons(CommentGroupId commentGroupId) {
        return true;
    }

    @Override
    public boolean addComment(CommentGroupId commentGroupId, CommentId commentId) {
        return this.db.saveValue(commentId, commentGroupId);
    }

    @Override
    public Stream<CommentId> getAllComments(CommentGroupId commentGroupId) {
        return this.db.getAllKeysAsc(hd, hd.getGroupChain().group(commentGroupId.value).endGroup());
    }

    @Override
    public boolean deleteComments(CommentGroupId commentGroupId) {
        return this.db.deleteAll(hd, hd.getGroupChain().group(commentGroupId.value).endGroup());
    }

    @Override
    public boolean deleteComment(CommentGroupId commentGroupId, CommentId commentId) {
        return this.db.deleteValue(commentId);
    }

    @Override
    public Long getCommentsCount(CommentGroupId commentGroupId) {
        return this.db.getCount(hd, hd.getGroupChain().group(commentGroupId.value).endGroup());
    }

    @Override
    public void shutdown() {
        this.db.shutdown();
    }

    public static CommentGroupRepository get(Path storingPath) {
        return new CommentGroupTGRepository(storingPath);
    }
}
