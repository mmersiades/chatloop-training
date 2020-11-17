package org.chatloop.user_training.util;

import java.util.Map;
import java.util.stream.Collectors;

public final class MapUtils {
    /**
     * Returns a useful string representation of the specified {@link Map}.
     * @param map a {@link Map}
     * @return {@link String}
     */
    public static String convertWithStream(Map<?, ?> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    /**
     * Returns {@code true} if the given {@link Map}s contain equal keys mapping to equal values.
     * @param first the first map
     * @param second the other map to compare to
     * @return boolean
     */
    public static boolean areEqual(Map<?, ?> first, Map<?, ?> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
}
