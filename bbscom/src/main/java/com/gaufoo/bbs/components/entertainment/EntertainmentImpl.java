package com.gaufoo.bbs.components.entertainment;

import com.gaufoo.bbs.components.entertainment.common.EntertainmentId;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class EntertainmentImpl implements Entertainment {
    final private EntertainmentRepository repository;
    final private IdGenerator idGenerator;

    EntertainmentImpl(EntertainmentRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Stream<EntertainmentId> allPosts(boolean descending) {
        if (descending) return repository.getAllPostsDes();
        else return repository.getAllPostsAsc();
    }

    @Override
    public Stream<EntertainmentId> allPostsByAuthor(String authorId, boolean descending) {
        if (descending) return repository.getAllPostsByAuthorDes(authorId);
        else return repository.getAllPostsByAuthorAsc(authorId);
    }

    @Override
    public Optional<EntertainmentInfo> postInfo(EntertainmentId entertainmentId) {
        return Optional.ofNullable(repository.getPostInfo(entertainmentId));
    }

    @Override
    public Optional<EntertainmentId> publishPost(EntertainmentInfo entertainmentInfo) {
        EntertainmentId id = EntertainmentId.of(idGenerator.generateId());
        if (repository.savePost(id, entertainmentInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean removePost(EntertainmentId entertainmentId) {
        return repository.deletePost(entertainmentId);
    }

    @Override
    public Long allPostsCount() {
        return repository.count();
    }

}
