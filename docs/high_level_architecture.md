# High Level Architecture
```mermaid
flowchart LR
    UI --> AS[AuthService] --> AR[AuthRepo] --> EDB[EmployeeDB]
    UI --> BS[BookService] --> BR[BookRepo] --> BDB[BookDB]
    BS --> OS
    ES --> OS[OfferService] --> OR[OfferRepo] --> ODB[OfferDB]
    UI --> ES[EmployeeService] --> SS[SalesService] --> SR[SalesRepo] --> SDB[SalesDB]
    
    *Services --> AUS[AuditService] --> AUR[AuditRepo] --> ADB[AuditDB]
```

[//]: # (- Server)

[//]: # (```mermaid)

[//]: # (flowchart LR)

[//]: # (    TCP_Server --> Controller --> Service --> Repo --> Files)

[//]: # (```)


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
```mermaid
sequenceDiagram
    EmployeeService ->> AuditService: note( employee, addBook )
    AuditService ->> AuditRepo: ..
    AuditRepo ->> AuditDB: ..
```

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
    participant SS as SalesService
    participant SR as SalesRepo
    participant SDB as SalesDataBase
    ES ->> SS: ..
    SS ->> SR: ..
    SR ->> SDB: ..
    SDB -->> ES: sale
```
``` *Audit Update ```





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
``` *Audit Update ```

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
``` *Audit Update ```

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
    participant SS as StatsService
    participant SR as StatsRepo
    participant SDB as StatsDataBase
    ES ->> SS: updateBookQuantity(bookID, qnt)
    SS ->> SR: ..
    SR ->> SDB: ..
```
``` *Audit Update ```

## Create Sale:
```mermaid
sequenceDiagram
    actor  U as User
    participant SS as SalesService
    participant SR as SalesRepo
    participant SDB as SalesDataBase
    U ->> SS: createSale( Tag, Duration, Discount )
    SS ->> SR: ..
    SR ->> SDB: ..
```
``` *Audit Update ```
