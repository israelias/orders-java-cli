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

## H2 In Memory Database

* ### Connect to the database
    * Add a field for the database connection URL
        * `"jdbc:h2:mem:orders;DB_CLOSE_DELAY=-1"`
    * Add field for the database user
        * `"sa"`
    * Add field for db password
        * `"""`
    * Use DriverManager class to connect to the database
    * `database.getConnection()`

### DAO, DTO and Statement Logic

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