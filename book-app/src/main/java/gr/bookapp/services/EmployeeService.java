package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.exceptions.*;
import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;
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

    public EmployeeService(EmployeeRepository employeeRepository, BookRepository bookRepository, AuditRepository auditRepository, OfferService offerService, BookSalesService bookSalesService, AuditContext auditContext, Clock clock) {
        this.auditRepository = auditRepository;
        this.bookSalesService = bookSalesService;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
        this.offerService = offerService;
        this.auditContext = auditContext;
        this.clock = clock;
    }

    public void createOffer(List<String> tags, int percentage, long durationInDays) throws InvalidInputException {
        offerService.createOffer(tags, percentage, durationInDays);
    }

    /**
     *
     * @param bookID
     * @return Book
     */
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

        return bestOffer;
    }

//    public void hireNewEmployee(String username, String password){
//        long id = employeeRepository.getEmployeeCount() + 1;
//        Employee employee = new Employee(id, username, password);
//        employeeRepository.add(employee);
//
//    }

}
