package gr.bookapp.protocol.codec;

import java.io.*;

public record LongCodec() implements StreamCodec<Long> {

    @Override
    public int maxByteSize() {
        return Long.BYTES;
    }

    @Override
    public Long read(DataInput dataInput) throws IOException {
        return dataInput.readLong();
    }

    @Override
    public void write(DataOutput dataOutput, Long obj) throws IOException {
        dataOutput.writeLong(obj);
    }
}
