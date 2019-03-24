package com.gaufoo.bbs.components.tokenGenerator;

import java.time.Instant;

public interface TokenGenerator {
    String genToken(String key, Instant expireTime);

    boolean isExpired(String token);

    void expire(String token);

    static TokenGenerator defau1t(TokenGeneratorRepository repository) {
        return new TokenGeneratorImpl(repository);
    }
}
