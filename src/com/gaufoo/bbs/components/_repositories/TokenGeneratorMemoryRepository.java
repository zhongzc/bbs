package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.tokenGenerator.TokenGeneratorRepository;

import java.time.Instant;
import java.util.Hashtable;
import java.util.Map;

public class TokenGeneratorMemoryRepository implements TokenGeneratorRepository {
    private final Map<String, Instant> map = new Hashtable<>();

    private TokenGeneratorMemoryRepository() {}

    @Override
    public void saveToken(String token, Instant expireTime) {
        if (token != null) map.put(token, expireTime);
    }

    @Override
    public Instant getExpireTime(String token) {
        if (token == null) return null;
        else return map.get(token);
    }

    @Override
    public void delete(String token) {
        if (token != null) map.remove(token);
    }

    private static TokenGeneratorMemoryRepository instance = new TokenGeneratorMemoryRepository();

    public static TokenGeneratorRepository get() {
        return instance;
    }
}
