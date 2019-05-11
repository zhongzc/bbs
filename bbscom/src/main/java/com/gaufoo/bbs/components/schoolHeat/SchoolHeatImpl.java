package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class SchoolHeatImpl implements SchoolHeat {
    private final SchoolHeatRepository repository;
    private final IdGenerator idGenerator;

    SchoolHeatImpl(SchoolHeatRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Stream<SchoolHeatId> allPosts(boolean descending) {
        if (descending) return repository.getAllPostsDes();
        else return repository.getAllPostsAsc();
    }

    @Override
    public Stream<SchoolHeatId> allPostsByAuthor(String authorId, boolean descending) {
        if (descending) return repository.getAllPostsByAuthorDes(authorId);
        else return repository.getAllPostsByAuthorAsc(authorId);
    }

    @Override
    public Optional<SchoolHeatInfo> postInfo(SchoolHeatId schoolHeatId) {
        return Optional.ofNullable(repository.getPostInfo(schoolHeatId));
    }

    @Override
    public Optional<SchoolHeatId> publishPost(SchoolHeatInfo schoolHeatInfo) {
        SchoolHeatId id = SchoolHeatId.of(idGenerator.generateId());
        if (repository.savePost(id, schoolHeatInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean removePost(SchoolHeatId schoolHeatId) {
        return repository.deletePost(schoolHeatId);
    }

    @Override
    public Long allPostsCount() {
        return repository.count();
    }

    @Override
    public Long allPostsCountByAuthor(String authorId) {
        return repository.countByAuthor(authorId);
    }
}
