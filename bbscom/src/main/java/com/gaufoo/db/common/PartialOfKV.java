package com.gaufoo.db.common;

public interface PartialOfKV<Key, Value> {
    String model(Key key, Value value);
}