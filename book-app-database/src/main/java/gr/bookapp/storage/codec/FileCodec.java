package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface FileCodec<T>{
    int maxByteSize();
    T read(RandomAccessFile accessFile) throws IOException;
    void write(RandomAccessFile accessFile, T obj) throws IOException;
}
