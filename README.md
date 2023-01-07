# CLI App for Managing Orders with Java JDBC DB

```bash
java -jar target/orders.jar
```

## Available Scripts

### `get`

Displays information about an order.
It takes the ID of the order to display as argument.

```shell
Enter command: get 1

Order ID: 1
CustomerID: 1
Status: created
Date: 2012-09-17 00:00:00.0
```

### `update`

Updates the status of an order. It takes two arguments, the ID of the order to update and the status (
`created`,`paid`,`canceled`).

```shell
Enter command: update 1 PAID

Rows affected: 1
```

### `delete`

Deletes an order. It takes a variable number of arguments, representing the IDs of the orders to delete.

```shell
Enter command: delete 20 35 41

Rows affected: 3
```

### `insert`

Inserts a new order. It doesn't take more arguments, however, the application will prompt for all the information of the
new order after issuing this command.

```shell
Enter command: insert

Customer ID:
```

### `total`

Gives the total amount of all paid orders of a customer. It takes the ID of the customer as argument.

```shell
Enter command: total 1

Total: 29.97
```

### `exit`

Exits the application

```shell
Enter command: exit

Process finished with exit code 0
```

### `help`

Displays usage instructions

## Development Set up

* Java 11
* IntelliJ

## Database Interaction with DAO and DTO

In any application which is going to interact with the database, there is need of performing CRUD operations on the
database tables and since the table operations may be done by different classes and hence it becomes cumbersome to
repeat the same code in various classes. Moreover, even after repeating the code, it becomes difficult to maintain the
daatbase interaction code whenever changes are required in the way database interaction is being done.

DAO is a class that usually has the CRUD operations like save, update, delete.

DTO is just an object that holds data. It
is JavaBean with instance variables and setter and getters.

The DTO is used to expose several values in a bean like fashion. This provides a light-weight mechanism to transfer
values over a network or between different application tiers.

DTO will be passed as value object to DAO layer and DAO layer will use this object to persist data using its CRUD
operation methods.

Always remember that the DAO, DTO, Factory, Loose Coupling (A SOLID principle), Factory design patterns all go along.
One can also create a connection pool while using the DAO design pattern. The important point is that the code becomes
clean and easy to understand when using the DAO, DTO design patterns.

## H2 In Memory Database

Database interaction with DAO and DTO design patterns and perform CRUD operation ( Create , Read , Update , Delete )
using prepared statement.

* ### Connect to the database
    * Add a field for the database connection URL
        * `"jdbc:h2:mem:orders;DB_CLOSE_DELAY=-1"`
    * Add field for the database user
        * `"sa"`
    * Add field for db password
        * `"""`
    * Use DriverManager class to connect to the database
    * `database.getConnection()`

### DAO-DTO and Statement Logic

<details>
<summary>More</summary>

* **DAO**: "Data Access Object"
    * encapsulates the database access
    * A DAO design pattern helps an application to perform various CRUD operations on the database.
    * The DAO classes provide methods for insertion, deletion, update and finder methods.
    * The basic purpose of creating DAO is the loose coupling and non-repetition of code.
    * It is less used these days in modern software development in particular when it comes to Java development. That is
      because, these days people normally use Spring Boot for bootstrapping Java web apps and Spring’s Spring Data
      project offers the use of the Repository pattern to encapsulate database access.

[DAO Pattern explained](https://colin-but.medium.com/dao-pattern-explained-895b65436f1c)

* **DTO**: "Data transfer objects"
    * an encapsulated object that contains data to be transferred from one location to another location
    * can travel between separate layers in software architecture.

[Dissecting the DTO pattern](https://blog.devgenius.io/dissecting-the-dto-pattern-ac3e54d0e4c8)

</details>

* #### Get information about an order
    * Get a Connection object
    * Create a PreparedStatement object
    * Create a ResultSet object
    * Extract the results of the query from the ResultSet object
    * Extract information from SQLException

* #### Update the status of an order

    * Get a Connection object
    * Create a PreparedStatement object
    * Execute the update operation

* #### Delete orders

    * Get a Connection object
    * Build the delete SQL string
    * Create a PreparedStatement object
    * Execute the Delete Operation

* #### Insert an order

    * Get a Connection object
    * Create a PreparedStatement object for the main order record
    * Disable the auto-commit mode
    * Execute the insert operation
    * Get the ResultSet object to get the identifier of the inserted order
    * Rollback the transaction in case of an error
    * Get the identifier of the inserted order
    * Create a PreparedStatement object for the detail of the order
    * Execute the insert operation
    * Rollback the transaction if nothing was inserted
    * Commit the transaction
    * Rollback the transaction in case an exception is thrown

* #### Get the total amount of all paid orders of a customer

    * Get a Connection object
    * Create a CallableStatement object
    * Call the stored procedure
    * Get the ResultSet object
    * Get the result of the stored-procedure from the ResultSet object

## Resources

- [Accessing Databases Through JDBC with Java](https://app.pluralsight.com/projects/accessing-databases-through-jdbc-in-java)
- [Design_Patterns_In_Java_CRUD](https://github.com/naman14310/Design_Patterns_In_Java_CRUD)
- [Database Normalization](http://extreme-java.blogspot.com/2014/05/database-normalization.html)
- [Database Interaction with DAO and DTO Design Patterns](https://dzone.com/articles/database-interaction-dao-and)