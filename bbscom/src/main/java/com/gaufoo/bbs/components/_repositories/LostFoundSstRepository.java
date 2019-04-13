package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.lostfound.LostFoundRepository;
import com.gaufoo.bbs.components.lostfound.common.FoundId;
import com.gaufoo.bbs.components.lostfound.common.FoundInfo;
import com.gaufoo.bbs.components.lostfound.common.LostId;
import com.gaufoo.bbs.components.lostfound.common.LostInfo;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.stream.Stream;

import static com.gaufoo.bbs.util.SstUtils.*;

public class LostFoundSstRepository implements LostFoundRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private final SST losts;
    private final SST founds;

    private LostFoundSstRepository(String repositoryName, Path storingDir) {
        this.repositoryName = repositoryName;

        String lstIdInfo = "lostId-Info";
        losts = SST.of(lstIdInfo, storingDir.resolve(lstIdInfo));

        String fndIdInfo = "foundId-Info";
        founds = SST.of(fndIdInfo, storingDir.resolve(fndIdInfo));
    }

    @Override
    public boolean saveLost(LostId id, LostInfo info) {
        return setEntry(losts, id.value, gson.toJson(info));
    }

    @Override
    public boolean saveFound(FoundId id, FoundInfo info) {
        return setEntry(founds, id.value, gson.toJson(info));
    }

    @Override
    public boolean updateLost(LostId id, LostInfo info) {
        return setEntry(losts, id.value, gson.toJson(info));
    }

    @Override
    public boolean updateFound(FoundId id, FoundInfo info) {
        return setEntry(founds, id.value, gson.toJson(info));
    }

    @Override
    public LostInfo getLostInfo(LostId id) {
        return getEntry(losts, id.value, lostInfo -> gson.fromJson(lostInfo, LostInfo.class));
    }

    @Override
    public FoundInfo getFoundInfo(FoundId id) {
        return getEntry(founds, id.value, foundInfo -> gson.fromJson(foundInfo, FoundInfo.class));
    }

    @Override
    public Stream<LostId> getAllLosts() {
        return waitFuture(losts.allKeysAsc()
                .thenApply(stringStream -> stringStream
                        .map(LostId::of))
        ).orElse(Stream.empty());
    }

    @Override
    public Stream<FoundId> getAllFounds() {
        return waitFuture(founds.allKeysAsc()
                .thenApply(stringStream -> stringStream
                        .map(FoundId::of))
        ).orElse(Stream.empty());
    }

    @Override
    public void deleteLost(LostId id) {
        removeEntryWithKey(losts, id.value);
    }

    @Override
    public void deleteFound(FoundId id) {
        removeEntryWithKey(founds, id.value);
    }

    @Override
    public void shutdown() {
        waitAllFuturesPar(
                losts.shutdown(),
                founds.shutdown()
        );
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    public static LostFoundSstRepository get(String repositoryName, Path storingPath) {
        return new LostFoundSstRepository(repositoryName, storingPath);
    }
}
