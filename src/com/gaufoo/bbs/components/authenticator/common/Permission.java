package com.gaufoo.bbs.components.authenticator.common;

import com.gaufoo.bbs.components.authenticator.Authenticator;

import java.util.Objects;

public class Permission {
    public final String userId;
    public final Authenticator.Role role;

    private Permission(String userId, Authenticator.Role role) {
        this.userId = userId;
        this.role = role;
    }

    public static Permission of(String userId, Authenticator.Role role) {
        return new Permission(userId, role);
    }

    public Permission modUserId(String userId) {
        return new Permission(userId, this.role);
    }

    public Permission modRole(Authenticator.Role role) {
        return new Permission(this.userId, role);
    }

    @Override
    public String toString() {
        return "Permission" + "(" + "'" + this.userId + "'" + ", " + this.role + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission other = (Permission) o;
        return Objects.equals(userId, other.userId) &&
                Objects.equals(role, other.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, role);
    }
}
