package com.gaufoo.bbs.components.reply;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.reply.common.CommentId;
import com.gaufoo.bbs.components.reply.common.CommentInfo;

import java.util.Optional;

public interface Comment {
    Optional<CommentId> comment(CommentInfo commentInfo);

    boolean reply(CommentId commentId, CommentInfo.Reply reply);

    Optional<CommentInfo> commentInfo(CommentId commentId);

    void removeComment(CommentId commentId);

    String getName();

    static Comment defau1t(String componentName, IdGenerator idGenerator, CommentRepository commentRepository) {
        return new CommentImpl(componentName, idGenerator, commentRepository);
    }
}
