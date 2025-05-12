package gr.bookapp.protocol.codec;

import gr.bookapp.models.Role;
import gr.bookapp.models.User;

import java.io.*;

public record UserCodec(StringCodec stringCodec) implements StreamCodec<User> {
    @Override
    public int maxByteSize() {
        return Long.BYTES + stringCodec.maxByteSize() * 3;
    }

    @Override
    public User read(DataInput dataInput) throws IOException {
        return new User(dataInput.readLong(), stringCodec.read(dataInput), stringCodec.read(dataInput), Role.valueOf(stringCodec.read(dataInput)));
    }

    @Override
    public void write(DataOutput dataOutput, User obj) throws IOException {
        dataOutput.writeLong(obj.id());
        stringCodec.write(dataOutput, obj.username());
        stringCodec.write(dataOutput, obj.password());
        stringCodec.write(dataOutput, obj.role().toString());
    }
}
