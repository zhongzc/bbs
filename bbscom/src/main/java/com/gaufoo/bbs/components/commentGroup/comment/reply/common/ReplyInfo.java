package com.gaufoo.bbs.components.commentGroup.comment.reply.common;

import java.time.Instant;
import java.util.Objects;

public class ReplyInfo {
    public final String replier;
    public final String contentId;
    public final String replyTo;
    public final Instant creationTime;

    private ReplyInfo(String replier, String contentId, String replyTo, Instant creationTime) {
        this.replier = replier;
        this.contentId = contentId;
        this.replyTo = replyTo;
        this.creationTime = creationTime;
    }

    public static ReplyInfo of(String replier, String contentId, String replyTo) {
        return new ReplyInfo(replier, contentId, replyTo, Instant.now());
    }

    public ReplyInfo modReplier(String replier) {
        return new ReplyInfo(replier, this.contentId, this.replyTo, this.creationTime);
    }

    public ReplyInfo modContentId(String contentId) {
        return new ReplyInfo(this.replier, contentId, this.replyTo, this.creationTime);
    }

    public ReplyInfo modReplyTo(String replyTo) {
        return new ReplyInfo(this.replier, this.contentId, replyTo, this.creationTime);
    }

    public ReplyInfo modCreationTime(Instant creationTime) {
        return new ReplyInfo(this.replier, this.contentId, this.replyTo, creationTime);
    }

    @Override
    public String toString() {
        return "ReplyInfo" + "(" + "'" + this.replier + "'" + ", " + "'" + this.contentId + "'" + ", " + "'" + this.replyTo + "'" + ", " + this.creationTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplyInfo other = (ReplyInfo) o;
        return Objects.equals(replier, other.replier) &&
                Objects.equals(contentId, other.contentId) &&
                Objects.equals(replyTo, other.replyTo) &&
                Objects.equals(creationTime, other.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replier, contentId, replyTo, creationTime);
    }
}
