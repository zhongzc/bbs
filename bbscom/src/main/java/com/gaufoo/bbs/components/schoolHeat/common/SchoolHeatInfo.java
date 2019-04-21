package com.gaufoo.bbs.components.schoolHeat.common;

import java.time.Instant;
import java.util.Objects;

public class SchoolHeatInfo {
    final public String title;
    final public String contentId;
    final public String authorId;
    final public String commentGroupId;
    final public Instant createTime;

    private SchoolHeatInfo(String title, String contentId, String authorId, String commentGroupId, Instant createTime) {
        this.title = title;
        this.contentId = contentId;
        this.authorId = authorId;
        this.commentGroupId = commentGroupId;
        this.createTime = createTime;
    }

    public static SchoolHeatInfo of(String title, String contentId, String authorId, String commentGroupId, Instant createTime) {
        return new SchoolHeatInfo(title, contentId, authorId, commentGroupId, createTime);
    }

    public static SchoolHeatInfo of(String title, String contentId, String authorId, String commentGroupId) {
        return new SchoolHeatInfo(title, contentId, authorId, commentGroupId, Instant.now());
    }

    public SchoolHeatInfo modTitle(String title) {
        return new SchoolHeatInfo(title, this.contentId, this.authorId, this.commentGroupId, this.createTime);
    }

    public SchoolHeatInfo modContentId(String contentId) {
        return new SchoolHeatInfo(this.title, contentId, this.authorId, this.commentGroupId, this.createTime);
    }

    public SchoolHeatInfo modAuthorId(String authorId) {
        return new SchoolHeatInfo(this.title, this.contentId, authorId, this.commentGroupId, this.createTime);
    }

    public SchoolHeatInfo modCommentGroupId(String commentGroupId) {
        return new SchoolHeatInfo(this.title, this.contentId, this.authorId, commentGroupId, this.createTime);
    }

    public SchoolHeatInfo modCreateTime(Instant createTime) {
        return new SchoolHeatInfo(this.title, this.contentId, this.authorId, this.commentGroupId, createTime);
    }

    @Override
    public String toString() {
        return "SchoolHeatInfo" + "(" + "'" + this.title + "'" + ", " + "'" + this.contentId + "'" + ", " + "'" + this.authorId + "'" + ", " + "'" + this.commentGroupId + "'" + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolHeatInfo other = (SchoolHeatInfo) o;
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
