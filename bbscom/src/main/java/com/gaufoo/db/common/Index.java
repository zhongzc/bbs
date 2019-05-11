package com.gaufoo.db.common;

import java.util.LinkedList;
import java.util.List;

public class Index<Key, Value> {
    public static class Record<Key, Value> {
        final public Type type;
        final public IndexFactor<Key, Value> factor;
        final public List<Aggregate> agg;

        private Record(Type type, IndexFactor<Key, Value> factor, List<Aggregate> agg) {
            this.type = type;
            this.factor = factor;
            this.agg = agg;
        }

        public static <Key, Value> Record<Key, Value> group(IndexFactor<Key, Value> factor, List<Aggregate> agg) {
            return new Record<>(Type.Group, factor, agg);
        }

        public static <Key, Value> Record<Key, Value> sort(IndexFactor<Key, Value> factor) {
            return new Record<>(Type.Sort, factor, null);
        }
    }

    public enum Type {
        Group,
        Sort
    }

    public final LinkedList<Record<Key, Value>> records = new LinkedList<>();

    private Index() { }

    public GroupIndex<Key, Value> groupBy(IndexFactor<Key, Value> factor, List<Aggregate> agg) {
        this.records.addLast(Record.group(factor, agg));
        return new GroupIndex<Key, Value>() {
            public GroupIndex<Key, Value> groupBy(IndexFactor<Key, Value> factor, List<Aggregate> agg) {
                return Index.this.groupBy(factor, agg);
            }
            public SortIndex<Key, Value> sortBy(IndexFactor<Key, Value> factor) {
                return Index.this.sortBy(factor);
            }
            public Index<Key, Value> build() {
                return Index.this;
            }
        };
    }

    public SortIndex<Key, Value> sortBy(IndexFactor<Key, Value> factor) {
        this.records.addLast(Record.sort(factor));
        return new SortIndex<Key, Value>() {
            public SortIndex<Key, Value> sortBy(IndexFactor<Key, Value> factor) {
                return Index.this.sortBy(factor);
            }
            public Index<Key, Value> build() {
                return Index.this;
            }
        };
    }

    public static <Key, Value> Index<Key, Value> of() {
        return new Index<>();
    }

    public interface GroupIndex<Key, Value> {
        GroupIndex<Key, Value> groupBy(IndexFactor<Key, Value> factor, List<Aggregate> agg);
        SortIndex<Key, Value> sortBy(IndexFactor<Key, Value> factor);
        Index<Key, Value> build();
    }

    public interface SortIndex<Key, Value> {
        SortIndex<Key, Value> sortBy(IndexFactor<Key, Value> factor);
        Index<Key, Value> build();
    }

    public enum Aggregate {
        Count
    }
}