package gr.bookapp.storage.codec;

import gr.bookapp.models.Employee;

import java.io.IOException;
import java.io.RandomAccessFile;

public record EmployeeCodec(StringCodec stringCodec) implements Codec<Employee>{
    @Override
    public int maxByteSize() {
        return Long.BYTES + stringCodec.maxByteSize() * 2;
    }

    @Override
    public Employee read(RandomAccessFile accessFile) throws IOException {
        return new Employee(accessFile.readLong(), stringCodec.read(accessFile), stringCodec.read(accessFile));
    }

    @Override
    public void write(RandomAccessFile accessFile, Employee obj) throws IOException {
        accessFile.writeLong(obj.id());
        stringCodec.write(accessFile, obj.username());
        stringCodec.write(accessFile, obj.password());
    }
}
