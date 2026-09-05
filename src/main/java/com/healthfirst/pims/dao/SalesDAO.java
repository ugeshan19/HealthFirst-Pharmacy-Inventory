package com.healthfirst.pims.dao;

import com.healthfirst.pims.db.DBConnection;
import com.healthfirst.pims.model.Sale;
import com.healthfirst.pims.model.SaleItem;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Ugeshan Naicker Student No.402313594
 * Handles database operations for sales and sales reports
 */
public class SalesDAO {

    private final MedicineDAO medicineDAO = new MedicineDAO();

    /**
     * Records a complete sale and updates the medicine stock
     * The transaction is rolled back if there is not enough stock or if a database error occurs
     */
    public int recordSale(Sale sale) {
        String insertSale = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
        String insertItem = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // Reduce stock before saving the sale to prevent overselling
            for (SaleItem item : sale.getItems()) {
                boolean ok = medicineDAO.reduceStock(con, item.getMedicineId(), item.getQuantitySold());
                if (!ok) {
                    throw new IllegalStateException("Insufficient stock for: " + item.getMedicineName());
                }
            }

            // Save the main sale record and get its generated ID
            int saleId;
            try (PreparedStatement ps = con.prepareStatement(insertSale, Statement.RETURN_GENERATED_KEYS)) {
                ps.setBigDecimal(1, sale.getTotalAmount());
                if (sale.getUserId() != null) ps.setInt(2, sale.getUserId()); else ps.setNull(2, Types.INTEGER);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    saleId = keys.getInt(1);
                }
            }

            // Save each medicine included in the sale
            try (PreparedStatement ps = con.prepareStatement(insertItem)) {
                for (SaleItem item : sale.getItems()) {
                    ps.setInt(1, saleId);
                    ps.setInt(2, item.getMedicineId());
                    ps.setInt(3, item.getQuantitySold());
                    ps.setBigDecimal(4, item.getPriceAtSale());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return saleId;

        } catch (SQLException | IllegalStateException e) {
            // Undo all changes if the sale cannot be completed
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Sale failed and was rolled back: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // Returns the total sales for today for use on the dashboard
    public BigDecimal getTodayTotalSales() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM sales WHERE DATE(sale_date) = CURDATE()";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal("total");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to compute today's sales: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    // Returns the number of sales transactions made today
    public int getTodayTransactionCount() {
        String sql = "SELECT COUNT(*) AS cnt FROM sales WHERE DATE(sale_date) = CURDATE()";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count today's sales: " + e.getMessage(), e);
        }
        return 0;
    }

    // Returns sales records within the selected date range
    public List<Sale> getSalesBetween(LocalDate start, LocalDate end) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT sa.*, u.full_name AS cashier_name FROM sales sa " +
                     "LEFT JOIN users u ON sa.user_id = u.user_id " +
                     "WHERE DATE(sa.sale_date) BETWEEN ? AND ? ORDER BY sa.sale_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale s = new Sale();
                    s.setSaleId(rs.getInt("sale_id"));
                    s.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
                    s.setTotalAmount(rs.getBigDecimal("total_amount"));
                    int uid = rs.getInt("user_id");
                    s.setUserId(rs.wasNull() ? null : uid);
                    s.setCashierName(rs.getString("cashier_name"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch sales report: " + e.getMessage(), e);
        }
        return list;
    }

    // Returns the total revenue for the selected date range
    public BigDecimal getTotalRevenueBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT COALESCE(SUM(total_amount),0) AS total FROM sales WHERE DATE(sale_date) BETWEEN ? AND ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to sum revenue: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    // Returns the best-selling medicines for the selected date range
    public List<Object[]> getTopSellingMedicines(LocalDate start, LocalDate end, int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT m.name, SUM(si.quantity_sold) AS qty, SUM(si.quantity_sold * si.price_at_sale) AS revenue " +
                     "FROM sale_items si " +
                     "JOIN sales sa ON si.sale_id = sa.sale_id " +
                     "LEFT JOIN medicines m ON si.medicine_id = m.medicine_id " +
                     "WHERE DATE(sa.sale_date) BETWEEN ? AND ? " +
                     "GROUP BY si.medicine_id, m.name ORDER BY qty DESC LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("name"),
                        rs.getInt("qty"),
                        rs.getBigDecimal("revenue")
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch top sellers: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Returns sales grouped by medicine, including quantity sold and total revenue for the selected date range
     */
    public List<Object[]> getItemWiseSales(LocalDate start, LocalDate end) {
        List<Object[]> list = new ArrayList<>();

        String sql =
                "SELECT m.name, " +
                "SUM(si.quantity_sold) AS qty, " +
                "SUM(si.quantity_sold * si.price_at_sale) AS revenue " +
                "FROM sale_items si " +
                "JOIN sales sa ON si.sale_id = sa.sale_id " +
                "JOIN medicines m ON si.medicine_id = m.medicine_id " +
                "WHERE DATE(sa.sale_date) BETWEEN ? AND ? " +
                "GROUP BY si.medicine_id, m.name " +
                "ORDER BY revenue DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(new Object[]{
                            rs.getString("name"),
                            rs.getInt("qty"),
                            rs.getBigDecimal("revenue")
                    });
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to fetch item-wise sales: "
                    + e.getMessage(),
                    e
            );
        }

        return list;
    }
}