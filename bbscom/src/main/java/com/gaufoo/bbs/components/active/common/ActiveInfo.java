package com.gaufoo.bbs.components.active.common;

import java.time.Instant;
import java.util.Objects;

public class ActiveInfo {
    public final String toucherId;
    public final Instant time;

    private ActiveInfo(String toucherId, Instant time) {
        this.toucherId = toucherId;
        this.time = time;
    }

    public static ActiveInfo of(String toucherId, Instant time) {
        return new ActiveInfo(toucherId, time);
    }

    public ActiveInfo modToucherId(String toucherId) {
        return new ActiveInfo(toucherId, this.time);
    }

    public ActiveInfo modTime(Instant time) {
        return new ActiveInfo(this.toucherId, time);
    }

    @Override
    public String toString() {
        return "ActiveInfo" + "(" + "'" + this.toucherId + "'" + ", " + this.time + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveInfo other = (ActiveInfo) o;
        return Objects.equals(toucherId, other.toucherId) &&
                Objects.equals(time, other.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toucherId, time);
    }
}
