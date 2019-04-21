package com.gaufoo.bbs.components.learningResource.common;

import java.time.Instant;
import java.util.Objects;

public class LearningResourceInfo {
    public final String authorId;
    public final String contentId;
    public final String courseCode;
    public final String attachedFileId;
    public final String commentGroupId;
    public final Instant createTime;

    private LearningResourceInfo(String authorId, String contentId, String courseCode, String attachedFileId, String commentGroupId, Instant createTime) {
        this.authorId = authorId;
        this.contentId = contentId;
        this.courseCode = courseCode;
        this.attachedFileId = attachedFileId;
        this.commentGroupId = commentGroupId;
        this.createTime = createTime;
    }

    public static LearningResourceInfo of(String authorId, String contentId, String courseCode, String attachedFileId, String commentGroupId, Instant createTime) {
        return new LearningResourceInfo(authorId, contentId, courseCode, attachedFileId, commentGroupId, createTime);
    }

    public static LearningResourceInfo of(String authorId, String contentId, String courseCode, String attachedFileId, String commentGroupId) {
        return new LearningResourceInfo(authorId, contentId, courseCode, attachedFileId, commentGroupId, Instant.now());
    }

    public LearningResourceInfo modAuthorId(String authorId) {
        return new LearningResourceInfo(authorId, this.contentId, this.courseCode, this.attachedFileId, this.commentGroupId, this.createTime);
    }

    public LearningResourceInfo modContentId(String contentId) {
        return new LearningResourceInfo(this.authorId, contentId, this.courseCode, this.attachedFileId, this.commentGroupId, this.createTime);
    }

    public LearningResourceInfo modCourseCode(String courseCode) {
        return new LearningResourceInfo(this.authorId, this.contentId, courseCode, this.attachedFileId, this.commentGroupId, this.createTime);
    }

    public LearningResourceInfo modAttachedFileId(String attachedFileId) {
        return new LearningResourceInfo(this.authorId, this.contentId, this.courseCode, attachedFileId, this.commentGroupId, this.createTime);
    }

    public LearningResourceInfo modCommentGroupId(String commentGroupId) {
        return new LearningResourceInfo(this.authorId, this.contentId, this.courseCode, this.attachedFileId, commentGroupId, this.createTime);
    }

    public LearningResourceInfo modCreateTime(Instant createTime) {
        return new LearningResourceInfo(this.authorId, this.contentId, this.courseCode, this.attachedFileId, this.commentGroupId, createTime);
    }

    @Override
    public String toString() {
        return "LearningResourceInfo" + "(" + "'" + this.authorId + "'" + ", " + "'" + this.contentId + "'" + ", " + "'" + this.courseCode + "'" + ", " + "'" + this.attachedFileId + "'" + ", " + "'" + this.commentGroupId + "'" + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningResourceInfo other = (LearningResourceInfo) o;
        return Objects.equals(authorId, other.authorId) &&
                Objects.equals(contentId, other.contentId) &&
                Objects.equals(courseCode, other.courseCode) &&
                Objects.equals(attachedFileId, other.attachedFileId) &&
                Objects.equals(commentGroupId, other.commentGroupId) &&
                Objects.equals(createTime, other.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorId, contentId, courseCode, attachedFileId, commentGroupId, createTime);
    }
}
