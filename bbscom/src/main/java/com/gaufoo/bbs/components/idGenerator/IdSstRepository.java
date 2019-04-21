package com.gaufoo.bbs.components.idGenerator;

import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;

import java.nio.file.Path;

public class IdSstRepository implements IdRepository {
    private final SST lastIds;

    private IdSstRepository(Path storingPath) {
        this.lastIds = SST.of("ids", storingPath);
    }

    @Override
    public boolean saveLastId(String componentName, int lastId) {
        return SstUtils.setEntry(lastIds, componentName, String.valueOf(lastId));
    }

    @Override
    public Integer getLastId(String componentName) {
        return SstUtils.getEntry(lastIds, componentName, Integer::parseInt);
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(lastIds.shutdown());
    }

    public static IdRepository get(Path storingPath) {
        return new IdSstRepository(storingPath);
    }
}
