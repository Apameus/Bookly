package accessFile.codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateCodec implements Codec<LocalDate>{ //TODO
    private LocalDate date;

    public DateCodec() {
    }

    @Override
    public int maxByteSize() {
        return 10;
    }

    @Override
    public LocalDate read(RandomAccessFile accessFile) throws IOException {
        byte[] dateBytes = new byte[10];
        accessFile.read(dateBytes);
        String str = new String(dateBytes);
        LocalDate date = LocalDate.parse(str, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return date;
    }

    @Override
    public void write(RandomAccessFile accessFile, LocalDate date) throws IOException {
        String dateString = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        byte[] dateBytes = dateString.getBytes();
        accessFile.write(dateBytes);
    }
}
