package gr.bookapp.protocol.codec;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public record ListCodec<T>(StreamCodec<T> valueCodec, int maxEntries) implements StreamCodec<List<T>> {
    public ListCodec(StreamCodec<T> valueStreamCodec){ this(valueStreamCodec, 20); }

    @Override
    public int maxByteSize() {
        return Integer.BYTES + valueCodec().maxByteSize() * maxEntries;
    }

    @Override
    public List<T> read(DataInput dataInput) throws IOException {
        int listSize = dataInput.readInt();
        List<T> list = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) list.add(valueCodec.read(dataInput));
        return list;
    }

    @Override
    public void write(DataOutput dataOutput, List<T> list) throws IOException {
        if (list.size() > maxEntries) throw new IllegalStateException(String.format("The entries in the list (%s) are more than specified in the constructor (%s)", list.size(), maxEntries));
        dataOutput.writeInt(list.size());
        for (T t : list) valueCodec.write(dataOutput, t);
    }
}
