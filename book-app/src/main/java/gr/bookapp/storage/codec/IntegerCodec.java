package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record IntegerCodec() implements Codec<Integer>{
    @Override
    public int maxByteSize() {
        return Integer.BYTES;
    }

    @Override
    public Integer read(RandomAccessFile accessFile) throws IOException {
        return accessFile.readInt();
    }

    @Override
    public void write(RandomAccessFile accessFile, Integer obj) throws IOException {
        accessFile.writeInt(obj);
    }
}
