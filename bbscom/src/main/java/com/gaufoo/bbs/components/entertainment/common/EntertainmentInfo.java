package com.gaufoo.bbs.components.entertainment.common;

import java.time.Instant;
import java.util.Objects;

public class EntertainmentInfo {
    final public String title;
    final public String contentId;
    final public String authorId;
    final public String commentGroupId;
    final public Instant createTime;

    private EntertainmentInfo(String title, String contentId, String authorId, String commentGroupId, Instant createTime) {
        this.title = title;
        this.contentId = contentId;
        this.authorId = authorId;
        this.commentGroupId = commentGroupId;
        this.createTime = createTime;
    }

    public static EntertainmentInfo of(String title, String contentId, String authorId, String commentGroupId, Instant createTime) {
        return new EntertainmentInfo(title, contentId, authorId, commentGroupId, createTime);
    }

    public static EntertainmentInfo of(String title, String contentId, String authorId, String commentGroupId) {
        return new EntertainmentInfo(title, contentId, authorId, commentGroupId, Instant.now());
    }

    public EntertainmentInfo modTitle(String title) {
        return new EntertainmentInfo(title, this.contentId, this.authorId, this.commentGroupId, this.createTime);
    }

    public EntertainmentInfo modContentId(String contentId) {
        return new EntertainmentInfo(this.title, contentId, this.authorId, this.commentGroupId, this.createTime);
    }

    public EntertainmentInfo modAuthorId(String authorId) {
        return new EntertainmentInfo(this.title, this.contentId, authorId, this.commentGroupId, this.createTime);
    }

    public EntertainmentInfo modCommentGroupId(String commentGroupId) {
        return new EntertainmentInfo(this.title, this.contentId, this.authorId, commentGroupId, this.createTime);
    }

    public EntertainmentInfo modCreateTime(Instant createTime) {
        return new EntertainmentInfo(this.title, this.contentId, this.authorId, this.commentGroupId, createTime);
    }

    @Override
    public String toString() {
        return "EntertainmentInfo" + "(" + "'" + this.title + "'" + ", " + "'" + this.contentId + "'" + ", " + "'" + this.authorId + "'" + ", " + "'" + this.commentGroupId + "'" + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntertainmentInfo other = (EntertainmentInfo) o;
        return Objects.equals(title, other.title) &&
                Objects.equals(contentId, other.contentId) &&
                Objects.equals(authorId, other.authorId) &&
                Objects.equals(commentGroupId, other.commentGroupId) &&
                Objects.equals(createTime, other.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, contentId, authorId, commentGroupId, createTime);
    }
}
