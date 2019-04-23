package com.gaufoo.bbs.components.commentGroup.comment;

import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class CommentSstRepository implements CommentRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;
    private final SST idToRpyCnt;
    private final SST cluster;

    private CommentSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
        this.idToRpyCnt = SST.of("id-to-cnt", storingPath);
        this.cluster = SST.of("cluster", storingPath);
    }

    @Override
    public boolean saveComment(CommentId id, CommentInfo commentInfo) {
        if (SstUtils.contains(idToInfo, id.value)) return false;

        if (SstUtils.setEntry(idToInfo, id.value, gson.toJson(commentInfo))) {
            return SstUtils.setEntry(idToRpyCnt, id.value, String.valueOf(0L));
        } else return false;
    }

    @Override
    public CommentInfo getComment(CommentId id) {
        return SstUtils.getEntry(idToInfo, id.value,
                info -> gson.fromJson(info, CommentInfo.class));
    }

    @Override
    public boolean addReply(CommentId commentId, ReplyId replyId) {
        return Optional.ofNullable(getRepliesCount(commentId)).map(c -> {
            List<CompletionStage<Boolean>> tasks = new ArrayList<>();
            tasks.add(SstUtils.setEntryAsync(idToRpyCnt, commentId.value, String.valueOf(c + 1)));
            tasks.add(SstUtils.setEntryAsync(cluster, concat(commentId, replyId), "GAUFOO"));
            return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
        }).orElse(false);
    }

    @Override
    public Stream<ReplyId> getAllReplies(CommentId id) {
        return SstUtils.waitFuture(cluster
                .rangeKeysAsc(concat(id, ReplyId.of("00000000")), concat(id, ReplyId.of("99999999")))
                .thenApply(ss -> ss.map(CommentSstRepository::retrieveId))).orElse(Stream.empty());
    }

    @Override
    public Long getRepliesCount(CommentId id) {
        return Optional.ofNullable(SstUtils.getEntry(idToRpyCnt, id.value, Long::parseLong)).orElse(0L);
    }

    @Override
    public boolean deleteComment(CommentId id) {
        if (SstUtils.removeEntryByKey(idToInfo, id.value) != null && SstUtils.removeEntryByKey(idToRpyCnt, id.value) != null) {
            getAllReplies(id).forEach(rpyid -> SstUtils.removeEntryByKey(cluster, concat(id, rpyid)));
            return true;
        } else return false;
    }

    @Override
    public boolean deleteReply(CommentId commentId, ReplyId replyId) {
        if (SstUtils.removeEntryByKey(cluster, concat(commentId, replyId)) != null) {
            return Optional.ofNullable(getRepliesCount(commentId))
                    .map(l -> SstUtils.setEntry(idToRpyCnt, commentId.value, String.valueOf(l - 1)))
                    .orElse(false);
        } else return false;
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToInfo.shutdown(), idToRpyCnt.shutdown(), cluster.shutdown());
    }

    private static String concat(CommentId commentId, ReplyId replyId) {
        return commentId.value + replyId.value;
    }

    private static ReplyId retrieveId(String string) {
        return ReplyId.of(string.substring(8));
    }

    public static CommentRepository get(Path storingPath) {
        return new CommentSstRepository(storingPath);
    }
}
