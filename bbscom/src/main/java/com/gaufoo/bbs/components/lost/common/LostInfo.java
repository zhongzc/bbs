package com.gaufoo.bbs.components.lost.common;

import java.time.Instant;
import java.util.Objects;

public class LostInfo {
    public final String name;
    public final String publisherId;
    public final String description;
    public final String position;
    public final String pictureId;
    public final String contact;
    public final Instant lostTime;
    public final Instant createTime;
    public final String founderId;

    private LostInfo(String name, String publisherId, String description, String position, String pictureId, String contact, Instant lostTime, Instant createTime, String founderId) {
        this.name = name;
        this.publisherId = publisherId;
        this.description = description;
        this.position = position;
        this.pictureId = pictureId;
        this.contact = contact;
        this.lostTime = lostTime;
        this.createTime = createTime;
        this.founderId = founderId;
    }

    public static LostInfo of(String name, String publisherId, String description, String position, String pictureId, String contact, Instant lostTime, Instant createTime, String founderId) {
        return new LostInfo(name, publisherId, description, position, pictureId, contact, lostTime, createTime, founderId);
    }

    public static LostInfo of(String name, String publisherId, String description, String position, String pictureId, String contact, Instant lostTime) {
        return new LostInfo(name, publisherId, description, position, pictureId, contact, lostTime, Instant.now(), null);
    }

    public LostInfo modName(String name) {
        return new LostInfo(name, this.publisherId, this.description, this.position, this.pictureId, this.contact, this.lostTime, this.createTime, this.founderId);
    }

    public LostInfo modPublisherId(String publisherId) {
        return new LostInfo(this.name, publisherId, this.description, this.position, this.pictureId, this.contact, this.lostTime, this.createTime, this.founderId);
    }

    public LostInfo modDescription(String description) {
        return new LostInfo(this.name, this.publisherId, description, this.position, this.pictureId, this.contact, this.lostTime, this.createTime, this.founderId);
    }

    public LostInfo modPosition(String position) {
        return new LostInfo(this.name, this.publisherId, this.description, position, this.pictureId, this.contact, this.lostTime, this.createTime, this.founderId);
    }

    public LostInfo modPictureId(String pictureId) {
        return new LostInfo(this.name, this.publisherId, this.description, this.position, pictureId, this.contact, this.lostTime, this.createTime, this.founderId);
    }

    public LostInfo modContact(String contact) {
        return new LostInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, contact, this.lostTime, this.createTime, this.founderId);
    }

    public LostInfo modLostTime(Instant lostTime) {
        return new LostInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, this.contact, lostTime, this.createTime, this.founderId);
    }

    public LostInfo modCreateTime(Instant createTime) {
        return new LostInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, this.contact, this.lostTime, createTime, this.founderId);
    }

    public LostInfo modFounderId(String founderId) {
        return new LostInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, this.contact, this.lostTime, this.createTime, founderId);
    }

    @Override
    public String toString() {
        return "LostInfo" + "(" + "'" + this.name + "'" + ", " + "'" + this.publisherId + "'" + ", " + "'" + this.description + "'" + ", " + "'" + this.position + "'" + ", " + "'" + this.pictureId + "'" + ", " + "'" + this.contact + "'" + ", " + this.lostTime + ", " + this.createTime + ", " + "'" + this.founderId + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LostInfo other = (LostInfo) o;
        return Objects.equals(name, other.name) &&
                Objects.equals(publisherId, other.publisherId) &&
                Objects.equals(description, other.description) &&
                Objects.equals(position, other.position) &&
                Objects.equals(pictureId, other.pictureId) &&
                Objects.equals(contact, other.contact) &&
                Objects.equals(lostTime, other.lostTime) &&
                Objects.equals(createTime, other.createTime) &&
                Objects.equals(founderId, other.founderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, publisherId, description, position, pictureId, contact, lostTime, createTime, founderId);
    }
}
