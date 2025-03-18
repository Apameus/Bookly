package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;

public record InstantCodec(StringCodec stringCodec) implements Codec<Instant> {

    @Override
    public int maxByteSize() {
        return stringCodec.maxByteSize();
    }

    @Override
    public Instant read(RandomAccessFile accessFile) throws IOException {
        String date = stringCodec.read(accessFile);
        return InstantFormatter.parse(date);
    }

    @Override
    public void write(RandomAccessFile accessFile, Instant obj) throws IOException {
        String date = InstantFormatter.serialize(obj);
        stringCodec.write(accessFile, date);
    }
}
