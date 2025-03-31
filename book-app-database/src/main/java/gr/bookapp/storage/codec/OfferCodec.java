package gr.bookapp.storage.codec;

import gr.bookapp.models.Offer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.util.List;

public record OfferCodec(ListCodec<String> listCodec, InstantCodec instantCodec) implements Codec<Offer> {

    @Override
    public int maxByteSize() {
        return Long.BYTES + listCodec.maxByteSize() + Integer.BYTES + instantCodec.maxByteSize();
    }

    @Override
    public Offer read(RandomAccessFile accessFile) throws IOException {
        long id = accessFile.readLong();
        List<String> tags = listCodec.read(accessFile);
        int percentage = accessFile.readInt();
        Instant untilDate = instantCodec.read(accessFile);
        return new Offer(id, tags, percentage, untilDate);
    }

    @Override
    public void write(RandomAccessFile accessFile, Offer obj) throws IOException {
        accessFile.writeLong(obj.offerID());
        listCodec.write(accessFile, obj.tags());
        accessFile.writeInt(obj.percentage());
        instantCodec.write(accessFile, obj.untilDate());
    }
}
