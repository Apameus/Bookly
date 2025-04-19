package gr.bookapp.storage.codec;

import gr.bookapp.Audit;
import gr.bookapp.protocol.codec.InstantCodec;
import gr.bookapp.protocol.codec.StreamCodec;
import gr.bookapp.protocol.codec.StringCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;

public record AuditCodec(StringCodec stringCodec, InstantCodec instantCodec) implements StreamCodec<Audit> {
    @Override
    public int maxByteSize() {
        return Long.BYTES + stringCodec.maxByteSize() + instantCodec.maxByteSize();
    }

    @Override
    public Audit read(DataInput dataInput) throws IOException {
        long employeeId = dataInput.readLong();
        String action = stringCodec.read(dataInput);
        Instant time = instantCodec.read(dataInput);
        return new Audit(employeeId, action, time);
    }

    @Override
    public void write(DataOutput dataOutput, Audit obj) throws IOException {
        dataOutput.writeLong(obj.employeeID());
        stringCodec.write(dataOutput, obj.action());
        instantCodec.write(dataOutput, obj.time());
    }
}
