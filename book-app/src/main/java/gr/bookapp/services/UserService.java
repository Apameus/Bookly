package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.exceptions.*;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;
import gr.bookapp.models.User;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.UserRepository;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

public final class UserService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final AuditRepository auditRepository;
    private final BookSalesService bookSalesService;
    private final OfferService offerService;
    private final AuditContext auditContext;
    private final Clock clock;
    private final Logger logger;

    public UserService(UserRepository userRepository, BookRepository bookRepository, AuditRepository auditRepository, OfferService offerService, BookSalesService bookSalesService, AuditContext auditContext, Clock clock, Logger.Factory loggerFactory) {
        this.auditRepository = auditRepository;
        this.bookSalesService = bookSalesService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.offerService = offerService;
        this.auditContext = auditContext;
        this.clock = clock;
        logger = loggerFactory.create("Employee_Service");
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

        String auditMsg;
        if (bestOffer == null) auditMsg = "Book with id: %s sold".formatted(bookID);
        else auditMsg = "Book with id: %s sold with extra offer of: %s from offer with id: %s".formatted(bookID, bestOffer.percentage(), bestOffer.offerID());
        auditRepository.audit(auditContext.getEmployeeID(), auditMsg, clock.instant());

        logger.log("Book with name %s is sold", book.name());
        return book;
    }

    private Offer bestOffer(List<Offer> offers) {
        Offer bestOffer = null;
        for (Offer offer : offers) {
            if (offer.untilDate().isBefore(clock.instant())) continue;
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

    public boolean hasAdminAccount(){
        return userRepository.getAll().stream().anyMatch(User::isAdmin);
    }

}
