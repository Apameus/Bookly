package gr.bookapp.protocol;

import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;
import gr.bookapp.protocol.codec.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import static gr.bookapp.protocol.Response.*;

public final class ResponseStreamCodec {
    private final ListCodec<Book> listBookCodec;
    private final ListCodec<Offer> listOfferCodec;
    private final StringCodec stringCodec = new StringCodec();

    public ResponseStreamCodec() {
        ListCodec<String> listCodec = new ListCodec<>(stringCodec);
        InstantCodec instantCodec = new InstantCodec();
        BookCodec bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
        listBookCodec = new ListCodec<Book>(bookCodec);
        OfferCodec offerCodec = new OfferCodec(listCodec, instantCodec);
        listOfferCodec = new ListCodec<Offer>(offerCodec);
    }

    public void serialize(DataOutput dataOutput, Response response) throws IOException {
        switch (response){
            case AuthenticateResponse() -> { dataOutput.writeByte(AuthenticateResponse.TYPE); }
            case GeneralSuccessResponse() -> { dataOutput.writeByte(GeneralSuccessResponse.TYPE); }
            case GetBooksResponse(List<Book> books) -> {
                dataOutput.writeByte(GetBooksResponse.TYPE);
                listBookCodec.write(dataOutput, books);
            }
            case GetOffersResponse(List<Offer> offers) -> {
                dataOutput.writeByte(GetOffersResponse.TYPE);
                listOfferCodec.write(dataOutput, offers);
            }
            case ErrorResponse(String error) -> {
                dataOutput.writeByte(ErrorResponse.TYPE);
                stringCodec.write(dataOutput, error);
            }
        }
    }

    public Response parse(DataInput dataInput) throws IOException {
        byte type = dataInput.readByte();
        return switch (type){
            case AuthenticateResponse.TYPE -> {
                yield new AuthenticateResponse();
            }
            case GeneralSuccessResponse.TYPE -> {
                yield new GeneralSuccessResponse();
            }
            case ErrorResponse.TYPE -> {
                String error = stringCodec.read(dataInput);
                yield new ErrorResponse(error);
            }
            case GetBooksResponse.TYPE -> {
                List<Book> books = listBookCodec.read(dataInput);
                yield new GetBooksResponse(books);
            }
            case GetOffersResponse.TYPE -> {
                List<Offer> offers = listOfferCodec.read(dataInput);
                yield new GetOffersResponse(offers);
            }
            default -> throw new IllegalStateException("Unknown Response");
        };
    }
}
