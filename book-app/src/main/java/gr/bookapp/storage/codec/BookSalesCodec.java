package gr.bookapp.storage.codec;

import gr.bookapp.models.BookSales;

import java.io.IOException;
import java.io.RandomAccessFile;

public record BookSalesCodec() implements Codec<BookSales> {

    @Override
    public int maxByteSize() {
        return Long.BYTES * 2;
    }

    @Override
    public BookSales read(RandomAccessFile accessFile) throws IOException {
        return new BookSales(accessFile.readLong(), accessFile.readLong());
    }

    @Override
    public void write(RandomAccessFile accessFile, BookSales obj) throws IOException {
        accessFile.writeLong(obj.bookID());
        accessFile.writeLong(obj.sales());
    }
}
