package com.gaufoo.bbs.components.reply.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final public class CommentInfo {
    // 被回复的帖子
    public final String subject;
    public final String content;
    public final String commenter;

    // 楼中楼
    public final List<Reply> replies;

    private CommentInfo(String subject, String content, String commenter, List<Reply> replies) {
        this.subject = subject;
        this.content = content;
        this.commenter = commenter;
        this.replies = replies;
    }

    public static CommentInfo of(String subject, String content, String replier, List<Reply> replies) {
        return new CommentInfo(subject, content, replier, replies);
    }

    public static CommentInfo of(String subject, String content, String replier) {
        return new CommentInfo(subject, content, replier, new ArrayList<>());
    }

    public CommentInfo modSubject(String subject) {
        return new CommentInfo(subject, this.content, this.commenter, this.replies);
    }

    public CommentInfo modContent(String content) {
        return new CommentInfo(this.subject, content, this.commenter, this.replies);
    }

    public CommentInfo modCommenter(String commenter) {
        return new CommentInfo(this.subject, this.content, commenter, this.replies);
    }

    public CommentInfo modReplies(List<Reply> replies) {
        return new CommentInfo(this.subject, this.content, this.commenter, replies);
    }

    @Override
    public String toString() {
        return "CommentInfo" + "(" + "'" + this.subject + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.commenter + "'" + ", " + this.replies + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentInfo other = (CommentInfo) o;
        return Objects.equals(subject, other.subject) &&
                Objects.equals(content, other.content) &&
                Objects.equals(commenter, other.commenter) &&
                Objects.equals(replies, other.replies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, content, commenter, replies);
    }

    final public static class Reply {
        public final String replier;
        public final String content;
        public final String replyTo;

        private Reply(String replier, String content, String replyTo) {
            this.replier = replier;
            this.content = content;
            this.replyTo = replyTo;
        }

        public static Reply of(String replier, String content, String replyTo) {
            return new Reply(replier, content, replyTo);
        }

        public static Reply of(String replier, String content) {
            return new Reply(replier, content, null);
        }

        public Reply modReplier(String replier) {
            return new Reply(replier, this.content, this.replyTo);
        }

        public Reply modContent(String content) {
            return new Reply(this.replier, content, this.replyTo);
        }

        public Reply modReplyTo(String replyTo) {
            return new Reply(this.replier, this.content, replyTo);
        }

        @Override
        public String toString() {
            return "Reply" + "(" + "'" + this.replier + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.replyTo + "'" + ')';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Reply other = (Reply) o;
            return Objects.equals(replier, other.replier) &&
                    Objects.equals(content, other.content) &&
                    Objects.equals(replyTo, other.replyTo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(replier, content, replyTo);
        }
    }
}
