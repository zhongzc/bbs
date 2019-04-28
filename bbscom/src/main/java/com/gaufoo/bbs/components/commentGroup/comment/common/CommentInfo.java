package com.gaufoo.bbs.components.commentGroup.comment.common;

import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final public class CommentInfo {
    public final String contentId;
    public final String commenter;
    public final Instant creationTime;

    private CommentInfo(String contentId, String commenter, Instant creationTime) {
        this.contentId = contentId;
        this.commenter = commenter;
        this.creationTime = creationTime;
    }

    public static CommentInfo of(String contentId, String commenter) {
        return new CommentInfo(contentId, commenter, Instant.now());
    }

    public CommentInfo modContentId(String contentId) {
        return new CommentInfo(contentId, this.commenter, this.creationTime);
    }

    public CommentInfo modCommenter(String commenter) {
        return new CommentInfo(this.contentId, commenter, this.creationTime);
    }

    public CommentInfo modCreationTime(Instant creationTime) {
        return new CommentInfo(this.contentId, this.commenter, creationTime);
    }

    @Override
    public String toString() {
        return "CommentInfo" + "(" + "'" + this.contentId + "'" + ", " + "'" + this.commenter + "'" + ", " + this.creationTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentInfo other = (CommentInfo) o;
        return Objects.equals(contentId, other.contentId) &&
                Objects.equals(commenter, other.commenter) &&
                Objects.equals(creationTime, other.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, commenter, creationTime);
    }
}
