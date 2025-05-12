package gr.bookapp.protocol.codec;

import java.io.*;

public record StringCodec(int maxStringLength) implements StreamCodec<String> {
    public StringCodec() {
        this(40);
    }

    @Override
    public int maxByteSize() {
        return Integer.BYTES + maxStringLength;
    }

    @Override
    public String read(DataInput dataInput) throws IOException {
        int stringLength = dataInput.readInt();
        if (stringLength == 0) return "";
        byte[] bytes = new byte[stringLength];
        dataInput.readFully(bytes);

        return new String(bytes);
    }

    @Override
    public void write(DataOutput dataOutput, String string) throws IOException {
        if (string.length() > maxStringLength) throw new IllegalStateException(String.format("String length can't be more than %s characters", maxStringLength));

        dataOutput.writeInt(string.length());
        dataOutput.write(string.getBytes());
    }

}
