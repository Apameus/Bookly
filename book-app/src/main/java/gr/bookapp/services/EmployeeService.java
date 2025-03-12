package gr.bookapp.services;

import gr.bookapp.exceptions.*;
import gr.bookapp.models.Book;
import gr.bookapp.models.Employee;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.BookSalesRepository;
import gr.bookapp.repositories.EmployeeRepository;

import java.util.List;

public final class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final BookSalesService bookSalesService;
    private final OfferService offerService;

    public EmployeeService(EmployeeRepository employeeRepository, BookRepository bookRepository, OfferService offerService, BookSalesService bookSalesService) {
        this.bookSalesService = bookSalesService;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
        this.offerService = offerService;
    }

    // create Offer
    public void createOffer(Offer offer){}

    public void createOffer(List<String> tags, int percentage, long untilDate) throws OfferDurationException, InvalidInputException, TagAlreadyOnOfferException {
        offerService.createOffer(tags, percentage, untilDate);
    }

    // sell a book
    public void sellBook(long bookID) throws InvalidQuantityException, BookNotFoundException {
        //TODO  //check if the book has an offer
        Book book = bookRepository.getBookByID(bookID);
        List<Offer> offers = offerService.getOffers(book.tags());
        //TODO //calculate the new price of the book

        bookSalesService.increaseSalesOfBook(bookID);
    }

    public void hireNewEmployee(String username, String password){
        long id;
        Employee employee = new Employee(id, username, password);
        employeeRepository.add(employee);
    }
}
