package gr.bookapp.services;

import gr.bookapp.AuditContext;
import gr.bookapp.exceptions.*;
import gr.bookapp.models.Book;
import gr.bookapp.models.Employee;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.EmployeeRepository;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final AuditRepository auditRepository;
    private final BookSalesService bookSalesService;
    private final OfferService offerService;

    public EmployeeService(EmployeeRepository employeeRepository, BookRepository bookRepository, AuditRepository auditRepository, OfferService offerService, BookSalesService bookSalesService) {
        this.auditRepository = auditRepository;
        this.bookSalesService = bookSalesService;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
        this.offerService = offerService;
    }

    public void createOffer(List<String> tags, int percentage, long untilDate) throws InvalidInputException {
        offerService.createOffer(tags, percentage, untilDate);
    }

    public Book sellBook(long bookID) throws  BookNotFoundException {
        var book = bookRepository.getBookByID(bookID);
        var offers = offerService.getOffers(book.tags());
        if (!offers.isEmpty()){
            Offer bestOffer = Collections.max(offers, Comparator.comparing(Offer::percentage));
            double price = book.price() - (book.price() * bestOffer.percentage() / 100.0 );
            book = book.withPrice(price);
        }
        bookSalesService.increaseSalesOfBook(bookID);
        auditRepository.audit(AuditContext.getEmployeeID(), "Book sailed", System.currentTimeMillis());
        return book;
    }

//    public void hireNewEmployee(String username, String password){
//        long id = employeeRepository.getEmployeeCount() + 1;
//        Employee employee = new Employee(id, username, password);
//        employeeRepository.add(employee);
//
//    }

}
