package com.healthfirst.pims.dao;

import com.healthfirst.pims.db.DBConnection;
import com.healthfirst.pims.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ugeshan Naicker Student No.402313594
 * Handles database operations for suppliers
 */
public class SupplierDAO {

    /**
     * Retrieves all suppliers sorted by name
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch suppliers: " + e.getMessage(), e);
        }

        return list;
    }

    /**
     * Adds a new supplier to the database
     */
    public void addSupplier(Supplier s) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getContactPerson());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add supplier: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the details of an existing supplier
     */
    public void updateSupplier(Supplier s) {
        String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=? WHERE supplier_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getContactPerson());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            ps.setInt(6, s.getSupplierId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update supplier: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a supplier using the supplier ID
     */
    public void deleteSupplier(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete supplier. It may still be linked to medicines: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a database record into a Supplier object
     */
    private Supplier mapRow(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("name"),
                rs.getString("contact_person"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address")
        );
    }
}