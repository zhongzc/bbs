package com.gaufoo.bbs.components.idGenerator;

public interface IdGenerator {
    String generateId();

    void shutdown();

    String getName();

    static IdGenerator seqInteger(String componentName, IdRepository idRepository) {
        return new SeqInteger(componentName, idRepository);
    }

    static IdGenerator seqInteger(String componentName) {
        return new SeqInteger(componentName, IdRepository.fakeIdRepository());
    }
}
