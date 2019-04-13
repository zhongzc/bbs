package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.tokenGenerator.TokenGeneratorRepository;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.time.Instant;

import static com.gaufoo.bbs.util.SstUtils.*;

public class TokenGeneratorSstRepository implements TokenGeneratorRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private final SST tknToTime;

    private TokenGeneratorSstRepository(String repositoryName, Path storingDir) {
        this.repositoryName = repositoryName;

        String tknTime = "token-time";
        tknToTime = SST.of(tknTime, storingDir.resolve(tknTime));
    }


    @Override
    public void saveToken(String token, Instant expireTime) {
        setEntry(tknToTime, token, gson.toJson(expireTime));
    }

    @Override
    public Instant getExpireTime(String token) {
        return getEntry(tknToTime, token,
                instant -> gson.fromJson(instant, Instant.class));
    }

    @Override
    public void delete(String token) {
        removeEntryWithKey(tknToTime, token);
    }

    @Override
    public void shutdown() {
        waitAllFuturesPar(tknToTime.shutdown());
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    public static TokenGeneratorSstRepository get(String repositoryName, Path storingDir) {
        return new TokenGeneratorSstRepository(repositoryName, storingDir);
    }
}
