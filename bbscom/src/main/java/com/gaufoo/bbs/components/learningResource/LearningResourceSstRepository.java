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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LearningResourceSstRepository implements LearningResourceRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;
    private final SST authorIndex;
    private final SST courseIndex;
    private final SST courseCodeToCnt;
    private final SST authorToCnt;
    private final AtomicLong count;

    private LearningResourceSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
        this.authorIndex = SST.of("author-index", storingPath);
        this.courseIndex = SST.of("course-index", storingPath);
        this.courseCodeToCnt = SST.of("courseId-count", storingPath);
        this.authorToCnt = SST.of("author-count", storingPath);
        this.count = new AtomicLong(getAllPostsAsc().count());
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
        tasks.add(SstUtils.setEntryAsync(courseCodeToCnt, postInfo.courseCode, String.valueOf(countOfCourse(postInfo.courseCode) + 1L)));
        tasks.add(SstUtils.setEntryAsync(authorToCnt, postInfo.authorId, String.valueOf(countOfAuthor(postInfo.authorId) + 1L)));
        if (SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b)) {
            this.count.incrementAndGet();
            return true;
        } else return false;
    }

    @Override
    public boolean deletePostInfo(LearningResourceId postId) {
        return Optional.ofNullable(SstUtils.removeEntryByKey(idToInfo, postId.value, info -> gson.fromJson(info, LearningResourceInfo.class))).map(info -> {
            List<CompletionStage<Boolean>> tasks = new ArrayList<>();
            tasks.add(SstUtils.removeEntryAsync(authorIndex, fill8(info.authorId) + postId.value));
            tasks.add(SstUtils.removeEntryAsync(courseIndex, fill8(info.courseCode) + postId.value));
            tasks.add(SstUtils.setEntryAsync(courseCodeToCnt, info.courseCode, String.valueOf(countOfCourse(info.courseCode) - 1L)));
            tasks.add(SstUtils.setEntryAsync(authorToCnt, info.authorId, String.valueOf(countOfAuthor(info.authorId) - 1L)));
            if (SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b)) {
                this.count.decrementAndGet();
                return true;
            } else {
                return false;
            }
        }).orElse(false);
    }

    @Override
    public Long count() {
        return this.count.get();
    }

    @Override
    public Long countOfCourse(String courseCode) {
        return Optional.ofNullable(
                SstUtils.getEntry(courseCodeToCnt, courseCode, Long::parseLong)
        ).orElse(0L);
    }

    @Override
    public Long countOfAuthor(String authorId) {
        return Optional.ofNullable(
                SstUtils.getEntry(authorToCnt, authorId, Long::parseLong)
        ).orElse(0L);
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
