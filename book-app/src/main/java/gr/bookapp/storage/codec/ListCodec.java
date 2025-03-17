package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public record ListCodec<T>(Codec<T> valueCodec) implements Codec<List<T>> {

    @Override
    public int maxByteSize() { //todo check
        return valueCodec().maxByteSize() * 20; // MAX 20 ENTRIES FOR THE LIST
    }

    @Override
    public List<T> read(RandomAccessFile accessFile) throws IOException {
        List<T> list = new ArrayList<>();
        int listSize = accessFile.readInt();
        for (int i = 0; i < listSize; i++) {
            list.add(valueCodec.read(accessFile));
        }
        return list;
    }

    @Override
    public void write(RandomAccessFile accessFile, List<T> list) throws IOException {
        accessFile.writeInt(list.size());
        for (T t : list) valueCodec.write(accessFile, t);
    }
}
