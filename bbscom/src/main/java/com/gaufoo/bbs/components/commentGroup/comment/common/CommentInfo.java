package com.gaufoo.bbs.components.commentGroup.comment.common;

import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final public class CommentInfo {
    // 被回复的帖子
    public final String subject;
    public final String content;
    public final String commenter;

    private CommentInfo(String subject, String content, String commenter) {
        this.subject = subject;
        this.content = content;
        this.commenter = commenter;
    }

    public static CommentInfo of(String subject, String content, String commenter) {
        return new CommentInfo(subject, content, commenter);
    }

    public CommentInfo modSubject(String subject) {
        return new CommentInfo(subject, this.content, this.commenter);
    }

    public CommentInfo modContent(String content) {
        return new CommentInfo(this.subject, content, this.commenter);
    }

    public CommentInfo modCommenter(String commenter) {
        return new CommentInfo(this.subject, this.content, commenter);
    }

    @Override
    public String toString() {
        return "CommentInfo" + "(" + "'" + this.subject + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.commenter + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentInfo other = (CommentInfo) o;
        return Objects.equals(subject, other.subject) &&
                Objects.equals(content, other.content) &&
                Objects.equals(commenter, other.commenter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, content, commenter);
    }
}
