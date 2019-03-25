package com.gaufoo.bbs.components.lostfound.common;

import java.time.Instant;
import java.util.Optional;

public class LostInfo {
    public final String publisher;
    public final String objName;
    public final Instant lostTime;
    public final String position;
    public final String description;
    public final String imageURI;
    public final String contact;
    private String claimant = null;

    public LostInfo(String publisher, String objName, Instant lostTime,
                    String position, String description, String imageURI,
                    String contact) {
        this.publisher = publisher;
        this.objName = objName;
        this.lostTime = lostTime;
        this.position = position;
        this.description = description;
        this.imageURI = imageURI;
        this.contact = contact;
    }

    public boolean claim(String c) {
        if (claimant != null) return false;

        claimant = c;
        return true;
    }

    public Optional<String> getClaimant() {
        return Optional.ofNullable(claimant);
    }
}
