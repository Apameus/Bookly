### High Level Architecture
```mermaid
flowchart LR
    UI --> BS[BookService] --> BR[BookRepo] --> Files
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
    participant F as Files
    U ->> BS: addBook(name, author, tags, price date)
    BS ->> BR: addBook( >> )
    BR ->> F: .
```

## Search book by name:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant F as Files
    U ->> BS: getBook(name)
    BS ->> BR: getBook(name)
    BR ->> F: getBook(name)
    F  -->> BS: List<Book>  books
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
    participant F as Files
    U ->> BS: getBook(author)
    BS ->> BR: getBook(author)
    BR ->> F: getBook(author)
    F ->> F: getDatabaseOffsetsOf(Author)
    F ->> BR: List<Long> databaseOffsets
    alt databaseOffset == null
        BR ->> U: Couldn't find books from this Author    
    end
    BR ->> BR: readBooks(databaseOffsets)
    BR -->> BS: List<Book> books
    BS -->> U: books

```

## Search book by Tags:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant F as Files
    U ->> BS: getBook(tags)
    BS ->> BR: getBook(tags)
    BR ->> F: getBook(tags)
    F ->> F: getDatabaseOffsetsOf(tags)
    F ->> BR: List<Long> databaseOffsets
    alt databaseOffset == null
        BR ->> U: Couldn't find books from this tags    
    end
    BR ->> BR: readBooks(databaseOffsets)
    BR -->> BS: List<Book> books
    BS -->> U: books

```

## Search book by Price:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant F as Files
    U ->> BS: getBook(priceRange)
    BS ->> BR: getBook(priceRange)
    BR ->> F: getBook(priceRange)
    F ->> F: getDatabaseOffsetsOf(priceRange)
    F ->> BR: List<Long> databaseOffsets
    alt databaseOffset == null
        BR ->> U: Couldn't find books from this priceRange    
    end
    BR ->> BR: readBooks(databaseOffsets)
    BR -->> BS: List<Book> books
    BS -->> U: books

```

## Search book by Date:
```mermaid
sequenceDiagram
    actor U as User
    participant BS as BookService
    participant BR as BookRepo
    participant F as Files
    U ->> BS: getBook(date)
    BS ->> BR: getBook(date)
    BR ->> F: getBook(date)
    F ->> F: getDatabaseOffsetsOf(date)
    F ->> BR: List<Long> databaseOffsets
    alt databaseOffset == null
        BR ->> U: Couldn't find books from this date    
    end
    BR ->> BR: readBooks(databaseOffsets)
    BR -->> BS: List<Book> books
    BS -->> U: books

```

