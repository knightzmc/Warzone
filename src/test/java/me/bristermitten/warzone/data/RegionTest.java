package me.bristermitten.warzone.data;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

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

    private World createWorld() {
        return (World) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{World.class}, (proxy, method, args) -> {
            if (method.equals(World.class.getMethod("getMinHeight"))) {
                return 0;
            }
            if (method.equals(World.class.getMethod("getMaxHeight"))) {
                return 256;
            }
            return null;
        });
    }

    private Chunk createChunk(int x, int z) {
        return (Chunk) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Chunk.class}, (proxy, method, args) -> {
            if (method.equals(Chunk.class.getMethod("getX"))) {
                return x;
            }
            if (method.equals(Chunk.class.getMethod("getZ"))) {
                return z;
            }
            if (method.equals(Chunk.class.getMethod("getWorld"))) {
                return createWorld();
            }
            return null;
        });
    }

    @Test
    void chunk() {
        Chunk chunk1 = createChunk(5, 6);
        Chunk chunk2 = createChunk(-7, 17);
        Chunk chunk3 = createChunk(-11, 1);
        var region = Region.of(
                new Point(0, 0, 0),
                new Point(250, 128, 250)
        );

        assertTrue(region.contains(chunk1));
        assertFalse(region.contains(chunk2));
        assertFalse(region.contains(chunk3));
    }
}
