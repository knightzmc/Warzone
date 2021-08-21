package me.bristermitten.warzone.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrdinalFormatterTest {

    @Test
    void format() {
        assertEquals("34th", OrdinalFormatter.format(34));
        assertEquals("1st", OrdinalFormatter.format(1));
        assertEquals("1029th", OrdinalFormatter.format(1029));
        assertThrows(IllegalArgumentException.class, () -> OrdinalFormatter.format(0));
        assertThrows(IllegalArgumentException.class, () -> OrdinalFormatter.format(-1));
    }

}
