package gr.bookapp.storage.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;

public interface Codec <T>{
    // Could use DataInput & DataOutput instead of RandomAccessFile

    int maxByteSize();
    T read(RandomAccessFile accessFile) throws IOException;
    void write(RandomAccessFile accessFile, T obj) throws IOException;

}
