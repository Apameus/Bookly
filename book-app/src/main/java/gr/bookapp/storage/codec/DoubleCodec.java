package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record DoubleCodec() implements Codec<Double>{
    @Override
    public int maxByteSize() {
        return Double.BYTES;
    }

    @Override
    public Double read(RandomAccessFile accessFile) throws IOException {
        return accessFile.readDouble();
    }

    @Override
    public void write(RandomAccessFile accessFile, Double obj) throws IOException {
        accessFile.writeDouble(obj);
    }
}
