package com.gaufoo.bbs.components.validator;

import java.util.function.Function;

class ValidatorImpl<T> implements Validator<T> {
    private final String componentName;
    private final Function<T, Boolean> pred;

    ValidatorImpl(String componentName, Function<T, Boolean> pred) {
        this.componentName = componentName;
        this.pred = pred;
    }

    @Override
    public Boolean validate(T value) {
        return pred.apply(value);
    }

    @Override
    public String getName() {
        return this.componentName;
    }

}
