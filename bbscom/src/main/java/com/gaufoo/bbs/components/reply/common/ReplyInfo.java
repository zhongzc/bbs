package com.gaufoo.bbs.components.reply.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final public class ReplyInfo {
    // 被回复的帖子
    public final String subject;
    public final String content;
    public final String replier;

    // 楼中楼
    public final List<Comment> comments;

    private ReplyInfo(String subject, String content, String replier, List<Comment> comments) {
        this.subject = subject;
        this.content = content;
        this.replier = replier;
        this.comments = comments;
    }

    public static ReplyInfo of(String subject, String content, String replier, List<Comment> comments) {
        return new ReplyInfo(subject, content, replier, comments);
    }

    public static ReplyInfo of(String subject, String content, String replier) {
        return new ReplyInfo(subject, content, replier, new ArrayList<>());
    }

    public ReplyInfo modSubject(String subject) {
        return new ReplyInfo(subject, this.content, this.replier, this.comments);
    }

    public ReplyInfo modContent(String content) {
        return new ReplyInfo(this.subject, content, this.replier, this.comments);
    }

    public ReplyInfo modReplier(String replier) {
        return new ReplyInfo(this.subject, this.content, replier, this.comments);
    }

    public ReplyInfo modComments(List<Comment> comments) {
        return new ReplyInfo(this.subject, this.content, this.replier, comments);
    }

    @Override
    public String toString() {
        return "ReplyInfo" + "(" + "'" + this.subject + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.replier + "'" + ", " + this.comments + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplyInfo other = (ReplyInfo) o;
        return Objects.equals(subject, other.subject) &&
                Objects.equals(content, other.content) &&
                Objects.equals(replier, other.replier) &&
                Objects.equals(comments, other.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, content, replier, comments);
    }

    final public static class Comment {
        public final String commentator;
        public final String content;
        public final String commentTo;

        private Comment(String commentator, String content, String commentTo) {
            this.commentator = commentator;
            this.content = content;
            this.commentTo = commentTo;
        }

        public static Comment of(String commentator, String content, String commentTo) {
            return new Comment(commentator, content, commentTo);
        }

        public static Comment of(String commentator, String content) {
            return new Comment(commentator, content, null);
        }

        public Comment modCommentator(String commentator) {
            return new Comment(commentator, this.content, this.commentTo);
        }

        public Comment modContent(String content) {
            return new Comment(this.commentator, content, this.commentTo);
        }

        public Comment modCommentTo(String commentTo) {
            return new Comment(this.commentator, this.content, commentTo);
        }

        @Override
        public String toString() {
            return "Comment" + "(" + "'" + this.commentator + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.commentTo + "'" + ')';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Comment other = (Comment) o;
            return Objects.equals(commentator, other.commentator) &&
                    Objects.equals(content, other.content) &&
                    Objects.equals(commentTo, other.commentTo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(commentator, content, commentTo);
        }
    }
}
