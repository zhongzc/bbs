package com.gaufoo.bbs.components.lostfound.common;

import java.time.Instant;
import java.util.Objects;

final public class LostInfo {
    public final String publisher;
    public final String objName;
    public final Instant lostTime;
    public final String position;
    public final String description;
    public final String imageIdentifier;
    public final String contact;
    public final String claimant;
    public final Instant createTime;

    private LostInfo(String publisher, String objName, Instant lostTime, String position, String description, String imageIdentifier, String contact, String claimant, Instant createTime) {
        this.publisher = publisher;
        this.objName = objName;
        this.lostTime = lostTime;
        this.position = position;
        this.description = description;
        this.imageIdentifier = imageIdentifier;
        this.contact = contact;
        this.claimant = claimant;
        this.createTime = createTime;
    }

    public static LostInfo of(String publisher, String objName, Instant lostTime, String position, String description, String imageIdentifier, String contact, String claimant, Instant createTime) {
        return new LostInfo(publisher, objName, lostTime, position, description, imageIdentifier, contact, claimant, createTime);
    }

    public static LostInfo of(String publisher, String objName, Instant lostTime, String position, String description, String imageIdentifier, String contact) {
        return new LostInfo(publisher, objName, lostTime, position, description, imageIdentifier, contact, null, Instant.now());
    }

    public LostInfo modPublisher(String publisher) {
        return new LostInfo(publisher, this.objName, this.lostTime, this.position, this.description, this.imageIdentifier, this.contact, this.claimant, this.createTime);
    }

    public LostInfo modObjName(String objName) {
        return new LostInfo(this.publisher, objName, this.lostTime, this.position, this.description, this.imageIdentifier, this.contact, this.claimant, this.createTime);
    }

    public LostInfo modLostTime(Instant lostTime) {
        return new LostInfo(this.publisher, this.objName, lostTime, this.position, this.description, this.imageIdentifier, this.contact, this.claimant, this.createTime);
    }

    public LostInfo modPosition(String position) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, position, this.description, this.imageIdentifier, this.contact, this.claimant, this.createTime);
    }

    public LostInfo modDescription(String description) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, description, this.imageIdentifier, this.contact, this.claimant, this.createTime);
    }

    public LostInfo modImageIdentifier(String imageIdentifier) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, this.description, imageIdentifier, this.contact, this.claimant, this.createTime);
    }

    public LostInfo modContact(String contact) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, this.description, this.imageIdentifier, contact, this.claimant, this.createTime);
    }

    public LostInfo modClaimant(String claimant) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, this.description, this.imageIdentifier, this.contact, claimant, this.createTime);
    }

    public LostInfo modeCreateTime(Instant createTime) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, this.description, this.imageIdentifier, this.contact, this.claimant, createTime);

    }

    @Override
    public String toString() {
        return "LostInfo" + "(" + "'" + this.publisher + "'" + ", " + "'" + this.objName + "'" + ", " + this.lostTime + ", " + "'" + this.position + "'" + ", " + "'" + this.description + "'" + ", " + "'" + this.imageIdentifier + "'" + ", " + "'" + this.contact + "'" + ", " + this.createTime + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LostInfo other = (LostInfo) o;
        return Objects.equals(publisher, other.publisher) &&
                Objects.equals(objName, other.objName) &&
                Objects.equals(lostTime, other.lostTime) &&
                Objects.equals(position, other.position) &&
                Objects.equals(description, other.description) &&
                Objects.equals(imageIdentifier, other.imageIdentifier) &&
                Objects.equals(contact, other.contact) &&
                Objects.equals(claimant, other.claimant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, objName, lostTime, position, description, imageIdentifier, contact, claimant);
    }
}
