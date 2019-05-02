package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.types.*;

import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class AppLatestAndHots {
    public static Latest.LatestsResult latests() {
        String lastTimeWin = Commons.lastActiveTimeWindow();
        return (Latest.Latests) () -> componentFactory.active.getAllDes(lastTimeWin)
                .map(AppLatestAndHots::fetchLatest).collect(Collectors.toList());
    }

    public static Hot.Hots hots() {
        String lastTimeWin = Commons.lastHeatTimeWindow();
        return () -> componentFactory.heat.getAllDes(lastTimeWin)
                .map(AppLatestAndHots::fetchHots).collect(Collectors.toList());
    }

    private static Latest.LatestItem fetchLatest(String groupPostIdPack) {
        Commons.PostType postType = Commons.parseGroupId(groupPostIdPack);
        String postId = Commons.parseItemId(groupPostIdPack);

        switch (postType) {
            case LearningResource: return convertActive(AppLearningResource.learningResourceInfo(postId));
            case Entertainment: return convertActive(AppEntertainment.entertainmentInfo(postId));
            case SchoolHeat: return convertActive(AppSchoolHeat.schoolHeatInfo(postId));
            default: return null;
        }
    }

    private static Hot.HotItem fetchHots(String groupPostIdPack) {
        Commons.PostType postType = Commons.parseGroupId(groupPostIdPack);
        String postId = Commons.parseItemId(groupPostIdPack);

        switch (postType) {
            case LearningResource: return convertHeat(AppLearningResource.learningResourceInfo(postId));
            case Entertainment: return convertHeat(AppEntertainment.entertainmentInfo(postId));
            case SchoolHeat: return convertHeat(AppSchoolHeat.schoolHeatInfo(postId));
            default: return null;
        }
    }

    private static Latest.LatestItem convertActive(LearningResource.LearningResourceInfoResult result) {
        return result instanceof LearningResource.LearningResourceInfo ?
                (LearningResource.LearningResourceInfo)result : null;
    }

    private static Latest.LatestItem convertActive(Entertainment.EntertainmentInfoResult result) {
        return result instanceof Entertainment.EntertainmentInfo ?
                (Entertainment.EntertainmentInfo) result : null;
    }

    private static Latest.LatestItem convertActive(SchoolHeat.SchoolHeatInfoResult result) {
        return result instanceof SchoolHeat.SchoolHeatInfo ?
                (SchoolHeat.SchoolHeatInfo) result: null;
    }

    private static Hot.HotItem convertHeat(LearningResource.LearningResourceInfoResult result) {
        return result instanceof LearningResource.LearningResourceInfo ?
                (LearningResource.LearningResourceInfo)result : null;
    }

    private static Hot.HotItem convertHeat(Entertainment.EntertainmentInfoResult result) {
        return result instanceof Entertainment.EntertainmentInfo ?
                (Entertainment.EntertainmentInfo) result : null;
    }

    private static Hot.HotItem convertHeat(SchoolHeat.SchoolHeatInfoResult result) {
        return result instanceof SchoolHeat.SchoolHeatInfo ?
                (SchoolHeat.SchoolHeatInfo) result: null;
    }
}
