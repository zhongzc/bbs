package com.gaufoo.bbs.components._depr_like;

import com.gaufoo.bbs.components._repositories.LikeComponentMemoryRepository;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components._depr_like.common.LikeId;
import com.gaufoo.bbs.components._depr_like.common.LikeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LikeComponentImpl implements LikeComponent {
    private final String componentName;
    private final LikeComponentRepository repository;
    private final IdGenerator idGenerator;

    LikeComponentImpl(String componentName, LikeComponentRepository repository, IdGenerator idGenerator) {
        this.componentName = componentName;
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<LikeId> initLike(String obj) {
        LikeId id = LikeId.of(idGenerator.generateId());
        LikeInfo info = LikeInfo.of(obj);

        if (repository.saveLike(id, info)) return Optional.of(id);
        return Optional.empty();
    }

    @Override
    public boolean like(LikeId likee, String liker) {
        Optional<LikeInfo> info = Optional.ofNullable(repository.getLikeInfo(likee));
        return info.map(i -> i.liker.add(liker)
                && repository.updateLike(likee, i)).orElse(false);
    }

    @Override
    public boolean cancelLike(LikeId likee, String liker) {
        Optional<LikeInfo> info = Optional.ofNullable(repository.getLikeInfo(likee));
        return info.map(i -> i.liker.remove(liker)
                && repository.updateLike(likee, i)).orElse(false);
    }

    @Override
    public boolean dislike(LikeId likee, String disliker) {
        Optional<LikeInfo> info = Optional.ofNullable(repository.getLikeInfo(likee));
        return info.map(i -> {
            i.liker.remove(disliker);
            return i.disliker.add(disliker)
                    && repository.updateLike(likee, i);
        }).orElse(false);
    }

    @Override
    public Optional<LikeInfo> likeInfo(LikeId likeId) {
        Optional<LikeInfo> info = Optional.ofNullable(repository.getLikeInfo(likeId));
        return info.map(i -> {
            List<String> lk = i.liker;
            List<String> dlk = i.disliker;
            return LikeInfo.of(i.obj, new ArrayList<>(lk), new ArrayList<>(dlk));
        });
    }

    @Override
    public Optional<Integer> likeValue(LikeId likeId, LikeCalc calc) {
        return Optional.ofNullable(repository.getLikeInfo(likeId)).map(info ->
                calc.apply(info.liker.size(), info.disliker.size()));
    }

    @Override
    public Stream<LikeId> allLikes() {
        return repository.getAllLike();
    }

    @Override
    public void remove(LikeId likeId) {
        repository.removeLike(likeId);
    }

    @Override
    public void shutdown() {
        repository.shutdown();
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) {
//        LikeComponent like = LikeComponent.defau1t("", LikeComponentMemoryRepository.get(""), IdGenerator.seqInteger(""));
//        Optional<LikeId> id = like.initLike("happy");
//        like.like(id.get(), "mee");
//        System.out.println(like.likeInfo(id.get()));
//        System.out.println(like.likeValue(id.get(), (l, dl) -> l - dl));
//        like.cancelLike(id.get(), "mee");
//        System.out.println(like.likeInfo(id.get()));
//        like.dislike(id.get(), "mee");
//        System.out.println(like.likeInfo(id.get()));
    }
}
