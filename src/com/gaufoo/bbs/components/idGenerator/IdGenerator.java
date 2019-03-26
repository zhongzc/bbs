package com.gaufoo.bbs.components.idGenerator;

public interface IdGenerator {
    String generateId();

    static IdGenerator seqInteger() {
        return new SeqInteger();
    }

    public static void main(String[] args) {
        IdGenerator g = seqInteger();
        System.out.println(g.generateId());
        System.out.println(g.generateId());
        System.out.println(g.generateId());
        System.out.println(g.generateId());
    }
}
