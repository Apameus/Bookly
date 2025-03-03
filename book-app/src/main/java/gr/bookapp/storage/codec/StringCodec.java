package gr.bookapp.storage.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;

public record StringCodec(int maxStringLength) implements Codec<String> {
    public StringCodec() {
        this(40);
    }

    @Override
    public int maxByteSize() {
        return Byte.BYTES + maxStringLength;
    }

    @Override
    public String read(RandomAccessFile accessFile) throws IOException {
        int stringLength = accessFile.readByte();
        if (stringLength == 0) return "";
        byte[] bytes = new byte[stringLength];
        accessFile.readFully(bytes);
        int bytesToSkip = maxStringLength - Byte.BYTES - stringLength;
        accessFile.skipBytes(bytesToSkip);

        return new String(bytes);
    }

    @Override
    public void write(RandomAccessFile accessFile, String string) throws IOException {
        if (string.length() > maxStringLength) throw new IllegalStateException(String.format("String length can't be more than %s characters", maxStringLength));

        accessFile.write(string.length());
        accessFile.write(string.getBytes());

        int bytesToSkip = maxStringLength - Byte.BYTES - string.length();
        accessFile.skipBytes(bytesToSkip);
    }
}
