package gr.bookapp.protocol.codec;

import gr.bookapp.models.Book;

import java.io.*;
import java.time.Instant;
import java.util.List;

public record BookCodec(StringCodec stringCodec, ListCodec<String> listCodec, InstantCodec instantCodec) implements StreamCodec<Book> {
    @Override
    public int maxByteSize() {
        return Long.BYTES * 2 + Double.BYTES + stringCodec.maxByteSize() + listCodec().maxByteSize() * 2;
    }

    @Override
    public Book read(DataInput dataInput) throws IOException {
        long id = dataInput.readLong();
        String name = stringCodec.read(dataInput);
        List<String> authors = listCodec.read(dataInput);
        double price = dataInput.readDouble();
        Instant releaseDate = instantCodec.read(dataInput);
        List<String> tags = listCodec.read(dataInput);
        return new Book(id, name, authors, price, releaseDate, tags);
    }

    @Override
    public void write(DataOutput dataOutput, Book obj) throws IOException {
        dataOutput.writeLong(obj.id());
        stringCodec.write(dataOutput, obj.name());
        listCodec.write(dataOutput, obj.authors());
        dataOutput.writeDouble(obj.price());
        instantCodec.write(dataOutput, obj.releaseDate());
        listCodec.write(dataOutput, obj.tags());
    }
}
