package com.gaufoo.bbs.components.tokenGenerator;

import java.time.Instant;

public interface TokenGeneratorRepository {
    void saveToken(String token, Instant expireTime);

    Instant getExpireTime(String token);

    void delete(String token);
}

