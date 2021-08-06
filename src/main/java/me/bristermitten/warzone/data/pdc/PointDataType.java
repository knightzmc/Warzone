package me.bristermitten.warzone.data.pdc;

import me.bristermitten.warzone.data.Point;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class PointDataType implements PersistentDataType<byte[], Point> {
    public static final PointDataType INSTANCE = new PointDataType();
    private PointDataType() {}

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<Point> getComplexType() {
        return Point.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull Point complex, @NotNull PersistentDataAdapterContext context) {
        var buf = ByteBuffer.wrap(new byte[3 * Integer.SIZE / 8]);
        buf.putInt(complex.x());
        buf.putInt(complex.y());
        buf.putInt(complex.z());
        return buf.array();
    }

    @Override
    public @NotNull Point fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        var buf = ByteBuffer.wrap(primitive);
        int x = buf.getInt();
        int y = buf.getInt();
        int z = buf.getInt();
        return new Point(x, y, z);
    }
}
