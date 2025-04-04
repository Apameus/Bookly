package gr.bookapp.protocol.codec;

import gr.bookapp.models.BookSales;

import java.io.*;

public record BookSalesCodec() implements StreamCodec<BookSales> {

    @Override
    public int maxByteSize() {
        return Long.BYTES * 2;
    }

    @Override
    public BookSales read(DataInput dataInput) throws IOException {
        return new BookSales(dataInput.readLong(), dataInput.readLong());
    }

    @Override
    public void write(DataOutput dataOutput, BookSales obj) throws IOException {
        dataOutput.writeLong(obj.bookID());
        dataOutput.writeLong(obj.sales());
    }
}
