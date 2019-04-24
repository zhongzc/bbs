package com.gaufoo.bbs.application.types;

import java.util.List;

public interface Hot {
    interface HotItem {}
    interface HotsResult {}
    interface Hots extends HotsResult {
        List<HotItem> getHots();
    }
}
