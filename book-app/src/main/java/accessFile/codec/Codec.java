package accessFile.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface Codec<T> {

    int maxByteSize();
    T read(RandomAccessFile accessFile) throws IOException;
    void write(RandomAccessFile accessFile, T t) throws IOException;
}
