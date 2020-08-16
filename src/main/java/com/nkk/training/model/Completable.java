package com.nkk.training.model;

public interface Completable {

    float getValue();

    int getValuePercent();

    default boolean isComplete() {
        return getValuePercent() == 100;
    }
}
