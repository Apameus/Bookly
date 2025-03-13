# High Level Architecture
```mermaid
flowchart LR
    UI --> AS[AuthService] --> ER[EmployeeRepo] --> EDB[EmployeeDB]
    UI --> BS[BookService] --> BR[BookRepo] --> BDB[BookDB]
    BS --> OS
    UI --> ES[EmployeeService] --> SS[SalesService] --> SR[SalesRepo] --> SDB[SalesDB]
    ES --> OS[OfferService] --> OR[OfferRepo] --> ODB[OfferDB]
    
    *Services --> AUS[AuditService] --> AUR[AuditRepo] --> ADB[AuditDB]
```

[//]: # (- Server)

[//]: # (```mermaid)

[//]: # (flowchart LR)

[//]: # (    TCP_Server --> Controller --> Service --> Repo --> Files)

[//]: # (```)

## Audit:
```mermaid

sequenceDiagram
    Service ->> AuditService: note( employee, method )
    AuditService ->> AuditRepo: ..
    AuditRepo ->> AuditDB: ..
```

## Add-Modify-Remove  book:
```mermaid
sequenceDiagram
    actor U as User
    participant ES as EmployeeService
    participant BS as BookService
    participant BR as BookRepo
    participant DB as BookDataBase
    U ->> ES: addBook(name, author, tags, price, date)
    ES ->> BS: addBook( .. )
    BS ->> BR: addBook( .. )
    BR ->> DB: insert( book )
    
```
[*Audit update](#audit)


## Search book:
```mermaid
sequenceDiagram
    actor U as User
    participant ES as EmployeeService
    participant BS as BookService
    participant BR as BookRepo
    participant DB as BookDataBase
    U ->> ES: findBooksBy( .. )
    ES ->> BS: getBooksBy( .. )
    BS ->> BR: getBooksBy( .. )
    BR ->> DB: getBooksBy( .. )
    DB -->> BS: List<Book> books
    alt books == null
        BS -->> U: Book doesn't exist
    end
    BS -->> U: books
```
```mermaid
sequenceDiagram
    participant ES as EmployeeService
    participant OS as OfferService
    participant OR as OfferRepo
    participant ODB as OfferDataBase
    ES ->> OS: ..
    OS ->> OR: ..
    OR ->> ODB: ..
    ODB -->> ES: sale
```
[*Audit update](#audit)




## Search book by Price:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant DB as BookDataBase
    U ->> BS: getBooksFrom(minPrice, maxPrice)
    BS ->> BR: getBooksFrom( .. )
    BR ->> DB: retrieveBooksFrom( .. )
    DB -->> BS: List<Book> books
    alt books == null
        BS -->> U: Couldn't find books from this price range
    end
    BS -->> U: List<Book> books

```
[*Audit update](#audit)

## Search book by Date:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant DB as BookDataBase
    U ->> BS: getBooks(fromDate, toDate)
    BS ->> BR: getBooks(fromDate, toDate)
    BR ->> DB: retrieveBooksBy(fromDate, toDate)
    DB -->> BS: List<Book> books
    alt books == null
        BS -->> U: Couldn't find books from this dates
    end
    BS -->> U: List<Book> books

```
[*Audit update](#audit)

## Sell Book:
```mermaid
sequenceDiagram
    actor U as User
    participant ES as EmployeeService
    participant BS as BookService
    participant BR as BookRepo
    participant DB as BookDataBase
    U ->> ES: SearchBookBy( .. )
    ES ->> BS: ..
    BS ->> BR: ..
    BR ->> DB: retrieve( .. )
    DB ->> ES: book
```
```mermaid
sequenceDiagram
    participant ES as EmployeeService
    participant BS as BookSalesService
    participant BR as BookSalesRepo
    participant BDB as BookSalesDataBase
    ES ->> BS: updateBookQuantity(bookID, qnt)
    BS ->> BR: ..
    BR ->> BDB: ..
```
```**Check if the book has an offer``` <br>
[*Audit update](#audit)

## Create Offer:
```mermaid
sequenceDiagram
    actor  U as User
    participant ES as EmployeeService
    participant OS as OfferService
    participant OR as OfferRepo
    participant ODB as OfferDataBase
    U ->> ES: createOffer( Tag, Duration, Discount )
    ES ->> OS: createOffer( .. )
    alt percentage <=0 || date < now 
            OS -->> ES: InvalidInputException
    end
    OS ->> OR: getOfferCount( )
    OR -->> OS: offerCount
    OS ->> OR: add(offer)
    OR ->> ODB: insert(offer)
```
[*Audit update](#audit)
