package com.gaufoo.bbs.components.lostfound.common;

import com.gaufoo.bbs.components.fileBuilder.common.FileId;

import java.time.Instant;
import java.util.Objects;

final public class FoundInternal {
    public final String publisher;
    public final String objName;
    public final Instant foundTime;
    public final String position;
    public final String description;
    public final FileId image;
    public final String contact;
    public final String claimant;

    private FoundInternal(String publisher, String objName, Instant foundTime, String position, String description, FileId image, String contact, String claimant) {
        this.publisher = publisher;
        this.objName = objName;
        this.foundTime = foundTime;
        this.position = position;
        this.description = description;
        this.image = image;
        this.contact = contact;
        this.claimant = claimant;
    }

    public static FoundInternal of(String publisher, String objName, Instant foundTime, String position, String description, FileId image, String contact, String claimant) {
        return new FoundInternal(publisher, objName, foundTime, position, description, image, contact, claimant);
    }

    public static FoundInternal of(String publisher, String objName, Instant foundTime, String position, String description, FileId image, String contact) {
        return new FoundInternal(publisher, objName, foundTime, position, description, image, contact, null);
    }

    public FoundInternal modPublisher(String publisher) {
        return new FoundInternal(publisher, this.objName, this.foundTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public FoundInternal modObjName(String objName) {
        return new FoundInternal(this.publisher, objName, this.foundTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public FoundInternal modFoundTime(Instant foundTime) {
        return new FoundInternal(this.publisher, this.objName, foundTime, this.position, this.description, this.image, this.contact, this.claimant);
    }

    public FoundInternal modPosition(String position) {
        return new FoundInternal(this.publisher, this.objName, this.foundTime, position, this.description, this.image, this.contact, this.claimant);
    }

    public FoundInternal modDescription(String description) {
        return new FoundInternal(this.publisher, this.objName, this.foundTime, this.position, description, this.image, this.contact, this.claimant);
    }

    public FoundInternal modImage(FileId image) {
        return new FoundInternal(this.publisher, this.objName, this.foundTime, this.position, this.description, image, this.contact, this.claimant);
    }

    public FoundInternal modContact(String contact) {
        return new FoundInternal(this.publisher, this.objName, this.foundTime, this.position, this.description, this.image, contact, this.claimant);
    }

    public FoundInternal modClaimant(String claimant) {
        return new FoundInternal(this.publisher, this.objName, this.foundTime, this.position, this.description, this.image, this.contact, claimant);
    }

    @Override
    public String toString() {
        return "FoundInternal" + "('" + this.publisher + "'" + ", " + "'" + this.objName + "'" + ", " + this.foundTime + ", " + "'" + this.position + "'" + ", " + "'" + this.description + "'" + ", " + this.image + ", " + "'" + this.contact + "'" + ", " + "'" + this.claimant + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundInternal other = (FoundInternal) o;
        return Objects.equals(publisher, other.publisher) &&
                Objects.equals(objName, other.objName) &&
                Objects.equals(foundTime, other.foundTime) &&
                Objects.equals(position, other.position) &&
                Objects.equals(description, other.description) &&
                Objects.equals(image, other.image) &&
                Objects.equals(contact, other.contact) &&
                Objects.equals(claimant, other.claimant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, objName, foundTime, position, description, image, contact, claimant);
    }
}
