package com.healthfirst.pims.ui;

import com.healthfirst.pims.ui.cashier.POSPanel;
import com.healthfirst.pims.ui.cashier.StockCheckPanel;
import com.healthfirst.pims.util.SessionManager;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ugeshan Naicker Student No.402313594
 * Main window for Cashier users.
 * Cashiers can only process sales (POS) and check stock/prices - no editing rights.
 */
public class CashierDashboard extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();

    public CashierDashboard() {

        setTitle("HealthFirst PIMS - Cashier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        showCard("Point of Sale");
    }

    private JPanel buildSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setLayout(
                new BoxLayout(
                        sidebar,
                        BoxLayout.Y_AXIS
                )
        );

        sidebar.setBackground(
                UIStyle.SIDEBAR_BG
        );

        sidebar.setPreferredSize(
                new Dimension(230, 0)
        );

        sidebar.setBorder(
                new EmptyBorder(
                        24,
                        0,
                        20,
                        0
                )
        );

        JLabel logo =
                new JLabel(
                        "  + HealthFirst"
                );

        logo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        logo.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        4,
                        0
                )
        );

        JLabel role =
                new JLabel(
                        "  Cashier Panel"
                );

        role.setFont(
                UIStyle.FONT_SMALL
        );

        role.setForeground(
                new Color(
                        255,
                        255,
                        255,
                        140
                )
        );

        role.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        role.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        24,
                        0
                )
        );

        sidebar.add(logo);
        sidebar.add(role);

        sidebar.add(
                navItem(
                        "Point of Sale",
                        "\uD83D\uDED2"
                )
        );

        sidebar.add(
                navItem(
                        "Stock Check",
                        "\uD83D\uDD0D"
                )
        );

        sidebar.add(
                Box.createVerticalGlue()
        );

        // User information
        JPanel userCard = new JPanel();

        userCard.setLayout(
                new BoxLayout(
                        userCard,
                        BoxLayout.Y_AXIS
                )
        );

        userCard.setBackground(
                UIStyle.SIDEBAR_HOVER
        );

        userCard.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        userCard.setMaximumSize(
                new Dimension(
                        206,
                        70
                )
        );

        userCard.setBorder(
                new EmptyBorder(
                        10,
                        12,
                        10,
                        12
                )
        );

        JLabel name =
                new JLabel(
                        SessionManager.getCurrentUser() != null
                                ? SessionManager
                                        .getCurrentUser()
                                        .getFullName()
                                : "Cashier"
                );

        name.setForeground(
                Color.WHITE
        );

        name.setFont(
                UIStyle.FONT_SUBHEAD
                        .deriveFont(13f)
        );

        JLabel roleTag =
                new JLabel(
                        "Cashier"
                );

        roleTag.setForeground(
                UIStyle.ACCENT
        );

        roleTag.setFont(
                UIStyle.FONT_SMALL
        );

        userCard.add(name);
        userCard.add(roleTag);

        /*
         * Developer footer
         */
        JPanel studentFooter =
                new JPanel();

        studentFooter.setLayout(
                new BoxLayout(
                        studentFooter,
                        BoxLayout.Y_AXIS
                )
        );

        studentFooter.setOpaque(false);

        studentFooter.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        studentFooter.setBorder(
                new EmptyBorder(
                        0,
                        14,
                        0,
                        0
                )
        );

        studentFooter.setMaximumSize(
                new Dimension(
                        206,
                        42
                )
        );

        JLabel developerName =
                new JLabel(
                        "Developed by @ugeshan"
                );

        developerName.setFont(
                UIStyle.FONT_SMALL
        );

        developerName.setForeground(
                UIStyle.SIDEBAR_TEXT
        );

        JLabel studentNumber =
                new JLabel(
                        "Student No. 402313594"
                );

        studentNumber.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );

        studentNumber.setForeground(
                UIStyle.SIDEBAR_MUTED
        );

        studentFooter.add(
                developerName
        );

        studentFooter.add(
                studentNumber
        );

        // Logout button
        JButton logout =
                new JButton(
                        "Log Out"
                );

        logout.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        logout.setMaximumSize(
                new Dimension(
                        206,
                        36
                )
        );

        logout.setFocusPainted(false);

        logout.setBackground(
                UIStyle.DANGER
        );

        logout.setForeground(
                Color.WHITE
        );

        logout.setBorder(
                new EmptyBorder(
                        8,
                        12,
                        8,
                        12
                )
        );

        logout.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        logout.addActionListener(
                e -> logout()
        );

        sidebar.add(
                Box.createRigidArea(
                        new Dimension(0, 10)
                )
        );

        sidebar.add(
                leftAligned(userCard)
        );

        // Developer details above logout
        sidebar.add(
                Box.createRigidArea(
                        new Dimension(0, 12)
                )
        );

        sidebar.add(
                leftAligned(studentFooter)
        );

        sidebar.add(
                Box.createRigidArea(
                        new Dimension(0, 10)
                )
        );

        sidebar.add(
                leftAligned(logout)
        );

        return sidebar;
    }

    private JPanel leftAligned(
            JComponent comp
    ) {

        JPanel p = new JPanel();

        p.setOpaque(false);

        p.setLayout(
                new BoxLayout(
                        p,
                        BoxLayout.X_AXIS
                )
        );

        p.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        p.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        0,
                        12
                )
        );

        p.add(comp);

        return p;
    }

    private JButton navItem(
            String label,
            String icon
    ) {

        JButton btn =
                new JButton(
                        "  "
                        + icon
                        + "   "
                        + label
                );

        btn.setFont(
                UIStyle.FONT_NAV
        );

        btn.setForeground(
                UIStyle.SIDEBAR_TEXT
        );

        btn.setBackground(
                UIStyle.SIDEBAR_BG
        );

        btn.setBorderPainted(false);

        btn.setFocusPainted(false);

        btn.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        btn.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        btn.setMaximumSize(
                new Dimension(
                        230,
                        44
                )
        );

        btn.setPreferredSize(
                new Dimension(
                        230,
                        44
                )
        );

        btn.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        btn.addActionListener(
                e -> showCard(label)
        );

        navButtons.put(
                label,
                btn
        );

        return btn;
    }

    private void highlightNav(
            String selected
    ) {

        for (
                Map.Entry<String, JButton> entry
                : navButtons.entrySet()
        ) {

            boolean active =
                    entry.getKey()
                            .equals(selected);

            entry.getValue().setBackground(
                    active
                            ? UIStyle.SIDEBAR_HOVER
                            : UIStyle.SIDEBAR_BG
            );

            entry.getValue().setForeground(
                    active
                            ? Color.WHITE
                            : UIStyle.SIDEBAR_TEXT
            );
        }
    }

    private JPanel buildContent() {

        contentPanel.setBackground(
                UIStyle.BG_LIGHT
        );

        contentPanel.add(
                new POSPanel(),
                "Point of Sale"
        );

        contentPanel.add(
                new StockCheckPanel(),
                "Stock Check"
        );

        return contentPanel;
    }

    private void showCard(
            String name
    ) {

        cardLayout.show(
                contentPanel,
                name
        );

        highlightNav(name);
    }

    private void logout() {

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Log out of HealthFirst PIMS?",
                        "Confirm Logout",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                confirm
                        == JOptionPane.YES_OPTION
        ) {

            SessionManager.logout();

            dispose();

            new LoginFrame()
                    .setVisible(true);
        }
    }
}