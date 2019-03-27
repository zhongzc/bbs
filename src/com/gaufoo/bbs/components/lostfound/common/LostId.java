package com.gaufoo.bbs.components.lostfound.common;

final public class LostId {
    public final String value;

    private LostId(String value) {
        this.value = value;
    }

    public static LostId of(String value) {
        return new LostId(value);
    }
}
