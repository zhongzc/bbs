package com.gaufoo.bbs.components.news.common;

import java.time.Instant;
import java.util.Objects;

public class NewsInfo {
    public final String title;
    public final String contentId;
    public final String pictureId;
    public final Instant postTime;

    private NewsInfo(String title, String contentId, String pictureId, Instant postTime) {
        this.title = title;
        this.contentId = contentId;
        this.pictureId = pictureId;
        this.postTime = postTime;
    }

    public static NewsInfo of(String title, String contentId, String pictureId, Instant postTime) {
        return new NewsInfo(title, contentId, pictureId, postTime);
    }

    public static NewsInfo of(String title, String contentId, String pictureId) {
        return new NewsInfo(title, contentId, pictureId, Instant.now());
    }

    public NewsInfo modTitle(String title) {
        return new NewsInfo(title, this.contentId, this.pictureId, this.postTime);
    }

    public NewsInfo modContentId(String contentId) {
        return new NewsInfo(this.title, contentId, this.pictureId, this.postTime);
    }

    public NewsInfo modPictureId(String pictureId) {
        return new NewsInfo(this.title, this.contentId, pictureId, this.postTime);
    }

    public NewsInfo modPostTime(Instant postTime) {
        return new NewsInfo(this.title, this.contentId, this.pictureId, postTime);
    }

    @Override
    public String toString() {
        return "NewsInfo" + "(" + "'" + this.title + "'" + ", " + "'" + this.contentId + "'" + ", " + "'" + this.pictureId + "'" + ", " + this.postTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsInfo other = (NewsInfo) o;
        return Objects.equals(title, other.title) &&
                Objects.equals(contentId, other.contentId) &&
                Objects.equals(pictureId, other.pictureId) &&
                Objects.equals(postTime, other.postTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, contentId, pictureId, postTime);
    }
}
