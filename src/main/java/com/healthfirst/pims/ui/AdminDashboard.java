package com.healthfirst.pims.ui;

import com.healthfirst.pims.dao.MedicineDAO;
import com.healthfirst.pims.dao.SalesDAO;
import com.healthfirst.pims.ui.admin.ManageMedicinesPanel;
import com.healthfirst.pims.ui.admin.ManageSuppliersPanel;
import com.healthfirst.pims.ui.admin.ManageUsersPanel;
import com.healthfirst.pims.ui.admin.ReportsPanel;
import com.healthfirst.pims.util.SessionManager;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ugeshan Naicker Student No.402313594
 * Main window for Administrator users
 * Provides a fixed sidebar for navigation and a CardLayout content area for: Overview, Medicines, Suppliers, Users, Reports
 */
public class AdminDashboard extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private JPanel overviewPanel;

    public AdminDashboard() {
        setTitle("HealthFirst PIMS - Administrator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        showCard("Overview");
    }

    // ---------------------------------------------------------------------
    private JPanel buildSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIStyle.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(24, 0, 20, 0));

        JLabel logo = new JLabel("  + HealthFirst");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 12, 4, 0));

        JLabel role = new JLabel("  Administrator Panel");
        role.setFont(UIStyle.FONT_SMALL);
        role.setForeground(new Color(255, 255, 255, 140));
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        role.setBorder(new EmptyBorder(0, 12, 24, 0));

        sidebar.add(logo);
        sidebar.add(role);

        sidebar.add(navItem("Overview", "\uD83C\uDFE0"));
        sidebar.add(navItem("Medicines", "\uD83D\uDC8A"));
        sidebar.add(navItem("Suppliers", "\uD83D\uDE9A"));
        sidebar.add(navItem("Users", "\uD83D\uDC65"));
        sidebar.add(navItem("Reports", "\uD83D\uDCCA"));

        sidebar.add(Box.createVerticalGlue());

        // User information
        JPanel userCard = new JPanel();
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        userCard.setBackground(UIStyle.SIDEBAR_HOVER);
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        userCard.setMaximumSize(new Dimension(206, 70));
        userCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel name = new JLabel(
                SessionManager.getCurrentUser() != null
                        ? SessionManager.getCurrentUser().getFullName()
                        : "Admin"
        );

        name.setForeground(Color.WHITE);
        name.setFont(UIStyle.FONT_SUBHEAD.deriveFont(13f));

        JLabel roleTag = new JLabel("Administrator");
        roleTag.setForeground(UIStyle.ACCENT);
        roleTag.setFont(UIStyle.FONT_SMALL);

        userCard.add(name);
        userCard.add(roleTag);

        /*
         * Developer footer
         */
        JPanel studentFooter = new JPanel();
        studentFooter.setLayout(new BoxLayout(studentFooter, BoxLayout.Y_AXIS));
        studentFooter.setOpaque(false);
        studentFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        studentFooter.setBorder(new EmptyBorder(0, 14, 0, 0));
        studentFooter.setMaximumSize(new Dimension(206, 42));

        JLabel developerName = new JLabel("Developed by @ugeshan");
        developerName.setFont(UIStyle.FONT_SMALL);
        developerName.setForeground(UIStyle.SIDEBAR_TEXT);

        JLabel studentNumber = new JLabel("Student No. 402313594");
        studentNumber.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );
        studentNumber.setForeground(UIStyle.SIDEBAR_MUTED);

        studentFooter.add(developerName);
        studentFooter.add(studentNumber);

        // Logout button
        JButton logout = new JButton("Log Out");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(206, 36));
        logout.setFocusPainted(false);
        logout.setBackground(UIStyle.DANGER);
        logout.setForeground(Color.WHITE);
        logout.setBorder(new EmptyBorder(8, 12, 8, 12));
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> logout());

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(leftAligned(userCard));

        // Developer details above logout
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        sidebar.add(leftAligned(studentFooter));

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(leftAligned(logout));

        return sidebar;
    }

    private JPanel leftAligned(JComponent comp) {

        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBorder(new EmptyBorder(0, 12, 0, 12));
        p.add(comp);

        return p;
    }

    private JButton navItem(String label, String icon) {

        JButton btn = new JButton("  " + icon + "   " + label);

        btn.setFont(UIStyle.FONT_NAV);
        btn.setForeground(UIStyle.SIDEBAR_TEXT);
        btn.setBackground(UIStyle.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 44));
        btn.setPreferredSize(new Dimension(230, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> showCard(label));

        navButtons.put(label, btn);

        return btn;
    }

    private void highlightNav(String selected) {

        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {

            boolean active = entry.getKey().equals(selected);

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

    // ---------------------------------------------------------------------
    private JPanel buildContent() {

        contentPanel.setBackground(UIStyle.BG_LIGHT);

        overviewPanel = buildOverviewPanel();

        contentPanel.add(
                wrapScroll(overviewPanel),
                "Overview"
        );

        contentPanel.add(
                new ManageMedicinesPanel(),
                "Medicines"
        );

        contentPanel.add(
                new ManageSuppliersPanel(),
                "Suppliers"
        );

        contentPanel.add(
                new ManageUsersPanel(),
                "Users"
        );

        contentPanel.add(
                new ReportsPanel(),
                "Reports"
        );

        return contentPanel;
    }

    private JScrollPane wrapScroll(JPanel p) {

        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        return sp;
    }

    private void showCard(String name) {

        if (name.equals("Overview")) {
            refreshOverview();
        }

        cardLayout.show(contentPanel, name);
        highlightNav(name);
    }

    // ---------------------------------------------------------------------
    private JPanel buildOverviewPanel() {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(UIStyle.BG_LIGHT);

        panel.setBorder(
                new EmptyBorder(
                        28,
                        32,
                        28,
                        32
                )
        );

        return panel;
    }

    private void refreshOverview() {

        overviewPanel.removeAll();

        JLabel title = UIStyle.heading("Dashboard Overview");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = UIStyle.muted(
                "A quick snapshot of today's pharmacy performance"
        );

        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sub.setBorder(
                new EmptyBorder(
                        2,
                        0,
                        20,
                        0
                )
        );

        overviewPanel.add(title);
        overviewPanel.add(sub);

        SalesDAO salesDAO = new SalesDAO();
        MedicineDAO medDAO = new MedicineDAO();

        BigDecimal todaySales = BigDecimal.ZERO;
        int todayCount = 0;

        List<com.healthfirst.pims.model.Medicine> lowStock = List.of();
        List<com.healthfirst.pims.model.Medicine> expiring = List.of();

        String errorMsg = null;

        try {

            todaySales = salesDAO.getTodayTotalSales();
            todayCount = salesDAO.getTodayTransactionCount();

            lowStock = medDAO.getLowStock();
            expiring = medDAO.getExpiringWithinDays(30);

        } catch (RuntimeException ex) {

            errorMsg = ex.getMessage();
        }

        JPanel statsRow =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                16,
                                0
                        )
                );

        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        110
                )
        );

        statsRow.add(
                statCard(
                        "Today's Revenue",
                        "R " + todaySales.setScale(
                                2,
                                java.math.RoundingMode.HALF_UP
                        ),
                        UIStyle.PRIMARY
                )
        );

        statsRow.add(
                statCard(
                        "Transactions Today",
                        String.valueOf(todayCount),
                        UIStyle.ACCENT
                )
        );

        statsRow.add(
                statCard(
                        "Low Stock Items",
                        String.valueOf(lowStock.size()),
                        UIStyle.WARNING
                )
        );

        statsRow.add(
                statCard(
                        "Expiring (30 days)",
                        String.valueOf(expiring.size()),
                        UIStyle.DANGER
                )
        );

        overviewPanel.add(statsRow);

        overviewPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 24)
                )
        );

        if (errorMsg != null) {

            JPanel err = UIStyle.card();
            err.setLayout(new BorderLayout());
            err.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel el = new JLabel(
                    "<html>Could not load live data — check your database connection.<br>"
                    + errorMsg
                    + "</html>"
            );

            el.setForeground(UIStyle.DANGER);

            err.add(el);

            overviewPanel.add(err);

            overviewPanel.add(
                    Box.createRigidArea(
                            new Dimension(0, 20)
                    )
            );
        }

        JPanel twoCol =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                20,
                                0
                        )
                );

        twoCol.setOpaque(false);
        twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);

        twoCol.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        320
                )
        );

        twoCol.add(
                buildAlertList(
                        "⚠ Low Stock Alerts",
                        lowStock,
                        true
                )
        );

        twoCol.add(
                buildAlertList(
                        "⏰ Expiring Within 30 Days",
                        expiring,
                        false
                )
        );

        overviewPanel.add(twoCol);

        overviewPanel.revalidate();
        overviewPanel.repaint();
    }

    private JPanel statCard(
            String label,
            String value,
            Color accent
    ) {

        JPanel card = UIStyle.card();

        card.setLayout(
                new BorderLayout()
        );

        JLabel valueLbl = new JLabel(value);

        valueLbl.setFont(
                UIStyle.FONT_TITLE
        );

        valueLbl.setForeground(accent);

        JLabel labelLbl =
                UIStyle.muted(label);

        JPanel textPanel = new JPanel();

        textPanel.setOpaque(false);

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.add(valueLbl);

        textPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 4)
                )
        );

        textPanel.add(labelLbl);

        card.add(
                textPanel,
                BorderLayout.CENTER
        );

        return card;
    }

    private JPanel buildAlertList(
            String title,
            List<com.healthfirst.pims.model.Medicine> items,
            boolean isLowStock
    ) {

        JPanel card = UIStyle.card();

        card.setLayout(
                new BorderLayout(
                        0,
                        10
                )
        );

        JLabel t = UIStyle.subheading(title);

        card.add(
                t,
                BorderLayout.NORTH
        );

        JPanel list = new JPanel();

        list.setOpaque(false);

        list.setLayout(
                new BoxLayout(
                        list,
                        BoxLayout.Y_AXIS
                )
        );

        if (items.isEmpty()) {

            list.add(
                    UIStyle.muted(
                            "Nothing to show — all good!"
                    )
            );

        } else {

            int shown = 0;

            for (
                    com.healthfirst.pims.model.Medicine m
                    : items
            ) {

                if (shown++ >= 6) {

                    list.add(
                            UIStyle.muted(
                                    "+ "
                                    + (items.size() - 6)
                                    + " more..."
                            )
                    );

                    break;
                }

                JPanel row =
                        new JPanel(
                                new BorderLayout()
                        );

                row.setOpaque(false);

                row.setBorder(
                        new EmptyBorder(
                                4,
                                0,
                                4,
                                0
                        )
                );

                JLabel name =
                        new JLabel(
                                m.getName()
                        );

                name.setFont(
                        UIStyle.FONT_BODY
                );

                String detail =
                        isLowStock
                                ? m.getQuantityInStock()
                                + " left (reorder at "
                                + m.getReorderLevel()
                                + ")"
                                : "Expires "
                                + m.getExpiryDate();

                JLabel badge =
                        UIStyle.badge(
                                detail,
                                isLowStock
                                        ? UIStyle.WARNING
                                        : UIStyle.DANGER,
                                isLowStock
                                        ? UIStyle.WARNING_BG
                                        : UIStyle.DANGER_BG
                        );

                row.add(
                        name,
                        BorderLayout.WEST
                );

                row.add(
                        badge,
                        BorderLayout.EAST
                );

                list.add(row);
            }
        }

        card.add(
                list,
                BorderLayout.CENTER
        );

        return card;
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

        if (confirm == JOptionPane.YES_OPTION) {

            SessionManager.logout();

            dispose();

            new LoginFrame().setVisible(true);
        }
    }
}