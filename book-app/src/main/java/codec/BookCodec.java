package codec;

import model.Book;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public final class BookCodec implements Codec<Book>{ //TODO
    StringCodec stringCodec = new StringCodec();

    @Override
    public int maxByteSize() {
        return Long.BYTES + stringCodec.maxByteSize() + stringCodec.maxByteSize() + Double.BYTES ;
    }

    @Override
    public Book read(RandomAccessFile accessFile) throws IOException {
        return null;
    }

    @Override
    public void write(RandomAccessFile accessFile, Book book) throws IOException {

    }
}
