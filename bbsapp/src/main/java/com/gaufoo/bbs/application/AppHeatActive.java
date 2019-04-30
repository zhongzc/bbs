package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentId;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.TaskChain;

import java.time.Instant;
import java.util.Optional;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class AppHeatActive {

    public static Long getHeat(LearningResourceId learningResourceId) {
        return componentFactory.heat.getHeat(Commons.getGroupId(Commons.PostType.LearningResource), learningResourceId.value).orElse(0L);
    }
    public static Long getHeat(SchoolHeatId schoolHeatId) {
        return componentFactory.heat.getHeat(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value).orElse(0L);
    }
    public static Long getHeat(EntertainmentId entertainmentId) {
        return componentFactory.heat.getHeat(Commons.getGroupId(Commons.PostType.Entertainment), entertainmentId.value).orElse(0L);
    }

    public static ActiveInfo fetchActiveInfoAndUnwrap(LearningResourceId learningResourceId) {
        return fetchActiveInfo(Commons.getGroupId(Commons.PostType.LearningResource), learningResourceId.value)
                .reduce(e -> null, i -> i);
    }
    public static ActiveInfo fetchActiveInfoAndUnwrap(SchoolHeatId schoolHeatId) {
        return fetchActiveInfo(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value)
                .reduce(e -> null, i -> i);
    }
    public static ActiveInfo fetchActiveInfoAndUnwrap(EntertainmentId entertainmentId) {
        return fetchActiveInfo(Commons.getGroupId(Commons.PostType.Entertainment), entertainmentId.value)
                .reduce(e -> null, i -> i);
    }

    private static TaskChain.Procedure<ErrorCode, ActiveInfo> fetchActiveInfo(String postGroupId, String id) {
        return TaskChain.Procedure.fromOptional(
                componentFactory.active.getLatestActiveInfo(postGroupId, id),
                ErrorCode.LatestActiveNotFound
        );
    }

    public static TaskChain.Procedure<ErrorCode, Void> createActiveAndHeat(LearningResourceId learningResourceId, UserId userId) {
        return createActiveAndHeat(Commons.getGroupId(Commons.PostType.LearningResource), learningResourceId.value, userId);
    }
    public static TaskChain.Procedure<ErrorCode, Void> createActiveAndHeat(SchoolHeatId schoolHeatId, UserId userId) {
        return createActiveAndHeat(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value, userId);
    }
    public static TaskChain.Procedure<ErrorCode, Void> createActiveAndHeat(EntertainmentId entertainmentId, UserId userId) {
        return createActiveAndHeat(Commons.getGroupId(Commons.PostType.Entertainment), entertainmentId.value, userId);
    }

    private static TaskChain.Procedure<ErrorCode, Void> createActiveAndHeat(String postGroupId, String itemId, UserId userId) {
        String currentActiveTimeWin = Commons.currentActiveTimeWindow();

        Optional<ActiveInfo> activeInfo = componentFactory.active.touch(postGroupId, itemId, userId.value);
        Optional<ActiveInfo> mostActiveInfo = componentFactory.active.touch(currentActiveTimeWin, postGroupId + itemId, userId.value);
        Optional<Long> heat = componentFactory.heat.increase(postGroupId, itemId, 1);
        Optional<Long> hottest = componentFactory.heat.increase(currentActiveTimeWin, postGroupId + itemId, 1);

        boolean success = activeInfo.isPresent() && mostActiveInfo.isPresent() && heat.isPresent() && hottest.isPresent();
        return success ? TaskChain.Result.of(null, () -> AppHeatActive.clearActiveAndHeat(postGroupId, itemId)) :
                TaskChain.Fail.of(ErrorCode.CreateActiveAndHeatFailed);
    }


    public static TaskChain.Procedure<ErrorCode, Void> alterHeat(LearningResourceId learningResourceId, Instant time, long delta) {
        return alterHeat(Commons.getGroupId(Commons.PostType.LearningResource), learningResourceId.value, time, delta);
    }
    public static TaskChain.Procedure<ErrorCode, Void> alterHeat(SchoolHeatId schoolHeatId, Instant time, long delta) {
        return alterHeat(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value, time, delta);
    }
    public static TaskChain.Procedure<ErrorCode, Void> alterHeat(EntertainmentId entertainmentId, Instant time, long delta) {
        return alterHeat(Commons.getGroupId(Commons.PostType.Entertainment), entertainmentId.value, time, delta);
    }

    private static TaskChain.Procedure<ErrorCode, Void> alterHeat(String postGroupId, String itemId, Instant time, long delta) {
        String timeWinToModify = Commons.heatTimeWindow(time);
        String curHeatTimeWin = Commons.currentHeatTimeWindow();
        String lastHeatTimeWin = Commons.lastHeatTimeWindow();

        return TaskChain.Procedure.fromOptional(componentFactory.heat.increase(postGroupId, itemId, delta), ErrorCode.AlterHeatFailed)
                .then(__ -> TaskChain.Result.of(null, () -> componentFactory.heat.increase(postGroupId, itemId, -delta)))
                .mapR(__ -> timeWinToModify.equals(curHeatTimeWin) || timeWinToModify.equals(lastHeatTimeWin))
                .then(needAlterHottest -> !needAlterHottest ? TaskChain.Result.of(null) :
                        TaskChain.Procedure.fromOptional(componentFactory.heat.increase(timeWinToModify, postGroupId + itemId, delta), ErrorCode.AlterHeatFailed)
                                .then(__ -> TaskChain.Result.of(null, () -> componentFactory.heat.increase(timeWinToModify, postGroupId + itemId, -delta)))
                                .then(__ -> TaskChain.Result.of(null)));
    }


    public static TaskChain.Procedure<ErrorCode, Void> touchActive(LearningResourceId learningResourceId, UserId toucherId) {
        return touchActive(Commons.getGroupId(Commons.PostType.LearningResource), learningResourceId.value, toucherId);
    }
    public static TaskChain.Procedure<ErrorCode, Void> touchActive(SchoolHeatId schoolHeatId, UserId toucherId) {
        return touchActive(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value, toucherId);
    }
    public static TaskChain.Procedure<ErrorCode, Void> touchActive(EntertainmentId entertainmentId, UserId toucherId) {
        return touchActive(Commons.getGroupId(Commons.PostType.Entertainment), entertainmentId.value, toucherId);
    }

    private static TaskChain.Procedure<ErrorCode, Void> touchActive(String postGroupId, String itemId, UserId toucherId) {
        String toucher = toucherId.value;

        String curActiveTimeWin = Commons.currentActiveTimeWindow();

        return TaskChain.Procedure.fromOptional(componentFactory.active.touch(postGroupId, itemId, toucher), ErrorCode.UnableToTouch)
                .mapR(__ -> componentFactory.active.touch(curActiveTimeWin, postGroupId + itemId, toucher))
                .then(__ -> TaskChain.Result.of(null));
    }


    public static TaskChain.Procedure<ErrorCode, Void> clearActiveAndHeat(LearningResourceId learningResourceId) {
        return clearActiveAndHeat(Commons.getGroupId(Commons.PostType.LearningResource), learningResourceId.value);
    }
    public static TaskChain.Procedure<ErrorCode, Void> clearActiveAndHeat(SchoolHeatId schoolHeatId) {
        return clearActiveAndHeat(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value);
    }
    public static TaskChain.Procedure<ErrorCode, Void> clearActiveAndHeat(EntertainmentId entertainmentId) {
        return clearActiveAndHeat(Commons.getGroupId(Commons.PostType.Entertainment), entertainmentId.value);
    }
    private static TaskChain.Procedure<ErrorCode, Void> clearActiveAndHeat(String postGroupId, String id) {
        String currentActiveTimeWin = Commons.currentActiveTimeWindow();
        String lastActiveTimeWin = Commons.lastActiveTimeWindow();

        boolean rmActive = componentFactory.active.remove(postGroupId, id);
        componentFactory.active.remove(currentActiveTimeWin, postGroupId + id);
        componentFactory.active.remove(lastActiveTimeWin, postGroupId + id);
        boolean rmHeat = componentFactory.heat.remove(postGroupId, id);
        componentFactory.heat.remove(currentActiveTimeWin, postGroupId + id);
        componentFactory.heat.remove(lastActiveTimeWin, postGroupId + id);

        boolean success = rmActive && rmHeat;
        return success ? TaskChain.Result.of(null) : TaskChain.Fail.of(ErrorCode.ClearActiveAndHeatFailed);
    }
}
