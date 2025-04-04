package gr.bookapp.protocol.codec;

import java.io.*;

public record DoubleCodec() implements StreamCodec<Double> {
    @Override
    public int maxByteSize() {
        return Double.BYTES;
    }

    @Override
    public Double read(DataInput dataInput) throws IOException {
        return dataInput.readDouble();
    }

    @Override
    public void write(DataOutput dataOutput, Double obj) throws IOException {
        dataOutput.writeDouble(obj);
    }
}
