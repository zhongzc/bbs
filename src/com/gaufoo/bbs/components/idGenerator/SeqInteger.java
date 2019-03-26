package com.gaufoo.bbs.components.idGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqInteger implements IdGenerator {
    private final AtomicInteger seq = new AtomicInteger();

    SeqInteger() {}

    @Override
    public String generateId() {
        int id = seq.getAndIncrement();
        return String.format("%08d", id);
    }
}
