-- Step 1: Create Database
CREATE DATABASE BankingSystem;
-- Step 2: Use the Database
USE BankingSystem;
-- Step 3: Create Tables in the correct order
-- Create User table
CREATE TABLE User (
    user_id        INT PRIMARY KEY AUTO_INCREMENT,
    username       VARCHAR(50) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    phone          VARCHAR(20),
    status         ENUM('active','inactive','suspended') DEFAULT 'active',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Create Admin table
CREATE TABLE Admin (
    admin_id       INT PRIMARY KEY AUTO_INCREMENT,
    username       VARCHAR(50) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    role           VARCHAR(50) NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
-- Create Account table
CREATE TABLE Account (
    account_no     BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id        INT NOT NULL,
    account_type   ENUM('savings','current') NOT NULL,
    balance        DECIMAL(15,2) DEFAULT 0.00,
    status         ENUM('active','inactive','closed') DEFAULT 'active',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);
-- Create Transaction table
CREATE TABLE Transaction (
    txn_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_no     BIGINT NOT NULL,
    txn_type       ENUM('fund_transfer','bill_payment') NOT NULL,
    amount         DECIMAL(15,2) NOT NULL,
    amount_type	   ENUM('sent','received'),
    current_balance DECIMAL(15,2) NOT NULL,
    txn_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status         ENUM('success','pending','failed') DEFAULT 'pending',
    CONSTRAINT fk_txn_account FOREIGN KEY (account_no) REFERENCES Account(account_no) ON DELETE CASCADE
);
-- Create FundTransfer table
CREATE TABLE fund_transfer (
    transfer_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    txn_id             BIGINT NOT NULL,
    from_account_no    BIGINT NOT NULL,
    to_account_no      BIGINT NOT NULL,
    amount             DECIMAL(15,2) NOT NULL,
    transfer_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status             ENUM('success','pending','failed') DEFAULT 'pending',
    CONSTRAINT fk_transfer_txn FOREIGN KEY (txn_id) REFERENCES Transaction(txn_id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_from FOREIGN KEY (from_account_no) REFERENCES Account(account_no) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_to FOREIGN KEY (to_account_no) REFERENCES Account(account_no) ON DELETE CASCADE
);
-- Create BillPayment table
CREATE TABLE bill_payment (
    bill_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    txn_id         BIGINT NOT NULL,
    biller_name    VARCHAR(100) NOT NULL,
    amount         DECIMAL(15,2) NOT NULL,
    due_date       DATE NOT NULL,
    payment_date   TIMESTAMP NULL,
    status         ENUM('paid','unpaid','pending') DEFAULT 'pending',
    CONSTRAINT fk_bill_txn FOREIGN KEY (txn_id) REFERENCES Transaction(txn_id) ON DELETE CASCADE
);
-- Create Card table
CREATE TABLE Card (
    card_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_no     BIGINT NOT NULL,
    card_number    VARCHAR(20) NOT NULL UNIQUE,
    card_type      ENUM('debit','credit') NOT NULL,
    expiry_date    DATE NOT NULL,
    cvv            CHAR(3) NOT NULL,
    status         ENUM('active','blocked') DEFAULT 'active',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_card_account FOREIGN KEY (account_no) REFERENCES Account(account_no) ON DELETE CASCADE
);
-- Create Loan table
CREATE TABLE Loan (
    loan_id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id              INT NOT NULL,
    loan_type            ENUM('home','personal','education','business') NOT NULL,
    principal            DECIMAL(15,2) NOT NULL,
    interest_rate        DECIMAL(5,2) NOT NULL,
    duration_months      INT NOT NULL,
    outstanding_amount   DECIMAL(15,2) NOT NULL,
    status               ENUM('approved','pending','rejected','closed') DEFAULT 'pending',
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loan_user FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE 
);