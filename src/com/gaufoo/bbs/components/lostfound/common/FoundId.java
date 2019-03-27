package com.gaufoo.bbs.components.lostfound.common;

public class FoundId {
    public final String value;

    private FoundId(String value) {
        this.value = value;
    }

    public static FoundId of(String value) {
        return new FoundId(value);
    }
}
