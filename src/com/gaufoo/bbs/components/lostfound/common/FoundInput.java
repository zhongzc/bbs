package com.gaufoo.bbs.components.lostfound.common;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

final public class FoundInput {
    public final String publisher;
    public final String objName;
    public final Instant foundTime;
    public final String position;
    public final String description;
    public final byte[] image;
    public final String contact;

    private FoundInput(String publisher, String objName, Instant foundTime, String position, String description, byte[] image, String contact) {
        this.publisher = publisher;
        this.objName = objName;
        this.foundTime = foundTime;
        this.position = position;
        this.description = description;
        this.image = image;
        this.contact = contact;
    }

    public static FoundInput of(String publisher, String objName, Instant foundTime, String position, String description, byte[] image, String contact) {
        return new FoundInput(publisher, objName, foundTime, position, description, image, contact);
    }

    public FoundInput modPublisher(String publisher) {
        return new FoundInput(publisher, this.objName, this.foundTime, this.position, this.description, this.image, this.contact);
    }

    public FoundInput modObjName(String objName) {
        return new FoundInput(this.publisher, objName, this.foundTime, this.position, this.description, this.image, this.contact);
    }

    public FoundInput modFoundTime(Instant foundTime) {
        return new FoundInput(this.publisher, this.objName, foundTime, this.position, this.description, this.image, this.contact);
    }

    public FoundInput modPosition(String position) {
        return new FoundInput(this.publisher, this.objName, this.foundTime, position, this.description, this.image, this.contact);
    }

    public FoundInput modDescription(String description) {
        return new FoundInput(this.publisher, this.objName, this.foundTime, this.position, description, this.image, this.contact);
    }

    public FoundInput modImage(byte[] image) {
        return new FoundInput(this.publisher, this.objName, this.foundTime, this.position, this.description, image, this.contact);
    }

    public FoundInput modContact(String contact) {
        return new FoundInput(this.publisher, this.objName, this.foundTime, this.position, this.description, this.image, contact);
    }

    @Override
    public String toString() {
        return "FoundInput" + "(" + "'" + this.publisher + "'" + ", " + "'" + this.objName + "'" + ", " + this.foundTime + ", " + "'" + this.position + "'" + ", " + "'" + this.description + "'" + ", " + this.image + ", " + "'" + this.contact + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundInput other = (FoundInput) o;
        return Objects.equals(publisher, other.publisher) &&
                Objects.equals(objName, other.objName) &&
                Objects.equals(foundTime, other.foundTime) &&
                Objects.equals(position, other.position) &&
                Objects.equals(description, other.description) &&
                Arrays.equals(image, other.image) &&
                Objects.equals(contact, other.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, objName, foundTime, position, description, image, contact);
    }
}
