package com.gaufoo.bbs.components.commentGroup.comment.reply.common;

import java.util.Objects;

public class ReplyInfo {
    public final String replier;
    public final String content;
    public final String replyTo;

    private ReplyInfo(String replier, String content, String replyTo) {
        this.replier = replier;
        this.content = content;
        this.replyTo = replyTo;
    }

    public static ReplyInfo of(String replier, String content, String replyTo) {
        return new ReplyInfo(replier, content, replyTo);
    }

    public ReplyInfo modReplier(String replier) {
        return new ReplyInfo(replier, this.content, this.replyTo);
    }

    public ReplyInfo modContent(String content) {
        return new ReplyInfo(this.replier, content, this.replyTo);
    }

    public ReplyInfo modReplyTo(String replyTo) {
        return new ReplyInfo(this.replier, this.content, replyTo);
    }

    @Override
    public String toString() {
        return "ReplyInfo" + "(" + "'" + this.replier + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.replyTo + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplyInfo other = (ReplyInfo) o;
        return Objects.equals(replier, other.replier) &&
                Objects.equals(content, other.content) &&
                Objects.equals(replyTo, other.replyTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replier, content, replyTo);
    }
}
