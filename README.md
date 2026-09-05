# HealthFirst Pharmacy Inventory Management System (PIMS)

A Java Swing desktop application for managing pharmacy stock, suppliers, users, point-of-sale transactions, billing and reports. Backed by MySQL via JDBC.

## Features

**Administrator**
- Add, edit, delete and search medicines
- Manage suppliers and user accounts (Admin / Cashier roles)
- Reports: sales, item-wise sales, low stock and expiry, with print and **PDF export**

**Cashier**
- Point of Sale: search medicines, check price and availability, build a cart, adjust quantities
- Process sales and generate customer bills (print or save)
- No access to medicine, supplier or user management

## Tech stack

Java 17 · Swing/AWT · JDBC · MySQL 8 · Maven · FlatLaf (modern look and feel) · OpenPDF

## Design

- **Layered structure:** `ui` → `dao` → `db`, with `model` classes and `util` helpers.
- **DAO pattern** for all database access (`MedicineDAO`, `SalesDAO`, `SupplierDAO`, `UserDAO`).
- **Role-based access** via a session manager; passwords stored as SHA-256 hashes.
- Connection settings are read from `db.properties`, so no credentials are hard-coded.

```
src/main/java/com/healthfirst/pims/
├── Main.java
├── db/      DBConnection
├── dao/     MedicineDAO, SalesDAO, SupplierDAO, UserDAO
├── model/   Medicine, Sale, SaleItem, Supplier, User
├── util/    PasswordUtil, SessionManager, UIStyle
└── ui/      LoginFrame, AdminDashboard, CashierDashboard, admin/, cashier/
```

## Getting started

**Prerequisites:** JDK 17+, Maven, MySQL 8.

1. Create the database and sample data:
   ```bash
   mysql -u root -p < database/schema.sql
   ```
2. Configure the connection:
   ```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   # then edit db.properties with your MySQL password
   ```
3. Build and run:
   ```bash
   mvn clean package
   java -jar target/PIMS-jar-with-dependencies.jar
   ```

### Demo logins (from `schema.sql`)

| Role | Username | Password |
|---|---|---|
| Admin | admin | admin123 |
| Cashier | cashier1 | cashier123 |


## Screenshots

Add PNGs to `docs/screenshots/` and link them here.

## Author

Ugeshan Naicker · Built as a PRO732 assignment at Richfield.
