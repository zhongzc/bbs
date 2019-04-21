package com.gaufoo.bbs.components.commentGroup;

import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class CommentGroupSstRepository implements CommentGroupRepository {
    private final SST idToCnt;
    private final SST cluster;

    private CommentGroupSstRepository(Path storingPath) {
        this.idToCnt = SST.of("id-to-cnt", storingPath);
        this.cluster = SST.of("cluster", storingPath);
    }

    @Override
    public boolean cons(CommentGroupId commentGroupId) {
        if (SstUtils.contains(idToCnt, commentGroupId.value)) return false;
        return SstUtils.setEntry(idToCnt, commentGroupId.value, String.valueOf(0));
    }

    @Override
    public boolean addComment(CommentGroupId commentGroupId, CommentId commentId) {
        if (!SstUtils.contains(idToCnt, commentGroupId.value)) return false;

        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(idToCnt, commentId.value, String.valueOf(getCommentsCount(commentGroupId) + 1)));
        tasks.add(SstUtils.setEntryAsync(cluster, concat(commentGroupId, commentId), commentId.value));

        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    @Override
    public Stream<CommentId> getAllComments(CommentGroupId commentGroupId) {
        return SstUtils.waitFuture(cluster.rangeKeysAsc(commentGroupId.value + "00000000", commentGroupId.value + "99999999"))
                .map(s -> s.map(CommentGroupSstRepository::retrieveId)).orElse(Stream.empty());
    }

    @Override
    public void deleteComments(CommentGroupId commentGroupId) {
        if (!SstUtils.contains(idToCnt, commentGroupId.value)) return;

        SstUtils.waitAllFuturesPar(
                idToCnt.delete(commentGroupId.value),
                cluster.rangeKeysAsc(commentGroupId.value + "00000000", commentGroupId.value + "99999999").thenAccept(
                        s -> SstUtils.waitAllFuturesPar(s.map(cluster::delete))
                )
        );
    }

    @Override
    public void deleteComment(CommentGroupId commentGroupId, CommentId commentId) {
        if (!SstUtils.contains(idToCnt, commentGroupId.value)) return;
        SstUtils.removeEntryWithKey(cluster, concat(commentGroupId, commentId));
    }

    @Override
    public Long getCommentsCount(CommentGroupId commentGroupId) {
        return SstUtils.getEntry(idToCnt, commentGroupId.value, Long::parseLong);
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToCnt.shutdown(), cluster.shutdown());
    }

    private static String concat(CommentGroupId commentGroupId, CommentId commentId) {
        return commentGroupId.value + commentId.value;
    }

    private static CommentId retrieveId(String string) {
        return CommentId.of(string.substring(8));
    }

    public static CommentGroupRepository get(Path storingPath) {
        return new CommentGroupSstRepository(storingPath);
    }
}
