package com.gaufoo.bbs.components.content;

import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.content.common.ContentInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public class ContentImpl implements Content {
    private final ContentRepository repository;
    private final IdGenerator idGenerator;

    ContentImpl(ContentRepository repository, IdGenerator idGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<ContentId> cons(ContentInfo contentInfo) {
        ContentId id = ContentId.of(idGenerator.generateId());
        if (repository.save(id, contentInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ContentInfo> contentInfo(ContentId contentId) {
        return Optional.ofNullable(repository.get(contentId));
    }

    @Override
    public boolean remove(ContentId contentId) {
         return repository.delete(contentId);
    }
}
