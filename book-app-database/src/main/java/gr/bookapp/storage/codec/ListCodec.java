package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public record ListCodec<T>(Codec<T> valueCodec, int maxEntries) implements Codec<List<T>> {
    public ListCodec(Codec<T> valueCodec){ this(valueCodec, 20); }

    @Override
    public int maxByteSize() {
        return Integer.BYTES + valueCodec().maxByteSize() * maxEntries;
    }

    @Override
    public List<T> read(RandomAccessFile accessFile) throws IOException {
        int listSize = accessFile.readInt();
        List<T> list = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) list.add(valueCodec.read(accessFile));
        return list;
    }

    @Override
    public void write(RandomAccessFile accessFile, List<T> list) throws IOException {
        if (list.size() > maxEntries) throw new IllegalStateException(String.format("The entries in the list (%s) are more than specified in the constructor (%s)", list.size(), maxEntries));
        accessFile.writeInt(list.size());
        for (T t : list) valueCodec.write(accessFile, t);
    }
}
