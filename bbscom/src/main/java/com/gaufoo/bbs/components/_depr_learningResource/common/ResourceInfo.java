package com.gaufoo.bbs.components._depr_learningResource.common;

import java.time.Instant;
import java.util.Objects;

final public class ResourceInfo {
    public final String sharer;
    public final String majorCode;
    public final String title;
    public final String content;
    public final String attachedFileIdentifier;
    public final Instant createTime;

    private ResourceInfo(String sharer, String majorCode, String title, String content, String attachedFileIdentifier, Instant createTime) {
        this.sharer = sharer;
        this.majorCode = majorCode;
        this.title = title;
        this.content = content;
        this.attachedFileIdentifier = attachedFileIdentifier;
        this.createTime = createTime;
    }

    public static ResourceInfo of(String sharer, String majorCode, String title, String content, String attachedFileIdentifier, Instant createTime) {
        return new ResourceInfo(sharer, majorCode, title, content, attachedFileIdentifier, createTime);
    }

    public static ResourceInfo of(String sharer, String majorCode, String title, String content, String attachedFileIdentifier) {
        return new ResourceInfo(sharer, majorCode, title, content, attachedFileIdentifier, Instant.now());
    }

    public ResourceInfo modSharer(String sharer) {
        return new ResourceInfo(sharer, this.majorCode, this.title, this.content, this.attachedFileIdentifier, this.createTime);
    }

    public ResourceInfo modMajorCode(String majorCode) {
        return new ResourceInfo(this.sharer, majorCode, this.title, this.content, this.attachedFileIdentifier, this.createTime);
    }

    public ResourceInfo modTitle(String title) {
        return new ResourceInfo(this.sharer, this.majorCode, title, this.content, this.attachedFileIdentifier, this.createTime);
    }

    public ResourceInfo modContent(String content) {
        return new ResourceInfo(this.sharer, this.majorCode, this.title, content, this.attachedFileIdentifier, this.createTime);
    }

    public ResourceInfo modAttachedFileIdentifier(String attachedFileIdentifier) {
        return new ResourceInfo(this.sharer, this.majorCode, this.title, this.content, attachedFileIdentifier, this.createTime);
    }

    public ResourceInfo modCreateTime(Instant createTime) {
        return new ResourceInfo(this.sharer, this.majorCode, this.title, this.content, this.attachedFileIdentifier, createTime);
    }

    @Override
    public String toString() {
        return "ResourceInfo" + "(" + "'" + this.sharer + "'" + ", " + "'" + this.majorCode + "'" + ", " + "'" + this.title + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.attachedFileIdentifier + "'" + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceInfo other = (ResourceInfo) o;
        return Objects.equals(sharer, other.sharer) &&
                Objects.equals(majorCode, other.majorCode) &&
                Objects.equals(title, other.title) &&
                Objects.equals(content, other.content) &&
                Objects.equals(attachedFileIdentifier, other.attachedFileIdentifier) &&
                Objects.equals(createTime, other.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sharer, majorCode, title, content, attachedFileIdentifier, createTime);
    }
}
