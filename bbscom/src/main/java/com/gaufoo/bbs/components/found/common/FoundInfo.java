package com.gaufoo.bbs.components.found.common;

import java.time.Instant;
import java.util.Objects;

public class FoundInfo {
    public final String name;
    public final String publisherId;
    public final String description;
    public final String position;
    public final String pictureId;
    public final String contact;
    public final Instant foundTime;
    public final Instant createTime;
    public final String losterId;

    private FoundInfo(String name, String publisherId, String description, String position, String pictureId, String contact, Instant foundTime, Instant createTime, String losterId) {
        this.name = name;
        this.publisherId = publisherId;
        this.description = description;
        this.position = position;
        this.pictureId = pictureId;
        this.contact = contact;
        this.foundTime = foundTime;
        this.createTime = createTime;
        this.losterId = losterId;
    }

    public static FoundInfo of(String name, String publisherId, String description, String position, String pictureId, String contact, Instant foundTime, Instant createTime, String losterId) {
        return new FoundInfo(name, publisherId, description, position, pictureId, contact, foundTime, createTime, losterId);
    }

    public static FoundInfo of(String name, String publisherId, String description, String position, String pictureId, String contact, Instant foundTime) {
        return new FoundInfo(name, publisherId, description, position, pictureId, contact, foundTime, Instant.now(), null);
    }

    public FoundInfo modName(String name) {
        return new FoundInfo(name, this.publisherId, this.description, this.position, this.pictureId, this.contact, this.foundTime, this.createTime, this.losterId);
    }

    public FoundInfo modPublisherId(String publisherId) {
        return new FoundInfo(this.name, publisherId, this.description, this.position, this.pictureId, this.contact, this.foundTime, this.createTime, this.losterId);
    }

    public FoundInfo modDescription(String description) {
        return new FoundInfo(this.name, this.publisherId, description, this.position, this.pictureId, this.contact, this.foundTime, this.createTime, this.losterId);
    }

    public FoundInfo modPosition(String position) {
        return new FoundInfo(this.name, this.publisherId, this.description, position, this.pictureId, this.contact, this.foundTime, this.createTime, this.losterId);
    }

    public FoundInfo modPictureId(String pictureId) {
        return new FoundInfo(this.name, this.publisherId, this.description, this.position, pictureId, this.contact, this.foundTime, this.createTime, this.losterId);
    }

    public FoundInfo modContact(String contact) {
        return new FoundInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, contact, this.foundTime, this.createTime, this.losterId);
    }

    public FoundInfo modFoundTime(Instant foundTime) {
        return new FoundInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, this.contact, foundTime, this.createTime, this.losterId);
    }

    public FoundInfo modCreateTime(Instant createTime) {
        return new FoundInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, this.contact, this.foundTime, createTime, this.losterId);
    }

    public FoundInfo modLosterId(String losterId) {
        return new FoundInfo(this.name, this.publisherId, this.description, this.position, this.pictureId, this.contact, this.foundTime, this.createTime, losterId);
    }

    @Override
    public String toString() {
        return "FoundInfo" + "(" + "'" + this.name + "'" + ", " + "'" + this.publisherId + "'" + ", " + "'" + this.description + "'" + ", " + "'" + this.position + "'" + ", " + "'" + this.pictureId + "'" + ", " + "'" + this.contact + "'" + ", " + this.foundTime + ", " + this.createTime + ", " + "'" + this.losterId + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundInfo other = (FoundInfo) o;
        return Objects.equals(name, other.name) &&
                Objects.equals(publisherId, other.publisherId) &&
                Objects.equals(description, other.description) &&
                Objects.equals(position, other.position) &&
                Objects.equals(pictureId, other.pictureId) &&
                Objects.equals(contact, other.contact) &&
                Objects.equals(foundTime, other.foundTime) &&
                Objects.equals(createTime, other.createTime) &&
                Objects.equals(losterId, other.losterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, publisherId, description, position, pictureId, contact, foundTime, createTime, losterId);
    }
}
