package com.gaufoo.bbs.components.idGenerator;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class SeqInteger implements IdGenerator {
    private final String componentName;
    private final IdRepository repository;
    private final AtomicInteger seq;

    SeqInteger(String componentName, IdRepository repository) {
        this.componentName = componentName;
        this.repository = repository;
        this.seq = Optional.ofNullable(repository.getLastId(componentName))
                .map(AtomicInteger::new).orElse(new AtomicInteger());
    }

    @Override
    public String generateId() {
        int id = seq.getAndIncrement();
        repository.saveLastId(componentName, seq.get());
        return String.format("%08d", id);
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) {
//        IdGenerator g = IdGenerator.seqInteger("");
//        System.out.println(g.generateId());
//        System.out.println(g.generateId());
//        System.out.println(g.generateId());
//        System.out.println(g.generateId());
    }
}
