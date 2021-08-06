package me.bristermitten.warzone.data.pdc;

import com.google.common.io.ByteStreams;
import com.google.gson.reflect.TypeToken;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListDataType<T> implements PersistentDataType<byte[], List<T>> {
    private final PersistentDataType<byte[], T> singleType;

    public ListDataType(PersistentDataType<byte[], T> singleType) {
        this.singleType = singleType;
    }

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<List<T>> getComplexType() {
        //noinspection unchecked
        return (Class<List<T>>) TypeToken.getParameterized(List.class, singleType.getComplexType()).getRawType();
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull List<T> complex, @NotNull PersistentDataAdapterContext context) {
        //noinspection UnstableApiUsage
        var out = ByteStreams.newDataOutput();
        out.writeInt(complex.size());
        for (T t : complex) {
            out.write(singleType.toPrimitive(t, context));
        }
        return out.toByteArray();
    }

    @Override
    public @NotNull List<T> fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        //noinspection UnstableApiUsage
        var in = ByteStreams.newDataInput(primitive);
        var len = in.readInt();
        var list = new ArrayList<T>(len);

        var size = primitive.length - Integer.BYTES;
        var elementSize = size / len;
        for (int i = 0; i < len; i++) {
            byte[] temp = new byte[elementSize];
            in.readFully(temp);
            var read = singleType.fromPrimitive(temp, context);
            list.add(read);
        }
        return list;
    }
}
