package com.gaufoo.bbs.components.lostfound.common;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

final public class LostInput {
    public final String publisher;
    public final String objName;
    public final Instant lostTime;
    public final String position;
    public final String description;
    public final byte[] image;
    public final String contact;

    private LostInput(String publisher, String objName, Instant lostTime, String position, String description, byte[] image, String contact) {
        this.publisher = publisher;
        this.objName = objName;
        this.lostTime = lostTime;
        this.position = position;
        this.description = description;
        this.image = image;
        this.contact = contact;
    }

    public static LostInput of(String publisher, String objName, Instant lostTime, String position, String description, byte[] image, String contact) {
        return new LostInput(publisher, objName, lostTime, position, description, image, contact);
    }

    public LostInput modPublisher(String publisher) {
        return new LostInput(publisher, this.objName, this.lostTime, this.position, this.description, this.image, this.contact);
    }

    public LostInput modObjName(String objName) {
        return new LostInput(this.publisher, objName, this.lostTime, this.position, this.description, this.image, this.contact);
    }

    public LostInput modLostTime(Instant lostTime) {
        return new LostInput(this.publisher, this.objName, lostTime, this.position, this.description, this.image, this.contact);
    }

    public LostInput modPosition(String position) {
        return new LostInput(this.publisher, this.objName, this.lostTime, position, this.description, this.image, this.contact);
    }

    public LostInput modDescription(String description) {
        return new LostInput(this.publisher, this.objName, this.lostTime, this.position, description, this.image, this.contact);
    }

    public LostInput modImage(byte[] image) {
        return new LostInput(this.publisher, this.objName, this.lostTime, this.position, this.description, image, this.contact);
    }

    public LostInput modContact(String contact) {
        return new LostInput(this.publisher, this.objName, this.lostTime, this.position, this.description, this.image, contact);
    }

    @Override
    public String toString() {
        return "LostInput" + "(" + "'" + this.publisher + "'" + ", " + "'" + this.objName + "'" + ", " + this.lostTime + ", " + "'" + this.position + "'" + ", " + "'" + this.description + "'" + ", " + this.image + ", " + "'" + this.contact + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LostInput other = (LostInput) o;
        return Objects.equals(publisher, other.publisher) &&
                Objects.equals(objName, other.objName) &&
                Objects.equals(lostTime, other.lostTime) &&
                Objects.equals(position, other.position) &&
                Objects.equals(description, other.description) &&
                Arrays.equals(image, other.image) &&
                Objects.equals(contact, other.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, objName, lostTime, position, description, image, contact);
    }
}
