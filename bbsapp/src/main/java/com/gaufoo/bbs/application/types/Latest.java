package com.gaufoo.bbs.application.types;

import java.util.List;

public interface Latest {
    interface LatestItem {}
    interface LatestsResult {}
    interface Latests extends LatestsResult {
        List<LatestItem> getLatests();
    }
}
