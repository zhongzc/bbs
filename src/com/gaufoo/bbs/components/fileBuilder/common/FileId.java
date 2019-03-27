package com.gaufoo.bbs.components.fileBuilder.common;

import java.util.Objects;

final public class FileId {
    public final String value;

    private FileId(String value) {
        this.value = value;
    }

    public static FileId of(String value) {
        return new FileId(value);
    }

    public FileId modValue(String value) {
        return new FileId(value);
    }

    @Override
    public String toString() {
        return "FileId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileId other = (FileId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
