package com.gaufoo.bbs.components.authenticator.common;

import java.util.Objects;

final public class UserToken {
    public final String value;

    private UserToken(String value) {
        this.value = value;
    }

    public static UserToken of(String value) {
        return new UserToken(value);
    }

    public UserToken modValue(String value) {
        return new UserToken(value);
    }

    @Override
    public String toString() {
        return "UserToken" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToken other = (UserToken) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
