package com.gaufoo.bbs.util;

import java.util.Objects;

public class Tuple<Left, Right> {
    public final Left left;
    public final Right right;
    private Tuple(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    public static <Left, Right> Tuple<Left, Right> of(Left left, Right right) {
        return new Tuple<>(left, right);
    }

    public Tuple<Left, Right> modLeft(Left left) {
        return new Tuple<>(left, this.right);
    }

    public Tuple<Left, Right> modRight(Right right) {
        return new Tuple<>(this.left, right);
    }

    @Override
    public String toString() {
        return "(" + left + "," + right + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(left, tuple.left) &&
                Objects.equals(right, tuple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}