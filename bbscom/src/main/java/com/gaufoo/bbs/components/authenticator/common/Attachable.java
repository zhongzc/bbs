package com.gaufoo.bbs.components.authenticator.common;

import com.gaufoo.bbs.components.authenticator.exceptions.CommonException;

public interface Attachable {
    void attach(Permission permission) throws CommonException;
}
