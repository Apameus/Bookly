# High Level Architecture
```mermaid
flowchart LR
    UI --> BS[BookService] --> BR[BookRepo] --> I[Indexes] --> DB[BookDB] 
```

[//]: # (- Server)

[//]: # (```mermaid)

[//]: # (flowchart LR)

[//]: # (    TCP_Server --> Controller --> Service --> Repo --> Files)

[//]: # (```)


## Add new book:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant I as Indexes
    participant DB as BookDataBase
    U ->> BS: addBook(name, author, tags, price, date)
    BS ->> BR: addBook( >> )
    BR ->> DB: insert( book )
    DB ->> I: updateIndexes( book, offset )
```

## Search book by name:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant DB as BookDataBase
    U ->> BS: getBooksBy(name)
    BS ->> BR: getBooksBy(name)
    BR ->> DB: getBooksBy(name)
    DB -->> BS: List<Book>  books
    alt books == null
        BS -->> U: Book doesn't exist
    end
    BS -->> U: books
```

## Search book by author:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant I as Indexes
    participant DB as BookDateBase
    U ->> BS: getBooksBy(author)
    BS ->> BR: getBooksBy(author)
    BR->> I: getBooksFromIndex(Author)
    I -->> BR: List<Long> databaseOffsets
        alt dataBaseOffsets.length == 0
            BR -->> U: Couldn't find books from this Author
        end
    BR ->> DB: readBooks(databaseOffsets)
    DB -->> U: List<Book> books

```

## Search book by Tags:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant I as Index
    participant DB as BookDataBase
    U ->> BS: getBook(tags)
    BS ->> BR: getBook(tags)
    BR ->> I: getBooksFromIndex(Tag)
    I ->> BR: List<Long> databaseOffsets
    alt databaseOffsets == null
        BR ->> U: Couldn't find books from this tags    
    end
    BR ->> DB: readBooks(databaseOffsets)
    DB -->> U: List<Book> books

```

## Search book by Price:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant I as Index
    participant DB as BookDataBase
    U ->> BS: getBook(priceRange)
    BS ->> BR: getBook(priceRange)
    BR ->> I: getBooksFromIndex(priceRange)
    I ->> BR: List<Long> databaseOffsets
    alt databaseOffsets == null
        BR ->> U: Couldn't find books from this priceRange    
    end
    BR ->> DB: readBooks(databaseOffsets)
    DB -->> U: List<Book> books

```

## Search book by Date:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant I as Index
    participant DB as BookDataBase
    U ->> BS: getBook(date)
    BS ->> BR: getBook(date)
    BR ->> I: getBooksFromIndex(date)
    I ->> BR: List<Long> databaseOffsets
    alt databaseOffsets == null
        BR ->> U: Couldn't find books from this date    
    end
    BR ->> DB: readBooks(databaseOffsets)
    DB -->> U: List<Book> books

```

