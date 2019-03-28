package com.gaufoo.bbs.components.learningResource.common;

import java.util.Objects;

final public class ResourceId {
    public final String value;

    private ResourceId(String value) {
        this.value = value;
    }

    public static ResourceId of(String value) {
        return new ResourceId(value);
    }

    public ResourceId modValue(String value) {
        return new ResourceId(value);
    }

    @Override
    public String toString() {
        return "ResourceId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceId other = (ResourceId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
