package com.gaufoo.bbs.components.lostfound.common;

import java.time.Instant;
import java.util.Objects;

final public class FoundInfo {
    public final String publisher;
    public final String objName;
    public final Instant foundTime;
    public final String position;
    public final String description;
    public final String imageIdentifier;
    public final String contact;
    public final String claimant;

    private FoundInfo(String publisher, String objName, Instant foundTime, String position, String description, String imageIdentifier, String contact, String claimant) {
        this.publisher = publisher;
        this.objName = objName;
        this.foundTime = foundTime;
        this.position = position;
        this.description = description;
        this.imageIdentifier = imageIdentifier;
        this.contact = contact;
        this.claimant = claimant;
    }

    public static FoundInfo of(String publisher, String objName, Instant foundTime, String position, String description, String imageURI, String contact, String claimant) {
        return new FoundInfo(publisher, objName, foundTime, position, description, imageURI, contact, claimant);
    }

    public FoundInfo modPublisher(String publisher) {
        return new FoundInfo(publisher, this.objName, this.foundTime, this.position, this.description, this.imageIdentifier, this.contact, this.claimant);
    }

    public FoundInfo modObjName(String objName) {
        return new FoundInfo(this.publisher, objName, this.foundTime, this.position, this.description, this.imageIdentifier, this.contact, this.claimant);
    }

    public FoundInfo modFoundTime(Instant foundTime) {
        return new FoundInfo(this.publisher, this.objName, foundTime, this.position, this.description, this.imageIdentifier, this.contact, this.claimant);
    }

    public FoundInfo modPosition(String position) {
        return new FoundInfo(this.publisher, this.objName, this.foundTime, position, this.description, this.imageIdentifier, this.contact, this.claimant);
    }

    public FoundInfo modDescription(String description) {
        return new FoundInfo(this.publisher, this.objName, this.foundTime, this.position, description, this.imageIdentifier, this.contact, this.claimant);
    }

    public FoundInfo modImageIdentifier(String imageIdentifier) {
        return new FoundInfo(this.publisher, this.objName, this.foundTime, this.position, this.description, imageIdentifier, this.contact, this.claimant);
    }

    public FoundInfo modContact(String contact) {
        return new FoundInfo(this.publisher, this.objName, this.foundTime, this.position, this.description, this.imageIdentifier, contact, this.claimant);
    }

    public FoundInfo modClaimant(String claimant) {
        return new FoundInfo(this.publisher, this.objName, this.foundTime, this.position, this.description, this.imageIdentifier, this.contact, claimant);
    }

    @Override
    public String toString() {
        return "FoundInfo" + "(" + "'" + this.publisher + "'" + ", " + "'" + this.objName + "'" + ", " + this.foundTime + ", " + "'" + this.position + "'" + ", " + "'" + this.description + "'" + ", " + "'" + this.imageIdentifier + "'" + ", " + "'" + this.contact + "'" + ", " + "'" + this.claimant + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundInfo other = (FoundInfo) o;
        return Objects.equals(publisher, other.publisher) &&
                Objects.equals(objName, other.objName) &&
                Objects.equals(foundTime, other.foundTime) &&
                Objects.equals(position, other.position) &&
                Objects.equals(description, other.description) &&
                Objects.equals(imageIdentifier, other.imageIdentifier) &&
                Objects.equals(contact, other.contact) &&
                Objects.equals(claimant, other.claimant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, objName, foundTime, position, description, imageIdentifier, contact, claimant);
    }

}
