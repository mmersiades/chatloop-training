package org.chatloop.user_training.service.impl;

import java.util.Map;

public final class LimitCalculator {

    private static final float THRESHOLD = 0.33f;

    public int getLimit(int numberOfModules) {
        int fallback = Math.round(THRESHOLD * numberOfModules);
        return MODULE_CNT_2_LIMIT.getOrDefault(numberOfModules, fallback);
    }

    /**
     * Key is number of modules, value is allowed failure limit. See PDF
     * specification.
     */
    private static final Map<Integer, Integer> MODULE_CNT_2_LIMIT;
    static {
        MODULE_CNT_2_LIMIT = Map.of(
                1, 0,
                2, 0,
                3, 1,
                4, 1,
                5, 1,
                6, 2,
                7, 2,
                8, 2,
                9, 3,
                10, 3);
    }
}
