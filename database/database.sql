CREATE DATABASE mybookdb;
USE mybookdb;

CREATE TABLE books (
    book_no INT AUTO_INCREMENT PRIMARY KEY,
    book_name VARCHAR(50) UNIQUE NOT NULL,
    book_price DOUBLE NOT NULL
);

CREATE TABLE bills (
    bill_no INT AUTO_INCREMENT PRIMARY KEY,
    bill_date DATETIME,
    cust_name VARCHAR(50),
    mobile_no VARCHAR(15),
    total_bill DOUBLE
);

CREATE TABLE bill_details (
    bill_no INT,
    book_name VARCHAR(50),
    book_price DOUBLE,
    book_quantity INT,
    amount DOUBLE
);
