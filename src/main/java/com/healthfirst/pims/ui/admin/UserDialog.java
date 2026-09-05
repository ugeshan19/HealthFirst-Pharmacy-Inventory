package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.UserDAO;
import com.healthfirst.pims.model.User;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//Ugeshan Naicker Student No.402313594
public class UserDialog extends JDialog {

    private final JTextField usernameField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    private final JComboBox<String> roleCombo =
            new JComboBox<>(
                    new String[]{"Cashier", "Admin"}
            );

    private final JCheckBox activeCheck =
            new JCheckBox("Account is active");

    private final UserDAO userDAO = new UserDAO();
    private final User editingUser;

    private boolean saved = false;

    public UserDialog(Frame owner, User existing) {

        super(
                owner,
                existing == null
                        ? "Add New User"
                        : "Edit User",
                true
        );

        this.editingUser = existing;

        setSize(
                440,
                existing == null ? 500 : 520
        );

        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel();

        content.setBackground(Color.WHITE);

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        content.setBorder(
                new EmptyBorder(
                        24,
                        28,
                        24,
                        28
                )
        );

        // ---------------------------------------------------------
        // TITLE
        // ---------------------------------------------------------

        JLabel title =
                UIStyle.heading(
                        existing == null
                                ? "Add New User"
                                : "Edit User"
                );

        title.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(title);

        content.add(
                Box.createRigidArea(
                        new Dimension(0, 18)
                )
        );

        // ---------------------------------------------------------
        // USERNAME
        // ---------------------------------------------------------

        content.add(
                fieldBlock(
                        "Username",
                        usernameField
                )
        );

        if (existing != null) {
            usernameField.setEnabled(false);
        }

        // ---------------------------------------------------------
        // FULL NAME
        // ---------------------------------------------------------

        content.add(
                fieldBlock(
                        "Full Name",
                        fullNameField
                )
        );

        // ---------------------------------------------------------
        // PASSWORD
        // ---------------------------------------------------------

        content.add(
                fieldBlock(
                        existing == null
                                ? "Password"
                                : "New Password (leave blank to keep current)",
                        passwordField
                )
        );

        // ---------------------------------------------------------
        // ROLE
        // ---------------------------------------------------------

        content.add(
                fieldBlock(
                        "Role",
                        roleCombo
                )
        );

        // ---------------------------------------------------------
        // ACTIVE ACCOUNT
        // ---------------------------------------------------------

        activeCheck.setOpaque(false);
        activeCheck.setSelected(true);

        activeCheck.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(
                Box.createRigidArea(
                        new Dimension(0, 2)
                )
        );

        content.add(activeCheck);

        // ---------------------------------------------------------
        // EXISTING USER
        // ---------------------------------------------------------

        if (existing != null) {
            populateFields(existing);
        }

        // ---------------------------------------------------------
        // BUTTONS
        // ---------------------------------------------------------

        JPanel buttonRow =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        buttonRow.setOpaque(false);

        buttonRow.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        buttonRow.setBorder(
                new EmptyBorder(
                        20,
                        0,
                        0,
                        0
                )
        );

        JButton cancelButton =
                UIStyle.secondaryButton(
                        "Cancel"
                );

        cancelButton.addActionListener(
                e -> dispose()
        );

        JButton saveButton =
                UIStyle.primaryButton(
                        "Save User"
                );

        saveButton.addActionListener(
                e -> onSave()
        );

        buttonRow.add(cancelButton);
        buttonRow.add(saveButton);

        content.add(buttonRow);

        // ---------------------------------------------------------
        // SCROLL CONTAINER
        // ---------------------------------------------------------

        JScrollPane scrollPane =
                new JScrollPane(content);

        scrollPane.setBorder(null);

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        setContentPane(scrollPane);

        SwingUtilities.invokeLater(
                () -> {
                    if (existing == null) {
                        usernameField.requestFocusInWindow();
                    } else {
                        fullNameField.requestFocusInWindow();
                    }
                }
        );
    }

    // =============================================================
    // POPULATE EXISTING USER
    // =============================================================

    private void populateFields(User u) {

        usernameField.setText(
                u.getUsername() != null
                        ? u.getUsername()
                        : ""
        );

        fullNameField.setText(
                u.getFullName() != null
                        ? u.getFullName()
                        : ""
        );

        roleCombo.setSelectedItem(
                u.getRole()
        );

        activeCheck.setSelected(
                u.isActive()
        );
    }

    // =============================================================
    // FIELD BLOCK
    // =============================================================

    private JPanel fieldBlock(
            String label,
            JComponent field
    ) {

        JPanel block = new JPanel();

        block.setOpaque(false);

        block.setLayout(
                new BoxLayout(
                        block,
                        BoxLayout.Y_AXIS
                )
        );

        block.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        block.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        12,
                        0
                )
        );

        JLabel labelComponent =
                UIStyle.subheading(label);

        labelComponent.setFont(
                UIStyle.FONT_SMALL.deriveFont(
                        Font.BOLD
                )
        );

        labelComponent.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        labelComponent.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        4,
                        0
                )
        );

        field.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        field.setPreferredSize(
                new Dimension(
                        0,
                        34
                )
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        34
                )
        );

        if (field instanceof JTextField) {

            field.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    UIStyle.BORDER,
                                    1,
                                    true
                            ),
                            new EmptyBorder(
                                    4,
                                    10,
                                    4,
                                    10
                            )
                    )
            );
        }

        if (field instanceof JComboBox<?>) {

            field.setBackground(Color.WHITE);
        }

        block.add(labelComponent);
        block.add(field);

        return block;
    }

    // =============================================================
    // SAVE
    // =============================================================

    private void onSave() {

        String username =
                usernameField
                        .getText()
                        .trim();

        String fullName =
                fullNameField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        String role =
                (String) roleCombo.getSelectedItem();

        // ---------------------------------------------------------
        // VALIDATION
        // ---------------------------------------------------------

        if (username.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            usernameField.requestFocusInWindow();
            return;
        }

        if (fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Full name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            fullNameField.requestFocusInWindow();
            return;
        }

        if (editingUser == null &&
                password.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password is required for a new user.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            passwordField.requestFocusInWindow();
            return;
        }

        // ---------------------------------------------------------
        // DATABASE SAVE
        // ---------------------------------------------------------

        try {

            if (editingUser == null) {

                if (userDAO.usernameExists(username)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "That username is already taken.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE
                    );

                    usernameField.requestFocusInWindow();
                    return;
                }

                User user = new User();

                user.setUsername(username);
                user.setPassword(password);
                user.setRole(role);
                user.setFullName(fullName);
                user.setActive(
                        activeCheck.isSelected()
                );

                userDAO.addUser(user);

            } else {

                editingUser.setFullName(fullName);
                editingUser.setRole(role);
                editingUser.setActive(
                        activeCheck.isSelected()
                );

                userDAO.updateUser(
                        editingUser,
                        password.isBlank()
                                ? null
                                : password
                );
            }

            saved = true;

            JOptionPane.showMessageDialog(
                    this,
                    "User saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save user:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =============================================================
    // SAVED STATUS
    // =============================================================

    public boolean isSaved() {
        return saved;
    }
}