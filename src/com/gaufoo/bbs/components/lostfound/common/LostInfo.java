package com.gaufoo.bbs.components.lostfound.common;

import java.time.Instant;
import java.util.Objects;

public class LostInfo {
    public final String publisher;
    public final String objName;
    public final Instant lostTime;
    public final String position;
    public final String description;
    public final byte[] image;
    public final String contact;
    public final String claimant;

    private LostInfo(String publisher, String objName, Instant lostTime, String position, String description, byte[] image, String contact, String claimant) {
        this.publisher = publisher;
        this.objName = objName;
        this.lostTime = lostTime;
        this.position = position;
        this.description = description;
        this.image = image;
        this.contact = contact;
        this.claimant = claimant;
    }

    public static LostInfo of(String publisher, String objName, Instant lostTime, String position, String description, byte[] image, String contact, String claimant) {
        return new LostInfo(publisher, objName, lostTime, position, description, image, contact, claimant);
    }

    public static LostInfo of(String publisher, String objName, Instant lostTime, String position, String description, byte[] image, String contact) {
        return new LostInfo(publisher, objName, lostTime, position, description, image, contact, null);
    }

    public LostInfo modPublisher(String publisher) {
        return new LostInfo(publisher, this.objName, this.lostTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInfo modObjName(String objName) {
        return new LostInfo(this.publisher, objName, this.lostTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInfo modLostTime(Instant lostTime) {
        return new LostInfo(this.publisher, this.objName, lostTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInfo modPosition(String position) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInfo modDescription(String description) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, description, this.image, this.contact, this.claimant);
    }

    public LostInfo modImage(byte[] image) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, this.description, image, this.contact, this.claimant);
    }

    public LostInfo modContact(String contact) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, this.description, this.image, contact, this.claimant);
    }

    public LostInfo modClaimant(String claimant) {
        return new LostInfo(this.publisher, this.objName, this.lostTime, this.position, this.description, this.image, this.contact, claimant);
    }

    @Override
    public String toString() {
        return "LostInfo" + "(" + "'" + this.publisher + "'" + ", " + "'" + this.objName + "'" + ", " + this.lostTime + ", " + "'" + this.position + "'" + ", " + "'" + this.description + "'" + ", " + this.image + ", " + "'" + this.contact + "'" + ", " + "'" + this.claimant + "'" + ')';
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
                Objects.equals(image, other.image) &&
                Objects.equals(contact, other.contact) &&
                Objects.equals(claimant, other.claimant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, objName, lostTime, position, description, image, contact, claimant);
    }
}
