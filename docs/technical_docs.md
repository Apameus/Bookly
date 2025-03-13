# Technical Documentation

### Classes:
- <h3>Book:</h3>
<b>long</b> id,
<b>String</b> name,
<b>List\<String></b>Authors,
<b>List\<String></b> tags, 
<b>double</b> price, 
<b>long</b> releaseDate.

- <h3>Employee</h3>
<b>long</b> id,
<b>String</b> username,
<b>String</b> password.

- <h3>Offer</h3>
<b>long</b> id,
<b>List\<String></b> tags,
<b>int</b> percentage,
<b>long</b> untilDate.

- <h3>BookSales:</h3>
<b>long</b> bookID,
<b>long</b> sales.


### Data Structures:
- Main DB(id, book): Binary Search Tree
- Indexes: Hash Map 

### Indexes:
- Name 
- Author 
- Tag 
- Price
- Date 
<br>
--------> return <b>List\<dataBaseOffset></b> books


### Concerns:
- A Book can have multiple tags
- An Offer can have multiple tags
- ..When more than one tag of a single book has an offer, we apply the biggest