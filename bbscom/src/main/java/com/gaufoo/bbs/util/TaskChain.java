package com.gaufoo.bbs.util;

import scala.Int;

import java.util.Optional;
import java.util.function.Function;

public interface TaskChain {
    interface Procedure<U, T> {
        <R> Procedure<U, R> then(Function<T, Procedure<U, R>> fn);
        <E> Procedure<E, T> mapE(Function<U, E> efn);
        <R> R reduce(Function<U, R> efn, Function<T, R> fn);
        boolean isSuccessful();
        Optional<U> retrieveError();
        Optional<T> retrieveResult();

        static <U, T> Procedure<U, T> fromOptional(Optional<T> optional, U error, Runnable rollback) {
            return optional.map(i -> (Procedure<U, T>) Result.of(i, rollback)).orElse(new Fail<>(error));
        }

        static <U, T> Procedure<U, T> fromOptional(Optional<T> optional, U error) {
            return optional.map(i -> (Procedure<U, T>) Result.of(i)).orElse(new Fail<>(error));
        }

        static <U, T> Procedure<U, T> ofNullable(T nullable, U error) {
            return fromOptional(Optional.ofNullable(nullable), error);
        }

        static <U, T> Procedure<U, T> ofNullable(T nullable, U error, Runnable rollback) {
            return fromOptional(Optional.ofNullable(nullable), error, rollback);
        }
    }

    class Result<U, T> implements Procedure<U, T> {
        private final T result;
        private final Runnable rollback;

        private Result(T result, Runnable rollback) {
            this.result = result;
            this.rollback = rollback;
        }

        @Override
        public <R> Procedure<U, R> then(Function<T, Procedure<U, R>> fn) {
            Procedure<U, R> r = fn.apply(result);
            if (!r.isSuccessful()) {
                rollback.run();
                return r;
            } else {
                Result<U, R> s = (Result<U, R>) r;
                return new Result<>(s.result, () -> {s.rollback.run(); rollback.run();});
            }
        }

        @Override
        public <E> Procedure<E, T> mapE(Function<U, E> efn) {
            return Result.of(result);
        }

        @Override
        public <R> R reduce(Function<U, R> efn, Function<T, R> fn) {
            return fn.apply(result);
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public Optional<U> retrieveError() {
            return Optional.empty();
        }

        @Override
        public Optional<T> retrieveResult() {
            return Optional.of(result);
        }

        public static <U, T> Result<U, T> of(T result, Runnable rollback) {
            return new Result<>(result, rollback);
        }

        public static <U, T> Result<U, T> of(T result) {
            return new Result<>(result, () -> {});
        }
    }

    class Fail<U, T> implements Procedure<U, T> {
        private final U error;

        private Fail(U error) {
            this.error = error;
        }

        @Override
        public <R> Procedure<U, R> then(Function<T, Procedure<U, R>> fn) {
            return new Fail<>(error);
        }

        @Override
        public <E> Procedure<E, T> mapE(Function<U, E> efn) {
            return Fail.of(efn.apply(error));
        }

        @Override
        public <R> R reduce(Function<U, R> efn, Function<T, R> fn) {
            return efn.apply(error);
        }

        @Override
        public boolean isSuccessful() {
            return false;
        }

        @Override
        public Optional<U> retrieveError() {
            return Optional.of(error);
        }

        @Override
        public Optional<T> retrieveResult() {
            return Optional.empty();
        }

        public static <U, T> Fail<U, T> of(U error) {
            return new Fail<>(error);
        }
    }

    static void main(String[] args) {
        Optional<Integer> i = Optional.of(12);
        Procedure<String, Integer> a = Procedure.fromOptional(i, "oh", () -> System.out.println("hello"));

        System.out.println(a.isSuccessful());
        System.out.println(a.retrieveResult());
        System.out.println();

        Procedure<String, Integer> c = a.then(ii -> Procedure.fromOptional(Optional.empty(), "sh*t", () -> System.out.println("world")));
        System.out.println(c.isSuccessful());
        System.out.println(c.retrieveError());
        System.out.println();

        Procedure<String, Integer> s =
        Result.<String, Integer>of(1,               () -> System.out.println("erase 1"))  .then(one ->
        Result.of(one.toString(),        () -> System.out.println("erase 2"))) .then(two ->
        Result.of(Integer.parseInt(two), () -> {})
        );

        System.out.println(s.retrieveResult());
        System.out.println(s.retrieveError());
        System.out.println();



        Procedure<String, Integer> b =
        Result.<String, Integer>of(1,                 () -> System.out.println("erase 1")  ).then(one ->
        Result.of(one.toString(),          () -> System.out.println("erase 2")) ).then(two ->
        Fail.<String, Integer>of("oh sh*t")                                             ).then(thr ->
        Result.of(thr + 1,            () -> System.out.println("erase 3")));

        System.out.println(b.retrieveResult());
        System.out.println(b.retrieveError());
    }
}
