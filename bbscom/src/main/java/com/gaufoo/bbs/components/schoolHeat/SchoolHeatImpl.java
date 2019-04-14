package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.schoolHeat.common.PostComparators;
import com.gaufoo.bbs.components.schoolHeat.common.PostId;
import com.gaufoo.bbs.components.schoolHeat.common.PostInfo;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class SchoolHeatImpl implements SchoolHeat {
    private final String componentName;
    private final HeatRepository repository;
    private final IdGenerator idGenerator;

    private SchoolHeatImpl(String componentName, HeatRepository repository, IdGenerator idGenerator) {
        this.componentName = componentName;
        this.repository = repository;
        this.idGenerator = idGenerator;
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

        return repository.savePostInfo(newPostId, postInfo) ?
                Optional.of(newPostId) :
                Optional.empty();
    }

    @Override
    public void removePost(PostId postId) {
        repository.deletePostInfo(postId);
    }

    @Override
    public void increaseHeat(PostId postId, int delta) {
        PostInfo postInfo = repository.getPostInfo(postId);
        if (postInfo == null) return;
        updatePost(postId, postInfo.modHeat(postInfo.heat + delta));
    }

    @Override
    public void setLatestReplier(PostId postId, String replier) {
        PostInfo postInfo = repository.getPostInfo(postId);
        if (postInfo == null) return;
        updatePost(postId, postInfo.modLatestReplier(replier));
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

    public static SchoolHeatImpl get(String componentName, HeatRepository repository, IdGenerator idGenerator) {
        return new SchoolHeatImpl(componentName, repository, idGenerator);
    }
}
