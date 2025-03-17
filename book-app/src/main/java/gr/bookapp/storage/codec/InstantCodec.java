package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;

public record InstantCodec(LongCodec longCodec) implements Codec<Instant> {
    @Override
    public int maxByteSize() {
        return longCodec().maxByteSize();
    }

    @Override
    public Instant read(RandomAccessFile accessFile) throws IOException {
        return Instant.ofEpochSecond(longCodec().read(accessFile));
    }

    @Override
    public void write(RandomAccessFile accessFile, Instant obj) throws IOException {
        longCodec.write(accessFile, obj.getEpochSecond());
    }
}
