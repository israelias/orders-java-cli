drop table if exists customers;
drop table if exists products;
drop table if exists orders;
drop table if exists order_detail;

create table customers(
  customer_id     numeric IDENTITY PRIMARY KEY,
  customer_name   varchar(50) NOT NULL,
  customer_email  varchar(50) NOT NULL
);

create table products(
  product_id     numeric IDENTITY PRIMARY KEY,
  product_name   varchar(50) NOT NULL,
  product_price  decimal(20, 2) NOT NULL
);

create table orders(
  order_id           numeric IDENTITY PRIMARY KEY,
  order_customer_id  numeric NOT NULL,
  order_date         timestamp NOT NULL,
  order_status       varchar(10) NOT NULL,
  constraint fk_customer foreign key (order_customer_id) references customers(customer_id)
);

create table order_details(
  order_detail_order_id    numeric NOT NULL,
  order_detail_product_id  numeric NOT NULL,
  order_detail_quantity    numeric NOT NULL,
  constraint fk_product foreign key (order_detail_product_id) references products(product_id),
  constraint fk_order foreign key (order_detail_order_id) references orders(order_id) ON DELETE CASCADE,
  primary key(order_detail_order_id, order_detail_product_id)
);

CREATE ALIAS GET_PAID_ORDER_TOTAL_FROM_CUSTOMER FOR "com.example.order.util.H2StoredProcedures.getPaidOrderTotalFromCustomer";
CREATE ALIAS MULT FOR "com.example.order.util.H2StoredProcedures.mult";

insert into customers values(1, 'Kevin Doe', 'kdoe@example.com');
insert into customers values(2, 'Amanda Walton', 'awton@example.com');
insert into customers values(3, 'Dave Keaton', 'davek@example.com');
insert into customers values(4, 'Joe Smith', 'joes@example.com');

insert into products values(1, 'Case', 9.99);
insert into products values(2, 'Charger', 9.99);
insert into products values(3, 'Stand', 9.99);

insert into orders values(1, 1, parsedatetime('17-09-2012 18:47:52.69', 'dd-MM-yyyy hh:mm:ss.SS'), 'created');
insert into orders values(2, 2, parsedatetime('17-09-2012 18:47:52.69', 'dd-MM-yyyy hh:mm:ss.SS'), 'paid');
insert into orders values(3, 3, parsedatetime('17-09-2012 18:47:52.69', 'dd-MM-yyyy hh:mm:ss.SS'), 'canceled');
insert into orders values(4, 1, parsedatetime('17-09-2012 18:47:52.69', 'dd-MM-yyyy hh:mm:ss.SS'), 'paid');

insert into order_details values(1, 2, 2);
insert into order_details values(1, 3, 1);
insert into order_details values(2, 1, 4);
insert into order_details values(3, 1, 1);
insert into order_details values(4, 1, 1);
insert into order_details values(4, 2, 2);

commit;