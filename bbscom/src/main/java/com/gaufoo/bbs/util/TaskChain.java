package com.gaufoo.bbs.util;

import java.util.Optional;
import java.util.function.Function;

public interface TaskChain {
    interface Procedure<T> {
        <R> Procedure<R> then(Function<T, Procedure<R>> fn);
        boolean isSuccessful();
        Optional<String> retrieveError();
        Optional<T> retrieveResult();

        static <T> Procedure<T> fromOptional(Optional<T> optional, String error, Runnable rollback) {
            return optional.map(i -> (Procedure<T>) Result.of(i, rollback)).orElse(new Fail<>(error));
        }

        static <T> Procedure<T> fromOptional(Optional<T> optional, String error) {
            return optional.map(i -> (Procedure<T>) Result.of(i)).orElse(new Fail<>(error));
        }
    }

    class Result<T> implements Procedure<T> {
        private final T result;
        private final Runnable rollback;

        private Result(T result, Runnable rollback) {
            this.result = result;
            this.rollback = rollback;
        }

        @Override
        public <R> Procedure<R> then(Function<T, Procedure<R>> fn) {
            Procedure<R> r = fn.apply(result);
            if (!r.isSuccessful()) {
                rollback.run();
                return r;
            } else {
                Result<R> s = (Result<R>) r;
                return new Result<>(s.result, () -> {s.rollback.run(); rollback.run();});
            }
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public Optional<String> retrieveError() {
            return Optional.empty();
        }

        @Override
        public Optional<T> retrieveResult() {
            return Optional.of(result);
        }

        public static <T> Result<T> of(T result, Runnable rollback) {
            return new Result<>(result, rollback);
        }

        public static <T> Result<T> of(T result) {
            return new Result<>(result, () -> {});
        }
    }

    class Fail<T> implements Procedure<T> {
        private final String error;

        private Fail(String error) {
            this.error = error;
        }

        @Override
        public <R> Procedure<R> then(Function<T, Procedure<R>> fn) {
            return new Fail<>(error);
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }

        @Override
        public Optional<String> retrieveError() {
            return Optional.of(error);
        }

        @Override
        public Optional<T> retrieveResult() {
            return Optional.empty();
        }

        public static <T> Fail<T> of(String error) {
            return new Fail<>(error);
        }
    }

    static void main(String[] args) {
        Optional<Integer> i = Optional.of(12);
        Procedure<Integer> a = Procedure.fromOptional(i, "oh", () -> System.out.println("hello"));

        System.out.println(a.isSuccessful());
        System.out.println(a.retrieveResult());
        System.out.println();

        Procedure<Integer> c = a.then(ii -> Procedure.fromOptional(Optional.empty(), "sh*t", () -> System.out.println("world")));
        System.out.println(c.isSuccessful());
        System.out.println(c.retrieveError());
        System.out.println();

        Procedure<Integer> s =
        Result.of(1,               () -> System.out.println("erase 1"))  .then(one ->
        Result.of(one.toString(),        () -> System.out.println("erase 2"))) .then(two ->
        Result.of(Integer.parseInt(two), () -> {})
        );

        System.out.println(s.retrieveResult());
        System.out.println(s.retrieveError());
        System.out.println();



        Procedure<Integer> b =
        Result.of(1,                 () -> System.out.println("erase 1")  ).then(one ->
        Result.of(one.toString(),          () -> System.out.println("erase 2")) ).then(two ->
        Fail.<Integer>of("oh sh*t")                                             ).then(thr ->
        Result.of(thr + 1,            () -> System.out.println("erase 3")));

        System.out.println(b.retrieveResult());
        System.out.println(b.retrieveError());
    }
}
