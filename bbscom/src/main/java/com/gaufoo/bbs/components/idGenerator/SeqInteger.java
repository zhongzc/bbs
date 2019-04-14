package com.gaufoo.bbs.components.idGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqInteger implements IdGenerator {
    private final String componentName;
    private final AtomicInteger seq;

    SeqInteger(String componentName) {
        this.componentName = componentName;
        this.seq = new AtomicInteger();
    }

    SeqInteger(String componentName, int from) {
        this.componentName = componentName;
        this.seq = new AtomicInteger(from);
    }

    @Override
    public String generateId() {
        int id = seq.getAndIncrement();
        return String.format("%08d", id);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) {
        IdGenerator g = IdGenerator.seqInteger("");
        System.out.println(g.generateId());
        System.out.println(g.generateId());
        System.out.println(g.generateId());
        System.out.println(g.generateId());
    }
}
