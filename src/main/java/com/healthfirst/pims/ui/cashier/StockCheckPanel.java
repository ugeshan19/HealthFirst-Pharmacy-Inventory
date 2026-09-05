package com.healthfirst.pims.ui.cashier;

import com.healthfirst.pims.dao.MedicineDAO;
import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ugeshan Naicker Student No.402313594
 * Read-only screen allowing Cashiers to quickly look up a medicine's
 * price and availability without going through a sale.
 * Cashiers cannot add, edit, or delete medicines from here.
 */
public class StockCheckPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField();

    public StockCheckPanel() {

        setLayout(new BorderLayout(0, 16));
        setBackground(UIStyle.BG_LIGHT);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        // Title
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(
                new BoxLayout(titleBlock, BoxLayout.Y_AXIS)
        );

        titleBlock.add(UIStyle.heading("Stock Check"));
        titleBlock.add(
                UIStyle.muted(
                        "Look up price and availability — view only"
                )
        );

        // Search row
        JPanel searchRow =
                new JPanel(new BorderLayout(8, 0));

        searchRow.setOpaque(false);
        searchRow.setBorder(
                new EmptyBorder(16, 0, 16, 0)
        );

        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyle.BORDER,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        searchField.addActionListener(
                e -> search()
        );

        JButton searchBtn =
                UIStyle.primaryButton("Search");

        searchBtn.addActionListener(
                e -> search()
        );

        JButton resetBtn =
                UIStyle.secondaryButton("Show All");

        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadAll();
        });

        JPanel searchInner =
                new JPanel(new BorderLayout(8, 0));

        searchInner.setOpaque(false);

        // Simple label instead of an emoji
        searchInner.add(
                new JLabel("Search:"),
                BorderLayout.WEST
        );

        searchInner.add(
                searchField,
                BorderLayout.CENTER
        );

        JPanel btns =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        btns.setOpaque(false);
        btns.add(searchBtn);
        btns.add(resetBtn);

        searchRow.add(
                searchInner,
                BorderLayout.CENTER
        );

        searchRow.add(
                btns,
                BorderLayout.EAST
        );

        JPanel northWrap =
                new JPanel(new BorderLayout());

        northWrap.setOpaque(false);

        northWrap.add(
                titleBlock,
                BorderLayout.NORTH
        );

        northWrap.add(
                searchRow,
                BorderLayout.SOUTH
        );

        add(
                northWrap,
                BorderLayout.NORTH
        );

        // Table
        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "Medicine",
                                "Company",
                                "Type",
                                "Price (R)",
                                "Availability"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int r,
                            int c
                    ) {
                        return false;
                    }
                };

        JTable table =
                new JTable(tableModel);

        table.setRowHeight(34);
        table.setFont(UIStyle.FONT_BODY);

        // Selection colours
        table.setSelectionBackground(
                UIStyle.PRIMARY_LIGHT
        );

        // FIX: keep selected row text readable
        table.setSelectionForeground(
                UIStyle.TEXT_DARK
        );

        UIStyle.styleTableHeader(
                table.getTableHeader()
        );

        table.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new AvailabilityRenderer()
                );

        JScrollPane scroll =
                new JScrollPane(table);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        add(
                scroll,
                BorderLayout.CENTER
        );

        loadAll();
    }

    private void loadAll() {

        try {

            populate(
                    medicineDAO.getAllMedicines()
            );

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load medicines: "
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void search() {

        String kw =
                searchField.getText().trim();

        if (kw.isEmpty()) {
            loadAll();
            return;
        }

        try {

            populate(
                    medicineDAO.searchMedicines(kw)
            );

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Search failed: "
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populate(
            List<Medicine> meds
    ) {

        tableModel.setRowCount(0);

        for (Medicine m : meds) {

            String availability;

            if (m.getQuantityInStock() <= 0) {

                availability = "Out of Stock";

            } else if (m.isLowStock()) {

                availability =
                        "Low Stock ("
                                + m.getQuantityInStock()
                                + ")";

            } else {

                availability =
                        "In Stock ("
                                + m.getQuantityInStock()
                                + ")";
            }

            tableModel.addRow(
                    new Object[]{
                            m.getName(),
                            m.getCompany(),
                            m.getMedicineType(),
                            m.getPrice(),
                            availability
                    }
            );
        }
    }

    /**
     * Renderer for the Availability column.
     */
    private static class AvailabilityRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel)
                            super.getTableCellRendererComponent(
                                    table,
                                    value,
                                    isSelected,
                                    hasFocus,
                                    row,
                                    column
                            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    UIStyle.FONT_SMALL.deriveFont(
                            Font.BOLD
                    )
            );

            String text =
                    String.valueOf(value);

            label.setOpaque(true);

            if (text.startsWith("Out")) {

                label.setForeground(Color.WHITE);
                label.setBackground(
                        UIStyle.DANGER
                );

            } else if (text.startsWith("Low")) {

                label.setForeground(
                        new Color(150, 100, 0)
                );

                label.setBackground(
                        UIStyle.WARNING_BG
                );

            } else {

                label.setForeground(
                        UIStyle.SUCCESS
                );

                label.setBackground(
                        UIStyle.SUCCESS_BG
                );
            }

            return label;
        }
    }
}