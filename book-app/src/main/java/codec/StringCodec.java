package codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class StringCodec implements Codec<String>{
    private final byte maxStringLength = 40;

    @Override
    public int maxByteSize() {
        return Byte.BYTES + maxStringLength;
    }

    @Override
    public String read(RandomAccessFile accessFile) throws IOException {
        int stringLength = accessFile.read();
        if (stringLength == 0) return "";
        byte[] bytes = new byte[stringLength];
        accessFile.read(bytes);
        int bytesToSkip = maxStringLength - stringLength;
        accessFile.skipBytes(bytesToSkip);
        return new String(bytes);
    }

    @Override
    public void write(RandomAccessFile accessFile, String string) throws IOException {
        if (string.length() > 40) throw new IllegalStateException("String length can't be more than 40 characters");

        accessFile.write(string.length());
        accessFile.write(string.getBytes());

        int bytesToSkip = maxStringLength - string.length();
        accessFile.skipBytes(bytesToSkip);
    }
}
