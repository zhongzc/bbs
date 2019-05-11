package com.gaufoo.db.common;

public class IndexFactor<Key, Value> {
    final public PartialOfKV<Key, Value> partialOfKV;
    final public int strLength;

    private IndexFactor(PartialOfKV<Key, Value> partialOfKV, int strLength) {
        this.partialOfKV = partialOfKV;
        this.strLength = strLength;
    }

    public static <Key, Value> IndexFactor<Key, Value> of(PartialOfKV<Key, Value> partialOfKV, int strLength) {
        return new IndexFactor<>(partialOfKV, strLength);
    }
}