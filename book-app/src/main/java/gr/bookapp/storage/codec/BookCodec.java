package gr.bookapp.storage.codec;

import gr.bookapp.models.Book;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.util.List;

public record BookCodec(StringCodec stringCodec, ListCodec<String> listCodec, InstantCodec instantCodec) implements Codec<Book> {
    @Override
    public int maxByteSize() {
        return Long.BYTES * 2 + Double.BYTES + stringCodec.maxByteSize() + listCodec().maxByteSize() * 2;
    }

    @Override
    public Book read(RandomAccessFile accessFile) throws IOException {
        long id = accessFile.readLong();
        String name = stringCodec.read(accessFile);
        List<String> authors = listCodec.read(accessFile);
        double price = accessFile.readDouble();
        Instant releaseDate = instantCodec.read(accessFile);
        List<String> tags = listCodec.read(accessFile);
        return new Book(id, name, authors, price, releaseDate, tags);
    }

    @Override
    public void write(RandomAccessFile accessFile, Book obj) throws IOException {
        accessFile.writeLong(obj.id());
        stringCodec.write(accessFile, obj.name());
        listCodec.write(accessFile, obj.authors());
        accessFile.writeDouble(obj.price());
        instantCodec.write(accessFile, obj.releaseDate());
        listCodec.write(accessFile, obj.tags());
    }
}
