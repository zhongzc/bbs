package com.gaufoo.bbs.components.idGenerator;

public interface IdRepository {
    boolean saveLastId(String componentName, int lastId);

    Integer getLastId(String componentName);

    void shutdown();

    static IdRepository fakeIdRepository() {
        return new IdRepository() {
            @Override
            public boolean saveLastId(String componentName, int lastId) {
                return true;
            }

            @Override
            public Integer getLastId(String componentName) {
                return null;
            }

            @Override
            public void shutdown() {

            }
        };
    }
}
