package com.gaufoo.bbs.components.authenticator.common;

import com.gaufoo.bbs.util.TaskChain;

public interface Attachable {
    TaskChain.Procedure<Boolean> attach(Permission permission);
}
