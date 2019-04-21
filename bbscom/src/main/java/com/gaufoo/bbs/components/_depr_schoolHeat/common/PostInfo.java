package com.gaufoo.bbs.components._depr_schoolHeat.common;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final public class PostInfo {
    public final String title;
    public final String content;
    public final String author;
    public final String latestCommenter;
    public final Integer heat;
    public final Long commentCount;
    public final List<String> commentIdentifiers;
    public final Instant latestActiveTime;
    public final Instant createTime;

    private PostInfo(String title, String content, String author, String latestCommenter, Integer heat, Long commentCount, List<String> commentIdentifiers, Instant latestActiveTime, Instant createTime) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.latestCommenter = latestCommenter;
        this.heat = heat;
        this.commentCount = commentCount;
        this.commentIdentifiers = commentIdentifiers;
        this.latestActiveTime = latestActiveTime;
        this.createTime = createTime;
    }

    public static PostInfo of(String title, String content, String author, String latestCommenter, Integer heat, Long commentCount, List<String> commentIdentifiers, Instant latestActiveTime, Instant createTime) {
        return new PostInfo(title, content, author, latestCommenter, heat, commentCount, commentIdentifiers, latestActiveTime, createTime);
    }

    public static PostInfo of(String title, String content, String author) {
        return new PostInfo(title, content, author, null, 0, 0L, new ArrayList<>(), Instant.now(), Instant.now());
    }

    public PostInfo modTitle(String title) {
        return new PostInfo(title, this.content, this.author, this.latestCommenter, this.heat, this.commentCount, this.commentIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modContent(String content) {
        return new PostInfo(this.title, content, this.author, this.latestCommenter, this.heat, this.commentCount, this.commentIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modAuthor(String author) {
        return new PostInfo(this.title, this.content, author, this.latestCommenter, this.heat, this.commentCount, this.commentIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modLatestCommenter(String latestCommenter) {
        return new PostInfo(this.title, this.content, this.author, latestCommenter, this.heat, this.commentCount, this.commentIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modHeat(Integer heat) {
        return new PostInfo(this.title, this.content, this.author, this.latestCommenter, heat, this.commentCount, this.commentIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modCommentCount(Long commentCount) {
        return new PostInfo(this.title, this.content, this.author, this.latestCommenter, this.heat, commentCount, this.commentIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modCommentIdentifiers(List<String> commentIdentifiers) {
        return new PostInfo(this.title, this.content, this.author, this.latestCommenter, this.heat, this.commentCount, commentIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modLatestActiveTime(Instant latestActiveTime) {
        return new PostInfo(this.title, this.content, this.author, this.latestCommenter, this.heat, this.commentCount, this.commentIdentifiers, latestActiveTime, this.createTime);
    }

    public PostInfo modCreateTime(Instant createTime) {
        return new PostInfo(this.title, this.content, this.author, this.latestCommenter, this.heat, this.commentCount, this.commentIdentifiers, this.latestActiveTime, createTime);
    }

    @Override
    public String toString() {
        return "PostInfo" + "(" + "'" + this.title + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.author + "'" + ", " + "'" + this.latestCommenter + "'" + ", " + this.heat + ", " + this.commentCount + ", " + this.commentIdentifiers + ", " + this.latestActiveTime + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostInfo other = (PostInfo) o;
        return Objects.equals(title, other.title) &&
                Objects.equals(content, other.content) &&
                Objects.equals(author, other.author) &&
                Objects.equals(latestCommenter, other.latestCommenter) &&
                Objects.equals(heat, other.heat) &&
                Objects.equals(commentCount, other.commentCount) &&
                Objects.equals(commentIdentifiers, other.commentIdentifiers) &&
                Objects.equals(latestActiveTime, other.latestActiveTime) &&
                Objects.equals(createTime, other.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, author, latestCommenter, heat, commentCount, commentIdentifiers, latestActiveTime, createTime);
    }
}
