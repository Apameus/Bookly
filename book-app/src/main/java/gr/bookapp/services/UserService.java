package gr.bookapp.services;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;
import gr.bookapp.models.User;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public final class UserService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookSalesService bookSalesService;
    private final OfferService offerService;
    private final Logger logger;

    public UserService(UserRepository userRepository, BookRepository bookRepository, OfferService offerService, BookSalesService bookSalesService, Logger.Factory loggerFactory) {
        this.userRepository = userRepository;
        this.bookSalesService = bookSalesService;
        this.bookRepository = bookRepository;
        this.offerService = offerService;
        logger = loggerFactory.create("User_Service");
    }

    public void createOffer(List<String> tags, int percentage, Duration duration) throws InvalidInputException {
        logger.log("Trial to create an offer..");
        offerService.createOffer(tags, percentage, duration);
    }


    public Book sellBook(long bookID) throws InvalidInputException {
        var book = bookRepository.getBookByID(bookID);
        if (book == null) throw new InvalidInputException("BookId doesn't exist");
        var offers = offerService.getOffers(book.tags());
        Offer bestOffer = null;
        if (!offers.isEmpty()){
            bestOffer = bestOffer(offers);
            double price = book.price() - (book.price() * bestOffer.percentage() / 100.0 );
            book = book.withPrice(price);
        }
        bookSalesService.increaseSalesOfBook(bookID);

        logger.log("Book with name %s is sold", book.name());
        return book;
    }

    private Offer bestOffer(List<Offer> offers) {
        Offer bestOffer = null;
        for (Offer offer : offers) {
            if (offer.expirationDate().isBefore(Instant.now())) continue; //TODO: Not testable ?!
            if (bestOffer == null){
                bestOffer = offer;
                continue;
            }
            if (offer.percentage() > bestOffer.percentage()) bestOffer = offer;
        }
        if (bestOffer == null) logger.log("No offer exist");
        else logger.log("Offer exist with %s percentage",bestOffer.percentage());
        return bestOffer;
    }

    public boolean hasAdminAccount() {
        return userRepository.adminExist();
    }

}
