package org.chatloop.user_training.basic;

import org.chatloop.user_training.service.impl.LimitCalculator;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LimitCalculatorTest {

    @Test
    public void test() {
        LimitCalculator calc = new LimitCalculator();

        assertEquals(calc.getLimit(3), 1);
        assertEquals(calc.getLimit(4), 1);
        assertEquals(calc.getLimit(5), 1);
        assertEquals(calc.getLimit(6), 2);
        assertEquals(calc.getLimit(7), 2);
        assertEquals(calc.getLimit(8), 2);
        assertEquals(calc.getLimit(9), 3);
        assertEquals(calc.getLimit(10), 3);
    }

    @Test
    public void testPrintAllAutoLimits() {
        int maxNumModules = 12;
        for (int numModules = 1; numModules < maxNumModules; numModules++) {
            int limitRounded = Math.round(0.33f * numModules);
            int limitCeiled = (int) Math.ceil(0.33f * numModules);
            int limitFloored = (int) Math.floor(0.33f * numModules);

            String message = String.format("numModules=%d -> %d, %d, %d", numModules, limitRounded, limitCeiled, limitFloored);
            System.out.println(message);
        }
    }
}
