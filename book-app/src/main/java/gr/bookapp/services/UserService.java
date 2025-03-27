package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.exceptions.*;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.models.Employee;
import gr.bookapp.models.Offer;
import gr.bookapp.models.Role;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.EmployeeRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final AuditRepository auditRepository;
    private final BookSalesService bookSalesService;
    private final OfferService offerService;
    private final AuditContext auditContext;
    private final Clock clock;
    private final Logger logger;

    public EmployeeService(EmployeeRepository employeeRepository, BookRepository bookRepository, AuditRepository auditRepository, OfferService offerService, BookSalesService bookSalesService, AuditContext auditContext, Clock clock, Logger.Factory loggerFactory) {
        this.auditRepository = auditRepository;
        this.bookSalesService = bookSalesService;
        this.employeeRepository = employeeRepository;
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
        auditRepository.audit(auditContext.getUserID(), auditMsg, clock.instant());

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

    public void hireEmployee(String username, String password){
        logger.log("New employee hired");
        employeeRepository.add(new Employee(System.currentTimeMillis(), username, password));
    }


//    public void hireEmployee(String s, String s1) throws InvalidInputException {
//        employeeRepository.add(new Employee(System.currentTimeMillis(), s, s1, Role.ADMIN));
//    }
}
