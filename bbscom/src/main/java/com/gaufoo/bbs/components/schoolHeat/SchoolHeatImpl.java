package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.schoolHeat.common.PostComparators;
import com.gaufoo.bbs.components.schoolHeat.common.PostId;
import com.gaufoo.bbs.components.schoolHeat.common.PostInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class SchoolHeatImpl implements SchoolHeat {
    private final String componentName;
    private final SchoolHeatRepository repository;
    private final IdGenerator idGenerator;
    private final AtomicLong count;

    private SchoolHeatImpl(String componentName, SchoolHeatRepository repository, IdGenerator idGenerator) {
        this.componentName = componentName;
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.count = new AtomicLong(repository.getAllPosts().count());
    }

    @Override
    public Stream<PostId> allPosts(Comparator<PostInfo> comparator) {
        if (comparator.equals(PostComparators.comparingTime)) return repository.allPostsByTimeAsc();
        if (comparator.equals(PostComparators.comparingTimeReversed)) return repository.allPostsByTimeDes();
        if (comparator.equals(PostComparators.comparingHeat)) return repository.allPostsByHeatAsc();
        if (comparator.equals(PostComparators.comparingHeatReversed)) return repository.allPostsByHeatDes();
        else return repository.getAllPosts(comparator);
    }

    @Override
    public Optional<PostInfo> postInfo(PostId postId) {
        return Optional.ofNullable(repository.getPostInfo(postId));
    }

    @Override
    public Optional<PostId> publishPost(PostInfo postInfo) {
        PostId newPostId = PostId.of(idGenerator.generateId());
        if (repository.savePostInfo(newPostId, postInfo)) {
            count.incrementAndGet();
            return Optional.of(newPostId);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void removePost(PostId postId) {
        count.decrementAndGet();
        repository.deletePostInfo(postId);
    }

    @Override
    public void increaseHeat(PostId postId, int delta) {
        PostInfo postInfo = repository.getPostInfo(postId);
        if (postInfo == null) return;
        updatePost(postId, postInfo.modHeat(postInfo.heat + delta));
    }

    @Override
    public void setLatestCommenter(PostId postId, String commenter) {
        PostInfo postInfo = repository.getPostInfo(postId);
        if (postInfo == null) return;
        updatePost(postId, postInfo.modLatestCommenter(commenter));
    }

    @Override
    public Long allPostsCount() {
        return count.get();
    }

    @Override
    public void addComment(PostId postId, String commentIdentifier) {
        postInfo(postId).ifPresent(oldPostInfo -> {
            List<String> commentIds = oldPostInfo.commentIdentifiers;
            commentIds.add(commentIdentifier);
            updatePost(postId, oldPostInfo.modCommentIdentifiers(commentIds)
                    .modCommentCount(oldPostInfo.commentCount + 1));
        });
    }

    @Override
    public void removeComment(PostId postId, String commentIdentifier) {
        postInfo(postId).ifPresent(oldPostInfo -> {
            List<String> commentIds = oldPostInfo.commentIdentifiers;
            commentIds.remove(commentIdentifier);
            updatePost(postId, oldPostInfo.modCommentIdentifiers(commentIds)
                .modCommentCount(oldPostInfo.commentCount - 1));
        });
    }

    @Override
    public void updatePost(PostId postId, PostInfo modPostInfo) {
        repository.updatePostInfo(postId, modPostInfo);
    }

    @Override
    public void shutdown() {
        repository.shutdown();
    }

    @Override
    public String getName() {
        return componentName;
    }

    public static SchoolHeatImpl get(String componentName, SchoolHeatRepository repository, IdGenerator idGenerator) {
        return new SchoolHeatImpl(componentName, repository, idGenerator);
    }
}
