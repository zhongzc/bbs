package com.gaufoo.bbs.components.reply;

import com.gaufoo.bbs.components._repositories.CommentMemoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.idGenerator.IdRepository;
import com.gaufoo.bbs.components.reply.common.CommentId;
import com.gaufoo.bbs.components.reply.common.CommentInfo;

import java.util.Optional;

public class CommentImpl implements Comment {
    private final String name;
    private final IdGenerator idGenerator;
    private final CommentRepository repository;

    CommentImpl(String componentName, IdGenerator idGenerator, CommentRepository commentRepository) {
        this.name = componentName;
        this.idGenerator = idGenerator;
        this.repository = commentRepository;
    }

    @Override
    public Optional<CommentId> comment(CommentInfo commentInfo) {
        CommentId id = CommentId.of(idGenerator.generateId());

        if (repository.saveComment(id, commentInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean reply(CommentId commentId, CommentInfo.Reply reply) {
        return Optional.ofNullable(repository.getComment(commentId)).map(info -> {
            info.replies.add(reply);
            return repository.updateComment(commentId, info.modReplies(info.replies));
        }).orElse(false);
    }

    @Override
    public Optional<CommentInfo> commentInfo(CommentId commentId) {
        return Optional.ofNullable(repository.getComment(commentId));
    }

    @Override
    public void removeComment(CommentId commentId) {
        repository.deleteComment(commentId);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static void main(String[] args) {
        IdGenerator idGenerator = IdGenerator.seqInteger("", IdRepository.fakeIdRepository());
        CommentRepository repository = CommentMemoryRepository.get("");
        Comment comment = Comment.defau1t("", idGenerator, repository);
        CommentId id = comment.comment(CommentInfo.of("subj1", "hello", "user1")).get();
        System.out.println(comment.commentInfo(id));
        comment.reply(id, CommentInfo.Reply.of("user2", "world"));
        System.out.println(comment.commentInfo(id));
        comment.reply(id, CommentInfo.Reply.of("user3", "!", "user2"));
        System.out.println(comment.commentInfo(id));
    }
}
