package org.chatloop.user_training.util;

import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilsTest {
    @Test
    public void testAreEqual() {
        var first = new HashMap<String, Set<String>>();
        first.put("foo", Sets.newSet("a", "b"));

        var second = new HashMap<String, Set<String>>();
        assertFalse(MapUtils.areEqual(first, second));

        second.put("foo", Sets.newSet("a"));
        assertFalse(MapUtils.areEqual(first, second));

        second.put("foo", Sets.newSet("a", "b"));
        assertTrue(MapUtils.areEqual(first, second));

        first.put("bar", Sets.newSet("c"));
        assertFalse(MapUtils.areEqual(first, second));

        second.put("bar", Sets.newSet("c"));
        assertTrue(MapUtils.areEqual(first, second));

        first.remove("bar");
        assertFalse(MapUtils.areEqual(first, second));
    }
}