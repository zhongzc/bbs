package com.gaufoo.bbs.components.learningResource;

import com.gaufoo.bbs.components.learningResource.common.LearningResourceId;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class LearningResourceSstRepository implements LearningResourceRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;
    private final SST authorIndex;
    private final SST courseIndex;

    private LearningResourceSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
        this.authorIndex = SST.of("author-index", storingPath);
        this.courseIndex = SST.of("course-index", storingPath);
    }

    @Override
    public Stream<LearningResourceId> getAllPostsAsc() {
        return SstUtils.waitFuture(idToInfo.allKeysAsc()).map(i -> i.map(LearningResourceId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<LearningResourceId> getAllPostsDes() {
        return SstUtils.waitFuture(idToInfo.allKeysDes()).map(i -> i.map(LearningResourceId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<LearningResourceId> getAllPostsByAuthorAsc(String authorId) {
        return SstUtils.waitFuture(authorIndex.rangeKeysAsc(fill8(authorId) + "00000000", fill8(authorId) + "99999999")
                .thenApply(keys -> keys.map(LearningResourceSstRepository::retrieveId))).orElse(Stream.empty());
    }

    @Override
    public Stream<LearningResourceId> getAllPostsByAuthorDes(String authorId) {
        return SstUtils.waitFuture(authorIndex.rangeKeysDes(fill8(authorId) + "99999999", fill8(authorId) + "00000000")
                .thenApply(keys -> keys.map(LearningResourceSstRepository::retrieveId))).orElse(Stream.empty());
    }

    @Override
    public Stream<LearningResourceId> getAllPostsOfCourseAsc(String courseCode) {
        return SstUtils.waitFuture(authorIndex.rangeKeysAsc(fill8(courseCode) + "00000000", fill8(courseCode) + "99999999")
                .thenApply(keys -> keys.map(LearningResourceSstRepository::retrieveId))).orElse(Stream.empty());
    }

    @Override
    public Stream<LearningResourceId> getAllPostsOfCourseDes(String courseCode) {
        return SstUtils.waitFuture(authorIndex.rangeKeysDes(fill8(courseCode) + "99999999", fill8(courseCode) + "00000000")
                .thenApply(keys -> keys.map(LearningResourceSstRepository::retrieveId))).orElse(Stream.empty());
    }

    @Override
    public LearningResourceInfo getPostInfo(LearningResourceId postId) {
        return SstUtils.getEntry(idToInfo, postId.value, info -> gson.fromJson(info, LearningResourceInfo.class));
    }

    @Override
    public boolean savePostInfo(LearningResourceId postId, LearningResourceInfo postInfo) {
        if (SstUtils.contains(idToInfo, postId.value)) return false;
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(idToInfo, postId.value, gson.toJson(postInfo)));
        tasks.add(SstUtils.setEntryAsync(authorIndex, fill8(postInfo.authorId) + postId.value, "GAUFOO"));
        tasks.add(SstUtils.setEntryAsync(courseIndex, fill8(postInfo.courseCode) + postId.value, "GAUFOO"));
        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    @Override
    public void deletePostInfo(LearningResourceId postId) {
        Optional.ofNullable(getPostInfo(postId)).ifPresent(info ->
                SstUtils.waitAllFuturesPar(
                        idToInfo.delete(postId.value),
                        authorIndex.delete(fill8(info.authorId) + postId.value),
                        courseIndex.delete(fill8(info.courseCode) + postId.value)
                ));
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToInfo.shutdown(), authorIndex.shutdown(), courseIndex.shutdown());
    }

    private static LearningResourceId retrieveId(String string) {
        return LearningResourceId.of(string.substring(8));
    }

    private static String fill8(String string) {
        return String.format("%8s", string);
    }

    public static LearningResourceRepository get(Path storingPath) {
        return new LearningResourceSstRepository(storingPath);
    }
}
