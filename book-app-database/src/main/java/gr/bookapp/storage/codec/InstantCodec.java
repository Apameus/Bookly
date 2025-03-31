package gr.bookapp.storage.codec;

import gr.bookapp.common.InstantFormatter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;

public record InstantCodec(LongCodec longCodec) implements Codec<Instant> {

    @Override
    public int maxByteSize() {
        return longCodec.maxByteSize();
    }

    @Override
    public Instant read(RandomAccessFile accessFile) throws IOException {
//        String date = longCodec.read(accessFile);
        Long epochSecondsDate = longCodec.read(accessFile);
        return InstantFormatter.parseLong(epochSecondsDate);
    }

    @Override
    public void write(RandomAccessFile accessFile, Instant obj) throws IOException {
//        String date = InstantFormatter.serialize(obj);
        Long epochSecondsDate = InstantFormatter.serializeLong(obj);
        longCodec.write(accessFile, epochSecondsDate);
    }
}
