package com.gaufoo.bbs.components._depr_schoolHeat.common;

import java.util.Collections;
import java.util.Comparator;

public class PostComparators {
    public static final Comparator<PostInfo> comparingTime =
            Comparator.comparing(postInfo -> postInfo.createTime);

    public static final Comparator<PostInfo> comparingTimeReversed =
            Collections.reverseOrder(comparingTime);

    public static final Comparator<PostInfo> comparingHeat =
            Comparator.comparing(postInfo -> postInfo.heat);

    public static final Comparator<PostInfo> comparingHeatReversed =
            Collections.reverseOrder(comparingHeat);
}
