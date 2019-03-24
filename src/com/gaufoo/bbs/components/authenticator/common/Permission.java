package com.gaufoo.bbs.components.authenticator.common;

import com.gaufoo.bbs.components.authenticator.Authenticator;

public class Permission {
    private final String userId;
    private final Authenticator.Role role;

    public Permission(String userId, Authenticator.Role role) {
        this.userId = userId;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public Authenticator.Role getRole() {
        return role;
    }
}
