package gr.bookapp.protocol.codec;

import gr.bookapp.common.InstantFormatter;
import java.io.*;
import java.time.Instant;

public record InstantCodec( ) implements StreamCodec<Instant> {

    @Override
    public int maxByteSize() {
        return Long.BYTES;
    }

    @Override
    public Instant read(DataInput dataInput) throws IOException {
        Long epochSecondsDate = dataInput.readLong();
        return InstantFormatter.parseLong(epochSecondsDate);
    }

    @Override
    public void write(DataOutput dataOutput, Instant obj) throws IOException {
        Long epochSecondsDate = InstantFormatter.serializeLong(obj);
        dataOutput.writeLong(epochSecondsDate);
    }
}
