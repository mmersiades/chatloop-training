package com.chatloop.training.service.impl;

import java.util.Map;

public class LimitCalculator {

    private static final float THRESHOLD = 0.33f;

    public int getLimit(int numberOfModules) {
        int fallback = Math.round(THRESHOLD * numberOfModules);
        return moduleCnt2Limit.getOrDefault(numberOfModules, fallback);
    }

    /**
     * Key is number of modules, value is allowed failure limit.
     */
    private static final Map<Integer, Integer> moduleCnt2Limit;
    static {
        moduleCnt2Limit = Map.of(1, 0, 2, 0, 3, 1, 4, 1, 5, 1, 6, 2, 7, 2, 8, 2);
    }
}
