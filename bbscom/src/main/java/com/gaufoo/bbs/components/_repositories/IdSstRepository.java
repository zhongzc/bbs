package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.idGenerator.IdRepository;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;

import java.nio.file.Path;

public class IdSstRepository implements IdRepository {
    private final String repositoryName;
    private final SST lastIds;

    private IdSstRepository(String repositoryName, Path storingPath) {
        this.repositoryName = repositoryName;
        this.lastIds = SST.of(repositoryName, storingPath.resolve(repositoryName));
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

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static IdRepository get(String repositoryName, Path storingPath) {
        return new IdSstRepository(repositoryName, storingPath);
    }
}
