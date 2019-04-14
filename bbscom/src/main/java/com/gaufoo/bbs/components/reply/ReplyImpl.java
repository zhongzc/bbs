package com.gaufoo.bbs.components.reply;

import com.gaufoo.bbs.components._repositories.ReplyMemoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.idGenerator.IdRepository;
import com.gaufoo.bbs.components.reply.common.ReplyId;
import com.gaufoo.bbs.components.reply.common.ReplyInfo;

import java.util.Optional;

public class ReplyImpl implements Reply {
    private final String name;
    private final IdGenerator idGenerator;
    private final ReplyRepository repository;

    ReplyImpl(String componentName, IdGenerator idGenerator, ReplyRepository replyRepository) {
        this.name = componentName;
        this.idGenerator = idGenerator;
        this.repository = replyRepository;
    }

    @Override
    public Optional<ReplyId> reply(ReplyInfo replyInfo) {
        ReplyId id = ReplyId.of(idGenerator.generateId());

        if (repository.saveReply(id, replyInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean comment(ReplyId replyId, ReplyInfo.Comment comment) {
        return Optional.ofNullable(repository.getReply(replyId)).map(info -> {
            info.comments.add(comment);
            return repository.updateReply(replyId, info.modComments(info.comments));
        }).orElse(false);
    }

    @Override
    public Optional<ReplyInfo> replyInfo(ReplyId replyId) {
        return Optional.ofNullable(repository.getReply(replyId));
    }

    @Override
    public void removeReply(ReplyId replyId) {
        repository.deleteReply(replyId);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static void main(String[] args) {
        IdGenerator idGenerator = IdGenerator.seqInteger("", IdRepository.fakeIdRepository());
        ReplyRepository repository = ReplyMemoryRepository.get("");
        Reply reply = Reply.defau1t("", idGenerator, repository);
        ReplyId id = reply.reply(ReplyInfo.of("subj1", "hello", "user1")).get();
        System.out.println(reply.replyInfo(id));
        reply.comment(id, ReplyInfo.Comment.of("user2", "world"));
        System.out.println(reply.replyInfo(id));
        reply.comment(id, ReplyInfo.Comment.of("user3", "!", "user2"));
        System.out.println(reply.replyInfo(id));
    }
}
