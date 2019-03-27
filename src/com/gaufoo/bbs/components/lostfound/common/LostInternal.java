package com.gaufoo.bbs.components.lostfound.common;

import com.gaufoo.bbs.components.fileBuilder.common.FileId;

import java.time.Instant;
import java.util.Objects;

final public class LostInternal {
    public final String publisher;
    public final String objName;
    public final Instant lostTime;
    public final String position;
    public final String description;
    public final FileId image;
    public final String contact;
    public final String claimant;

    private LostInternal(String publisher, String objName, Instant lostTime, String position, String description, FileId image, String contact, String claimant) {
        this.publisher = publisher;
        this.objName = objName;
        this.lostTime = lostTime;
        this.position = position;
        this.description = description;
        this.image = image;
        this.contact = contact;
        this.claimant = claimant;
    }

    public static LostInternal of(String publisher, String objName, Instant lostTime, String position, String description, FileId image, String contact, String claimant) {
        return new LostInternal(publisher, objName, lostTime, position, description, image, contact, claimant);
    }

    public static LostInternal of(String publisher, String objName, Instant lostTime, String position, String description, FileId image, String contact) {
        return new LostInternal(publisher, objName, lostTime, position, description, image, contact, null);
    }

    public LostInternal modPublisher(String publisher) {
        return new LostInternal(publisher, this.objName, this.lostTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInternal modObjName(String objName) {
        return new LostInternal(this.publisher, objName, this.lostTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInternal modLostTime(Instant lostTime) {
        return new LostInternal(this.publisher, this.objName, lostTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInternal modPosition(String position) {
        return new LostInternal(this.publisher, this.objName, this.lostTime, position, this.description, this.image, this.contact, this.claimant);
    }

    public LostInternal modDescription(String description) {
        return new LostInternal(this.publisher, this.objName, this.lostTime, this.position, description, this.image, this.contact, this.claimant);
    }

    public LostInternal modImage(FileId image) {
        return new LostInternal(this.publisher, this.objName, this.lostTime, this.position, this.description, image, this.contact, this.claimant);
    }

    public LostInternal modContact(String contact) {
        return new LostInternal(this.publisher, this.objName, this.lostTime, this.position, this.description, this.image, contact, this.claimant);
    }

    public LostInternal modClaimant(String claimant) {
        return new LostInternal(this.publisher, this.objName, this.lostTime, this.position, this.description, this.image, this.contact, claimant);
    }

    @Override
    public String toString() {
        return "LostInternal" + "('" + this.publisher + "'" + ", " + "'" + this.objName + "'" + ", " + this.lostTime + ", " + "'" + this.position + "'" + ", " + "'" + this.description + "'" + ", " + this.image + ", " + "'" + this.contact + "'" + ", " + "'" + this.claimant + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LostInternal other = (LostInternal) o;
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
