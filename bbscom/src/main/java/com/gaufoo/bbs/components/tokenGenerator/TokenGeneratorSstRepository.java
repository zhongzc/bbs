package com.gaufoo.bbs.components.tokenGenerator;

import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.time.Instant;

import static com.gaufoo.bbs.util.SstUtils.*;

public class TokenGeneratorSstRepository implements TokenGeneratorRepository {
    private static final Gson gson = new Gson();
    private final SST tknToTime;

    private TokenGeneratorSstRepository(Path storingDir) {
        tknToTime = SST.of("token", storingDir);
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

    public static TokenGeneratorSstRepository get(Path storingDir) {
        return new TokenGeneratorSstRepository(storingDir);
    }
}
