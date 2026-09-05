package com.healthfirst.pims.dao;

import com.healthfirst.pims.db.DBConnection;
import com.healthfirst.pims.model.User;
import com.healthfirst.pims.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ugeshan Naicker Student No.402313594
 * Handles all database operations related to users
 * This includes logging in, adding, updating, deleting and retrieving user information
 */
public class UserDAO {

    /**
     * Checks the login details against the users stored in the database
     * Only active users are allowed to log in
     */
    public User authenticate(String username, String plainPassword) {

        // Select the user with the given username, provided the account is active
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Add the username to the SQL query
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                // Check if a matching user was found.
                if (rs.next()) {

                    // Get the encrypted password stored in the database
                    String storedHash = rs.getString("password");

                    // Compare the entered password with the stored password
                    if (PasswordUtil.matches(plainPassword, storedHash)) {
                        return mapRow(rs);
                    }
                }
            }

        } catch (SQLException e) {
            // Handle any database errors that occur during login
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }

        // Return null when the login details are incorrect
        return null;
    }

    /**
     * Retrieves all users from the database
     * Users are sorted by role and then by their full name
     */
    public List<User> getAllUsers() {

        // Create a list to store the users returned from the database
        List<User> list = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY role, full_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Go through each database record and convert it into a User object
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            // Handle any database errors when retrieving users
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }

        return list;
    }

    /**
     * Checks whether a username already exists in the database
     */
    public boolean usernameExists(String username) {

        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Add the username being checked to the query
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                // If a record is found, the username already exists
                return rs.next();
            }

        } catch (SQLException e) {
            // Handle any database errors during the username check
            throw new RuntimeException("Lookup failed: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a new user to the database
     */
    public void addUser(User user) {

        String sql = "INSERT INTO users (username, password, role, full_name, is_active) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Add the user's details to the SQL statement
            ps.setString(1, user.getUsername());

            // Hash the password before storing it in the database
            ps.setString(2, PasswordUtil.hash(user.getPassword()));

            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName());
            ps.setBoolean(5, user.isActive());

            ps.executeUpdate();

        } catch (SQLException e) {
            // Handle any database errors when adding the user
            throw new RuntimeException("Failed to add user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user's details
     * If no new password is provided, the existing password is kept
     */
    public void updateUser(User user, String newPassword) {

        // Use a different SQL statement depending on whether the user is changing their password
        String sql = newPassword == null || newPassword.isBlank()
                ? "UPDATE users SET full_name = ?, role = ?, is_active = ? WHERE user_id = ?"
                : "UPDATE users SET full_name = ?, role = ?, is_active = ?, password = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Update the user's basic information
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole());
            ps.setBoolean(3, user.isActive());

            // Update the password only when a new password was entered
            if (newPassword == null || newPassword.isBlank()) {
                ps.setInt(4, user.getUserId());
            } else {
                // Hash the new password before saving it
                ps.setString(4, PasswordUtil.hash(newPassword));
                ps.setInt(5, user.getUserId());
            }

            // Execute the UPDATE statement
            ps.executeUpdate();

        } catch (SQLException e) {
            // Handle any database errors when updating the user
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user from the database using their user ID
     */
    public void deleteUser(int userId) {

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Set the ID of the user that needs to be deleted
            ps.setInt(1, userId);

            ps.executeUpdate();

        } catch (SQLException e) {
            // Handle any database errors when deleting the user
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a database record into a User object
     */
    private User mapRow(ResultSet rs) throws SQLException {

        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("full_name"),
                rs.getBoolean("is_active")
        );
    }
}