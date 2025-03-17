package gr.bookapp.storage.codec;

import gr.bookapp.models.Audit;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;

public record AuditCodec(StringCodec stringCodec, InstantCodec instantCodec) implements Codec<Audit> {
    @Override
    public int maxByteSize() {
        return Long.BYTES + stringCodec.maxByteSize() + instantCodec.maxByteSize();
    }

    @Override
    public Audit read(RandomAccessFile accessFile) throws IOException {
        long employeeId = accessFile.readLong();
        String action = stringCodec.read(accessFile);
        Instant time = instantCodec.read(accessFile);
        return new Audit(employeeId, action, time);
    }

    @Override
    public void write(RandomAccessFile accessFile, Audit obj) throws IOException {
        accessFile.writeLong(obj.employeeID());
        stringCodec.write(accessFile, obj.action());
        instantCodec.write(accessFile, obj.time());
    }
}
