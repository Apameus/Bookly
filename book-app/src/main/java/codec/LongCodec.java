package codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record LongCodec() implements Codec<Long>{
    @Override
    public int maxByteSize() {
        return Long.BYTES;
    }

    @Override
    public Long read(RandomAccessFile accessFile) throws IOException {
        return accessFile.readLong();
    }

    @Override
    public void write(RandomAccessFile accessFile, Long aLong) throws IOException {
        accessFile.writeLong(aLong);
    }
}
