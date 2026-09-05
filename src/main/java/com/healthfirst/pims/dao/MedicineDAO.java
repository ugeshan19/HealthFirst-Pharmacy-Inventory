package com.healthfirst.pims.dao;

import com.healthfirst.pims.db.DBConnection;
import com.healthfirst.pims.model.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ugeshan Naicker Student No.402313594
 * Handles database operations for medicines
 */
public class MedicineDAO {

    // Common query used when retrieving medicine details with supplier information
    private static final String BASE_SELECT =
        "SELECT m.*, s.name AS supplier_name FROM medicines m " +
        "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id ";

    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY m.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch medicines: " + e.getMessage(), e);
        }

        return list;
    }

    // Searches for medicines by name, company or medicine type
    public List<Medicine> searchMedicines(String keyword) {
        List<Medicine> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE m.name LIKE ? OR m.company LIKE ? OR m.medicine_type LIKE ? ORDER BY m.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }

        return list;
    }

    // Retrieves medicines where the stock has reached or fallen below the reorder level
    public List<Medicine> getLowStock() {
        List<Medicine> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE m.quantity_in_stock <= m.reorder_level ORDER BY m.quantity_in_stock";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch low stock items: " + e.getMessage(), e);
        }

        return list;
    }

    // Retrieves medicines that are due to expire within the specified number of days
    public List<Medicine> getExpiringWithinDays(int days) {

        List<Medicine> list = new ArrayList<>();

        String sql = BASE_SELECT +
                "WHERE m.expiry_date IS NOT NULL " +
                "AND m.expiry_date >= CURDATE() " +
                "AND m.expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "ORDER BY m.expiry_date";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to fetch expiring medicines: "
                    + e.getMessage(),
                    e
            );
        }

        return list;
    }

    // Adds a new medicine to the database
    public void addMedicine(Medicine m) {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindMedicine(ps, m);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add medicine: " + e.getMessage(), e);
        }
    }

    // Updates an existing medicine
    public void updateMedicine(Medicine m) {
        String sql = "UPDATE medicines SET name=?, company=?, medicine_type=?, price=?, quantity_in_stock=?, " +
                     "reorder_level=?, expiry_date=?, supplier_id=? WHERE medicine_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindMedicine(ps, m);
            ps.setInt(9, m.getMedicineId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update medicine: " + e.getMessage(), e);
        }
    }

    // Deletes a medicine using its ID
    public void deleteMedicine(int medicineId) {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, medicineId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete medicine: " + e.getMessage(), e);
        }
    }

    // Reduces stock after a sale while making sure the stock does not become negative
    public boolean reduceStock(Connection con, int medicineId, int quantity) throws SQLException {
        String sql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? " +
                     "WHERE medicine_id = ? AND quantity_in_stock >= ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, medicineId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() == 1;
        }
    }

    //Sets the medicine values used by the INSERT and UPDATE queries
    private void bindMedicine(PreparedStatement ps, Medicine m) throws SQLException {
        ps.setString(1, m.getName());
        ps.setString(2, m.getCompany());
        ps.setString(3, m.getMedicineType());
        ps.setBigDecimal(4, m.getPrice());
        ps.setInt(5, m.getQuantityInStock());
        ps.setInt(6, m.getReorderLevel());

        if (m.getExpiryDate() != null) {
            ps.setDate(7, Date.valueOf(m.getExpiryDate()));
        } else {
            ps.setNull(7, Types.DATE);
        }

        if (m.getSupplierId() != null) {
            ps.setInt(8, m.getSupplierId());
        } else {
            ps.setNull(8, Types.INTEGER);
        }
    }

    // Converts a database record into a Medicine object
    private Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setMedicineId(rs.getInt("medicine_id"));
        m.setName(rs.getString("name"));
        m.setCompany(rs.getString("company"));
        m.setMedicineType(rs.getString("medicine_type"));
        m.setPrice(rs.getBigDecimal("price"));
        m.setQuantityInStock(rs.getInt("quantity_in_stock"));
        m.setReorderLevel(rs.getInt("reorder_level"));

        Date exp = rs.getDate("expiry_date");
        m.setExpiryDate(exp != null ? exp.toLocalDate() : null);

        int supId = rs.getInt("supplier_id");
        m.setSupplierId(rs.wasNull() ? null : supId);
        m.setSupplierName(rs.getString("supplier_name"));

        return m;
    }
}