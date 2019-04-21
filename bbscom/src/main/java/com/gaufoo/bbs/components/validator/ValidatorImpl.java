package com.gaufoo.bbs.components.validator;

import java.util.function.Function;

class ValidatorImpl<T> implements Validator<T> {
    private final Function<T, Boolean> pred;

    ValidatorImpl(Function<T, Boolean> pred) {
        this.pred = pred;
    }

    @Override
    public Boolean validate(T value) {
        return pred.apply(value);
    }
}
