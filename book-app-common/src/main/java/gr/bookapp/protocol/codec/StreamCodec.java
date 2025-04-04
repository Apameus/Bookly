package gr.bookapp.protocol.codec;

import java.io.*;

public interface StreamCodec<T>{
    int maxByteSize();
    T read(DataInput dataInput) throws IOException;
    void write(DataOutput dataOutput, T obj) throws IOException;

}
