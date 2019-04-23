package com.gaufoo.bbs.components.lost;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.lost.common.LostId;
import com.gaufoo.bbs.components.lost.common.LostInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LostImpl implements Lost {
    private final LostRepository repository;
    private final IdGenerator idGenerator;

    LostImpl(LostRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Stream<LostId> allPosts(boolean descending) {
        if (descending) return repository.getAllPostsDes();
        else return repository.getAllPostsAsc();
    }

    @Override
    public Optional<LostInfo> postInfo(LostId lostId) {
        return Optional.ofNullable(repository.getPostInfo(lostId));
    }

    @Override
    public Optional<LostId> publishPost(LostInfo lostInfo) {
        LostId id = LostId.of(idGenerator.generateId());
        if (repository.savePost(id, lostInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<LostInfo> claim(LostId lostId, String founderId) {
        return postInfo(lostId).flatMap(info -> {
            if (info.founderId == null) {
                LostInfo newInfo = info.modFounderId(founderId);
                if (repository.updatePost(lostId, newInfo)) {
                    return Optional.ofNullable(newInfo);
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public boolean removePost(LostId lostId) {
        return repository.deletePost(lostId);
    }

    @Override
    public Long allPostsCount() {
        return repository.count();
    }
}
