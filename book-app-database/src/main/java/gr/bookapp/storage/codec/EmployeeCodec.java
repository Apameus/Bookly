package gr.bookapp.storage.codec;

import gr.bookapp.models.User;
import gr.bookapp.models.Role;

import java.io.IOException;
import java.io.RandomAccessFile;

public record EmployeeCodec(StringCodec stringCodec) implements Codec<User>{
    @Override
    public int maxByteSize() {
        return Long.BYTES + stringCodec.maxByteSize() * 3;
    }

    @Override
    public User read(RandomAccessFile accessFile) throws IOException {
        return new User(accessFile.readLong(), stringCodec.read(accessFile), stringCodec.read(accessFile), Role.valueOf(stringCodec.read(accessFile)));
    }

    @Override
    public void write(RandomAccessFile accessFile, User obj) throws IOException {
        accessFile.writeLong(obj.id());
        stringCodec.write(accessFile, obj.username());
        stringCodec.write(accessFile, obj.password());
        stringCodec.write(accessFile, obj.role().toString());
    }
}
