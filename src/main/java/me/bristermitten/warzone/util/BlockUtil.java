package me.bristermitten.warzone.util;

import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;

public class BlockUtil {
    private BlockUtil() {

    }

    public static boolean isHalfSlab(BlockData blockData) {
        if (!(blockData instanceof Slab slab)) {
            return false;
        }
        return slab.getType() != Slab.Type.DOUBLE;
    }

    public static boolean isBottomSlab(BlockData blockData) {
        if (!(blockData instanceof Slab slab)) {
            return false;
        }
        return slab.getType() == Slab.Type.BOTTOM;
    }
}
