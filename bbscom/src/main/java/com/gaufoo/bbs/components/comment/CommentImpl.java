package com.gaufoo.bbs.components.comment;

import com.gaufoo.bbs.components._repositories.CommentMemoryRepository;
import com.gaufoo.bbs.components.comment.reply.Reply;
import com.gaufoo.bbs.components.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.idGenerator.IdRepository;
import com.gaufoo.bbs.components.comment.common.CommentId;
import com.gaufoo.bbs.components.comment.common.CommentInfo;

import java.util.Optional;

public class CommentImpl implements Comment {
    private final String name;
    private final IdGenerator cmmtIds;
    private final CommentRepository repository;
    private final Reply reply;

    CommentImpl(String componentName, IdGenerator cmmtIds, IdGenerator rpyIds, CommentRepository commentRepository, ReplyRepository replyRepository) {
        this.name = componentName;
        this.cmmtIds = cmmtIds;
        this.repository = commentRepository;
        this.reply = Reply.defau1t(rpyIds, replyRepository);
    }

    @Override
    public Optional<CommentId> comment(CommentInfo commentInfo) {
        CommentId id = CommentId.of(cmmtIds.generateId());

        if (repository.saveComment(id, commentInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReplyId> reply(CommentId commentId, ReplyInfo replyInfo) {
        return Optional.ofNullable(repository.getComment(commentId)).flatMap(info ->
                reply.reply(replyInfo).flatMap(rpyId -> {
                    info.replies.add(rpyId);
                    if (repository.updateComment(commentId, info.modReplies(info.replies)))
                        return Optional.of(rpyId);
                    else
                        return Optional.empty();
                }));
    }

    @Override
    public Optional<CommentInfo> commentInfo(CommentId commentId) {
        return Optional.ofNullable(repository.getComment(commentId));
    }

    @Override
    public Optional<ReplyInfo> replyInfo(ReplyId replyId) {
        return this.reply.replyInfo(replyId);
    }


    @Override
    public void removeComment(CommentId commentId) {
        repository.deleteComment(commentId);
    }

    @Override
    public void removeReply(CommentId commentId, ReplyId replyId) {
        Optional.ofNullable(repository.getComment(commentId)).ifPresent(info -> {
            info.replies.remove(replyId);
            repository.updateComment(commentId,
                    info.modReplies(info.replies));
        });
        reply.removeReply(replyId);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void shutdown() {
        this.repository.shutdown();
        this.reply.shutdown();
    }

    public static void main(String[] args) {
//        IdGenerator cmmtIdGen = IdGenerator.seqInteger("", IdRepository.fakeIdRepository());
//        CommentRepository repository = CommentMemoryRepository.get("");
//        Comment comment = Comment.defau1t("", cmmtIdGen, repository);
//        CommentId id = comment.comment(CommentInfo.of("subj1", "hello", "user1")).get();
//        System.out.println(comment.commentInfo(id));
//        comment.reply(id, CommentInfo.Reply.of("user2", "world"));
//        System.out.println(comment.commentInfo(id));
//        comment.reply(id, CommentInfo.Reply.of("user3", "!", "user2"));
//        System.out.println(comment.commentInfo(id));
    }
}
