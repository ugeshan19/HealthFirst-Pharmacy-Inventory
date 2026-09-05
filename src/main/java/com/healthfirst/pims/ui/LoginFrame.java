package com.healthfirst.pims.ui;

import com.healthfirst.pims.dao.UserDAO;
import com.healthfirst.pims.model.User;
import com.healthfirst.pims.util.SessionManager;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//Ugeshan Naicker Student No.402313594

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel errorLabel = new JLabel(" ");
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {

        setTitle("HealthFirst PIMS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 540);
        setMinimumSize(new Dimension(820, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(
                buildLeftPanel(),
                BorderLayout.WEST
        );

        add(
                buildLoginPanel(),
                BorderLayout.CENTER
        );
    }

    private JPanel buildLeftPanel() {

        JPanel panel = new JPanel();

        panel.setPreferredSize(
                new Dimension(320, 0)
        );

        panel.setBackground(
                UIStyle.SIDEBAR_BG
        );

        panel.setBorder(
                new EmptyBorder(
                        45,
                        35,
                        30,
                        35
                )
        );

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel(
                        "HealthFirst"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        26
                )
        );

        title.setForeground(
                Color.WHITE
        );

        title.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel systemName =
                new JLabel(
                        "<html>Pharmacy Inventory<br>"
                        + "Management System</html>"
                );

        systemName.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        systemName.setForeground(
                UIStyle.SIDEBAR_TEXT
        );

        systemName.setBorder(
                new EmptyBorder(
                        8,
                        0,
                        0,
                        0
                )
        );

        systemName.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(title);
        panel.add(systemName);

        panel.add(
                Box.createVerticalStrut(55)
        );

        JLabel about =
                new JLabel(
                        "System features"
                );

        about.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        about.setForeground(
                Color.WHITE
        );

        about.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(about);

        panel.add(
                Box.createVerticalStrut(18)
        );

        addFeature(
                panel,
                "•",
                "Manage medicines and stock"
        );

        addFeature(
                panel,
                "•",
                "Process pharmacy sales"
        );

        addFeature(
                panel,
                "•",
                "Manage suppliers and users"
        );

        addFeature(
                panel,
                "•",
                "View inventory and sales reports"
        );

        panel.add(
                Box.createVerticalGlue()
        );

        // Login footer
        JLabel footer =
                new JLabel(
                        "<html>"
                        + "HealthFirst Pharmacy  |  "
                        + java.time.Year.now()
                        + "<br>"
                        + "<font size='2'>Developed by @ugeshan</font>"
                        + "</html>"
                );

        footer.setFont(
                UIStyle.FONT_SMALL
        );

        footer.setForeground(
                new Color(
                        190,
                        205,
                        215
                )
        );

        footer.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(footer);

        return panel;
    }

    private void addFeature(
            JPanel panel,
            String bullet,
            String text
    ) {

        JPanel row =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        row.setOpaque(false);

        row.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel bulletLabel =
                new JLabel(
                        bullet
                );

        bulletLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        bulletLabel.setForeground(
                Color.WHITE
        );

        JLabel textLabel =
                new JLabel(
                        text
                );

        textLabel.setFont(
                UIStyle.FONT_BODY
        );

        textLabel.setForeground(
                UIStyle.SIDEBAR_TEXT
        );

        row.add(bulletLabel);
        row.add(textLabel);

        panel.add(row);

        panel.add(
                Box.createVerticalStrut(14)
        );
    }

    private JPanel buildLoginPanel() {

        JPanel outer =
                new JPanel(
                        new GridBagLayout()
                );

        outer.setBackground(
                Color.WHITE
        );

        JPanel form =
                new JPanel();

        form.setBackground(
                Color.WHITE
        );

        form.setLayout(
                new BoxLayout(
                        form,
                        BoxLayout.Y_AXIS
                )
        );

        form.setPreferredSize(
                new Dimension(
                        350,
                        335
                )
        );

        JLabel title =
                new JLabel(
                        "Login"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        title.setForeground(
                UIStyle.TEXT_DARK
        );

        title.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel subtitle =
                new JLabel(
                        "Enter your account details to continue"
                );

        subtitle.setFont(
                UIStyle.FONT_BODY
        );

        subtitle.setForeground(
                UIStyle.TEXT_MUTED
        );

        subtitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        subtitle.setBorder(
                new EmptyBorder(
                        4,
                        0,
                        20,
                        0
                )
        );

        JLabel usernameLabel =
                new JLabel(
                        "Username"
                );

        usernameLabel.setFont(
                UIStyle.FONT_SUBHEAD
        );

        usernameLabel.setForeground(
                UIStyle.TEXT_DARK
        );

        usernameLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        styleField(usernameField);

        JLabel passwordLabel =
                new JLabel(
                        "Password"
                );

        passwordLabel.setFont(
                UIStyle.FONT_SUBHEAD
        );

        passwordLabel.setForeground(
                UIStyle.TEXT_DARK
        );

        passwordLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        passwordLabel.setBorder(
                new EmptyBorder(
                        13,
                        0,
                        6,
                        0
                )
        );

        styleField(passwordField);

        JCheckBox showPassword =
                new JCheckBox(
                        "Show password"
                );

        showPassword.setFont(
                UIStyle.FONT_SMALL
        );

        showPassword.setForeground(
                UIStyle.TEXT_MUTED
        );

        showPassword.setBackground(
                Color.WHITE
        );

        showPassword.setFocusPainted(false);

        showPassword.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        showPassword.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        0,
                        0
                )
        );

        showPassword.addActionListener(
                e -> {

                    if (showPassword.isSelected()) {

                        passwordField.setEchoChar(
                                (char) 0
                        );

                    } else {

                        passwordField.setEchoChar(
                                '•'
                        );
                    }
                }
        );

        errorLabel.setFont(
                UIStyle.FONT_SMALL
        );

        errorLabel.setForeground(
                UIStyle.DANGER
        );

        errorLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        errorLabel.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        0,
                        0
                )
        );

        JButton loginButton =
                UIStyle.primaryButton(
                        "Login"
                );

        loginButton.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        loginButton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        loginButton.addActionListener(
                e -> attemptLogin()
        );

        KeyAdapter enterKey =
                new KeyAdapter() {

                    @Override
                    public void keyPressed(
                            KeyEvent e
                    ) {

                        if (
                                e.getKeyCode()
                                        == KeyEvent.VK_ENTER
                        ) {

                            attemptLogin();
                        }
                    }
                };

        usernameField.addKeyListener(
                enterKey
        );

        passwordField.addKeyListener(
                enterKey
        );

        form.add(title);
        form.add(subtitle);

        form.add(usernameLabel);

        form.add(
                Box.createVerticalStrut(6)
        );

        form.add(usernameField);

        form.add(passwordLabel);

        form.add(passwordField);

        form.add(showPassword);

        form.add(errorLabel);

        form.add(
                Box.createVerticalStrut(15)
        );

        form.add(loginButton);

        // Move the form slightly higher than the centre
        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;

        gbc.insets =
                new Insets(
                        65,
                        0,
                        0,
                        0
                );

        outer.add(
                form,
                gbc
        );

        return outer;
    }

    private void styleField(
            JTextField field
    ) {

        field.setFont(
                UIStyle.FONT_BODY
        );

        field.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyle.BORDER,
                                1
                        ),
                        new EmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );
    }

    private void attemptLogin() {

        String username =
                usernameField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        if (
                username.isEmpty()
                        || password.isEmpty()
        ) {

            errorLabel.setText(
                    "Please enter both username and password."
            );

            return;
        }

        try {

            User user =
                    userDAO.authenticate(
                            username,
                            password
                    );

            if (user == null) {

                errorLabel.setText(
                        "Invalid username or password."
                );

                passwordField.setText("");

                return;
            }

            SessionManager.login(user);

            dispose();

            if (
                    "Admin".equalsIgnoreCase(
                            user.getRole()
                    )
            ) {

                new AdminDashboard()
                        .setVisible(true);

            } else {

                new CashierDashboard()
                        .setVisible(true);
            }

        } catch (RuntimeException ex) {

            errorLabel.setText(
                    "Connection error: "
                    + shorten(
                            ex.getMessage()
                    )
            );
        }
    }

    private String shorten(
            String message
    ) {

        if (message == null) {
            return "unknown error";
        }

        return message.length() > 70
                ? message.substring(0, 70) + "..."
                : message;
    }
}