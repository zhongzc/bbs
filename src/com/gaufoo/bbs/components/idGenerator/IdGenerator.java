package com.gaufoo.bbs.components.idGenerator;

public interface IdGenerator {
    String generateId();

    String getName();

    static IdGenerator seqInteger(String componentName) {
        return new SeqInteger(componentName);
    }
}
