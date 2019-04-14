package com.gaufoo.bbs.components.schoolHeat.common;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

final public class PostInfo {
    public final String title;
    public final String content;
    public final String author;
    public final String latestReplier;
    public final Integer heat;
    public final List<String> replyIdentifiers;
    public final Instant latestActiveTime;
    public final Instant createTime;

    private PostInfo(String title, String content, String author, String latestReplier, Integer heat, List<String> replyIdentifiers, Instant latestActiveTime, Instant createTime) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.latestReplier = latestReplier;
        this.heat = heat;
        this.replyIdentifiers = replyIdentifiers;
        this.latestActiveTime = latestActiveTime;
        this.createTime = createTime;
    }

    public static PostInfo of(String title, String content, String author, String latestReplier, Integer heat, List<String> replyIdentifiers, Instant latestActiveTime, Instant createTime) {
        return new PostInfo(title, content, author, latestReplier, heat, replyIdentifiers, latestActiveTime, createTime);
    }

    public PostInfo modTitle(String title) {
        return new PostInfo(title, this.content, this.author, this.latestReplier, this.heat, this.replyIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modContent(String content) {
        return new PostInfo(this.title, content, this.author, this.latestReplier, this.heat, this.replyIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modAuthor(String author) {
        return new PostInfo(this.title, this.content, author, this.latestReplier, this.heat, this.replyIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modLatestReplier(String latestReplier) {
        return new PostInfo(this.title, this.content, this.author, latestReplier, this.heat, this.replyIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modHeat(Integer heat) {
        return new PostInfo(this.title, this.content, this.author, this.latestReplier, heat, this.replyIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modReplyIdentifiers(List<String> replyIdentifiers) {
        return new PostInfo(this.title, this.content, this.author, this.latestReplier, this.heat, replyIdentifiers, this.latestActiveTime, this.createTime);
    }

    public PostInfo modLatestActiveTime(Instant latestActiveTime) {
        return new PostInfo(this.title, this.content, this.author, this.latestReplier, this.heat, this.replyIdentifiers, latestActiveTime, this.createTime);
    }

    public PostInfo modCreateTime(Instant createTime) {
        return new PostInfo(this.title, this.content, this.author, this.latestReplier, this.heat, this.replyIdentifiers, this.latestActiveTime, createTime);
    }

    @Override
    public String toString() {
        return "PostInfo" + "(" + "'" + this.title + "'" + ", " + "'" + this.content + "'" + ", " + "'" + this.author + "'" + ", " + "'" + this.latestReplier + "'" + ", " + this.heat + ", " + this.replyIdentifiers + ", " + this.latestActiveTime + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostInfo other = (PostInfo) o;
        return Objects.equals(title, other.title) &&
                Objects.equals(content, other.content) &&
                Objects.equals(author, other.author) &&
                Objects.equals(latestReplier, other.latestReplier) &&
                Objects.equals(heat, other.heat) &&
                Objects.equals(replyIdentifiers, other.replyIdentifiers) &&
                Objects.equals(latestActiveTime, other.latestActiveTime) &&
                Objects.equals(createTime, other.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, author, latestReplier, heat, replyIdentifiers, latestActiveTime, createTime);
    }
}
