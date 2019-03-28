package com.gaufoo.bbs.components.like.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LikeInfo {
    public final String obj;
    public final List<String> liker;
    public final List<String> disliker;

    private LikeInfo(String obj, List<String> liker, List<String> disliker) {
        this.obj = obj;
        this.liker = liker;
        this.disliker = disliker;
    }

    public static LikeInfo of(String obj, List<String> liker, List<String> disliker) {
        return new LikeInfo(obj, liker, disliker);
    }

    public static LikeInfo of(String obj) {
        return new LikeInfo(obj, new ArrayList<>(), new ArrayList<>());
    }

    public LikeInfo modObjName(String obj) {
        return new LikeInfo(obj, this.liker, this.disliker);
    }

    public LikeInfo modLiker(List<String> liker) {
        return new LikeInfo(this.obj, liker, this.disliker);
    }

    public LikeInfo modDisliker(List<String> disliker) {
        return new LikeInfo(this.obj, this.liker, disliker);
    }

    @Override
    public String toString() {
        return "LikeInfo" + "(" + "'" + this.obj + "'" + ", " + this.liker + ", " + this.disliker + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeInfo other = (LikeInfo) o;
        return Objects.equals(obj, other.obj) &&
                Objects.equals(liker, other.liker) &&
                Objects.equals(disliker, other.disliker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj, liker, disliker);
    }
}
