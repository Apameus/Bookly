package gr.bookapp.protocol.codec;

import gr.bookapp.models.Offer;

import java.io.*;
import java.time.Instant;
import java.util.List;

public record OfferCodec(ListCodec<String> listCodec, InstantCodec instantCodec) implements StreamCodec<Offer> {

    @Override
    public int maxByteSize() {
        return Long.BYTES + listCodec.maxByteSize() + Integer.BYTES + instantCodec.maxByteSize();
    }

    @Override
    public Offer read(DataInput dataInput) throws IOException {
        long id = dataInput.readLong();
        List<String> tags = listCodec.read(dataInput);
        int percentage = dataInput.readInt();
        Instant untilDate = instantCodec.read(dataInput);
        return new Offer(id, tags, percentage, untilDate);
    }

    @Override
    public void write(DataOutput dataOutput, Offer obj) throws IOException {
        dataOutput.writeLong(obj.offerID());
        listCodec.write(dataOutput, obj.tags());
        dataOutput.writeInt(obj.percentage());
        instantCodec.write(dataOutput, obj.untilDate());
    }
}
