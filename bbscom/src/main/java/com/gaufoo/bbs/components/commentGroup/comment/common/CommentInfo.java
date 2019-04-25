package com.gaufoo.bbs.components.commentGroup.comment.common;

import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final public class CommentInfo {
    public final String contentId;
    public final String commenter;

    private CommentInfo(String contentId, String commenter) {
        this.contentId = contentId;
        this.commenter = commenter;
    }

    public static CommentInfo of(String contentId, String commenter) {
        return new CommentInfo(contentId, commenter);
    }

    public CommentInfo modContentId(String contentId) {
        return new CommentInfo(contentId, this.commenter);
    }

    public CommentInfo modCommenter(String commenter) {
        return new CommentInfo(this.contentId, commenter);
    }

    @Override
    public String toString() {
        return "CommentInfo" + "(" + "'" + this.contentId + "'" + ", " + "'" + this.commenter + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentInfo other = (CommentInfo) o;
        return Objects.equals(contentId, other.contentId) &&
                Objects.equals(commenter, other.commenter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, commenter);
    }
}
