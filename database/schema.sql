-- ==========================================================================
-- HealthFirst Pharmacy Inventory Management System (PIMS)
-- Database Schema + Sample Data
-- ==========================================================================

DROP DATABASE IF EXISTS pims_db;

CREATE DATABASE pims_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE pims_db;

-- ==========================================================================
-- Table: users
-- Stores login credentials, roles and account status
-- ==========================================================================

CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('Admin', 'Cashier') NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================================
-- Table: suppliers
-- Stores medicine supplier information
-- ==========================================================================

CREATE TABLE suppliers (
    supplier_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT
);

-- ==========================================================================
-- Table: medicines
-- Stores pharmacy inventory
-- ==========================================================================

CREATE TABLE medicines (
    medicine_id         INT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    company             VARCHAR(100),
    medicine_type       VARCHAR(50),
    price               DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantity_in_stock   INT NOT NULL DEFAULT 0,
    reorder_level       INT NOT NULL DEFAULT 10,
    expiry_date         DATE,
    supplier_id         INT,

    CONSTRAINT fk_medicine_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(supplier_id)
        ON DELETE SET NULL
);

-- ==========================================================================
-- Table: sales
-- Stores the header information for each sale
-- ==========================================================================

CREATE TABLE sales (
    sale_id       INT AUTO_INCREMENT PRIMARY KEY,
    sale_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    user_id       INT,

    CONSTRAINT fk_sale_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);

-- ==========================================================================
-- Table: sale_items
-- Stores individual items belonging to each sale
-- ==========================================================================

CREATE TABLE sale_items (
    sale_item_id    INT AUTO_INCREMENT PRIMARY KEY,
    sale_id         INT NOT NULL,
    medicine_id     INT,
    quantity_sold   INT NOT NULL,
    price_at_sale   DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_saleitem_sale
        FOREIGN KEY (sale_id)
        REFERENCES sales(sale_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_saleitem_medicine
        FOREIGN KEY (medicine_id)
        REFERENCES medicines(medicine_id)
        ON DELETE SET NULL
);

-- ==========================================================================
-- SAMPLE DATA
-- ==========================================================================

-- ==========================================================================
-- Default Users
--
-- Admin:
-- Username: admin
-- Password: admin123
--
-- Cashier:
-- Username: cashier1
-- Password: cashier123
-- ==========================================================================

INSERT INTO users
    (username, password, role, full_name, is_active)
VALUES
    (
        'admin',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'Admin',
        'System Administrator',
        1
    ),
    (
        'cashier1',
        'b4c94003c562bb0d89535eca77f07284fe560fd48a7cc1ed99f0a56263d616ba',
        'Cashier',
        'Jane Cashier',
        1
    );

-- ==========================================================================
-- Suppliers
-- ==========================================================================

INSERT INTO suppliers
    (name, contact_person, phone, email, address)
VALUES
    (
        'MediPharm Distributors',
        'Robert King',
        '011-234-5678',
        'robert@medipharm.com',
        '12 Industrial Rd, Johannesburg'
    ),
    (
        'Global Health Supplies',
        'Alice Nkosi',
        '011-987-6543',
        'alice@ghsupplies.com',
        '45 Main St, Pretoria'
    ),
    (
        'CarePlus Wholesalers',
        'Sipho Dlamini',
        '031-456-7890',
        'sipho@careplus.co.za',
        '7 Market Ave, Durban'
    );

-- ==========================================================================
-- Medicines
--
-- Includes:
-- - Normal stock
-- - Low stock
-- - Medicines expiring soon
-- - Medicines with longer expiry dates
--
-- Dynamic expiry dates are used so the demonstration data remains useful
-- when the database is installed at a later date.
-- ==========================================================================

INSERT INTO medicines
    (
        name,
        company,
        medicine_type,
        price,
        quantity_in_stock,
        reorder_level,
        expiry_date,
        supplier_id
    )
VALUES
    (
        'Paracetamol 500mg',
        'PharmaCorp',
        'Tablet',
        25.50,
        200,
        30,
        DATE_ADD(CURDATE(), INTERVAL 18 MONTH),
        1
    ),
    (
        'Amoxicillin 250mg',
        'BioGenix',
        'Capsule',
        65.00,
        120,
        25,
        DATE_ADD(CURDATE(), INTERVAL 12 MONTH),
        1
    ),
    (
        'Cough Syrup Honey Lem.',
        'NatureCare',
        'Syrup',
        48.75,
        40,
        15,
        DATE_ADD(CURDATE(), INTERVAL 15 DAY),
        2
    ),
    (
        'Ibuprofen 200mg',
        'PharmaCorp',
        'Tablet',
        32.00,
        8,
        20,
        DATE_ADD(CURDATE(), INTERVAL 9 MONTH),
        1
    ),
    (
        'Insulin Injection',
        'DiabetiCare',
        'Injection',
        210.00,
        15,
        10,
        DATE_ADD(CURDATE(), INTERVAL 6 DAY),
        3
    ),
    (
        'Hydrocortisone Cream',
        'DermaWell',
        'Cream',
        55.20,
        60,
        15,
        DATE_ADD(CURDATE(), INTERVAL 24 MONTH),
        2
    ),
    (
        'Vitamin C 1000mg',
        'NatureCare',
        'Tablet',
        38.90,
        5,
        20,
        DATE_ADD(CURDATE(), INTERVAL 20 MONTH),
        2
    ),
    (
        'Antacid Suspension',
        'Global Health',
        'Syrup',
        29.00,
        75,
        20,
        DATE_ADD(CURDATE(), INTERVAL 25 DAY),
        3
    ),
    (
        'Cetirizine 10mg',
        'BioGenix',
        'Tablet',
        19.99,
        150,
        25,
        DATE_ADD(CURDATE(), INTERVAL 14 MONTH),
        1
    ),
    (
        'Amoxiclav 625mg',
        'BioGenix',
        'Tablet',
        89.50,
        3,
        15,
        DATE_ADD(CURDATE(), INTERVAL 3 DAY),
        1
    );

-- ==========================================================================
-- End of database/schema.sql
-- ==========================================================================