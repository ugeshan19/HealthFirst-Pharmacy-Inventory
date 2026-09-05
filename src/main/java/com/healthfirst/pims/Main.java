package com.healthfirst.pims;

import com.formdev.flatlaf.FlatLightLaf;
import com.healthfirst.pims.ui.LoginFrame;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;

/**
 * Ugeshan Naicker Student No.402313594
 * Main class for the HealthFirst Pharmacy Inventory
 * This class starts the application and opens the login screen.
 */
public class Main {

    public static void main(String[] args) {
        // Set up the application's appearance and apply the custom UI styling
        try {
            FlatLightLaf.setup();
            UIStyle.applyGlobalUI();
        } catch (Exception e) {
            // Display an error message if the interface styling cannot be loaded
            System.err.println("Failed to initialize Look and Feel: " + e.getMessage());
        }

        // Start the application on the Swing event dispatch thread
        SwingUtilities.invokeLater(() -> {
            
            // Create the login window and make it visible to the user
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
