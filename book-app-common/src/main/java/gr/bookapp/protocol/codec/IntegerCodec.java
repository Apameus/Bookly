package gr.bookapp.protocol.codec;

import java.io.*;

public record IntegerCodec() implements StreamCodec<Integer> {
    @Override
    public int maxByteSize() {
        return Integer.BYTES;
    }

    @Override
    public Integer read(DataInput dataInput) throws IOException {
        return dataInput.readInt();
    }

    @Override
    public void write(DataOutput dataOutput, Integer obj) throws IOException {
        dataOutput.writeInt(obj);
    }
}
