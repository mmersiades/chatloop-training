package org.chatloop.user_training.model.progress;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationResultTest {
    @Test
    public void testMerging() {
        var result = new EvaluationResult();

        var other = new EvaluationResult();
        other.addQualifyingUser("foo", "1");
        other.addQualifyingUser("foo", "2");
        result.merge(other);

        assertEquals(2, result.asEarnedQualifications().count());

        other = new EvaluationResult();
        other.addQualifyingUser("foo", "3");
        other.addQualifyingUser("bar", "2");
        result.merge(other);

        assertEquals(3, result.asEarnedQualifications().count());
    }
}