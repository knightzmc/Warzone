package me.bristermitten.warzone.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegionTest {

    @Test
    void segment() {
        var enclosingRegion = new Region(new Point(0, 0, 0), new Point(50, 50, 50));

        var segment = enclosingRegion.segment(
                new Point(-10, 0, 10),
                new Point(60, 100, 10)
        );

        assertEquals(new Point(0, 0, 10), segment.min());
        assertEquals(new Point(50, 50, 10), segment.max());
    }
}
