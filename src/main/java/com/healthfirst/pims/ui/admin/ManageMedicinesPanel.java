package com.healthfirst.pims.ui.admin;

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
 * Administrator screen for full CRUD management of the medicines inventory.
 */
public class ManageMedicinesPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField = new JTextField();
    private List<Medicine> currentList;

    public ManageMedicinesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIStyle.BG_LIGHT);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        add(buildHeader(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Name",
                        "Company",
                        "Type",
                        "Price (R)",
                        "Stock",
                        "Reorder Lvl",
                        "Expiry",
                        "Supplier",
                        "Status"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(UIStyle.FONT_BODY);

        table.setSelectionBackground(UIStyle.PRIMARY_LIGHT);
        table.setSelectionForeground(UIStyle.TEXT_DARK);

        table.getColumnModel().getColumn(0).setMaxWidth(50);

        UIStyle.styleTableHeader(table.getTableHeader());

        table.getColumnModel()
                .getColumn(9)
                .setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);

        // ---------------------------------------------------------
        // Title and subtitle
        // ---------------------------------------------------------

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(
                new BoxLayout(titleBlock, BoxLayout.Y_AXIS)
        );

        JLabel title = UIStyle.heading("Manage Medicines");

        JLabel sub = UIStyle.muted(
                "Add, edit, and remove medicines from your inventory"
        );

        titleBlock.add(title);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBlock.add(sub);

        header.add(titleBlock, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // Search and action buttons
        // ---------------------------------------------------------

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        0
                )
        );

        actions.setOpaque(false);

        searchField.setPreferredSize(
                new Dimension(220, 36)
        );

        searchField.setBorder(
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

        searchField.addActionListener(
                e -> search()
        );

        JButton searchBtn =
                UIStyle.secondaryButton("Search");

        searchBtn.addActionListener(
                e -> search()
        );

        JButton resetBtn =
                UIStyle.secondaryButton("Reset");

        resetBtn.addActionListener(
                e -> {
                    searchField.setText("");
                    loadData();
                }
        );

        JButton addBtn =
                UIStyle.primaryButton("+ Add Medicine");

        addBtn.addActionListener(
                e -> openAddDialog()
        );

        JButton editBtn =
                UIStyle.secondaryButton("Edit Selected");

        editBtn.addActionListener(
                e -> openEditDialog()
        );

        JButton deleteBtn =
                UIStyle.dangerButton("Delete Selected");

        deleteBtn.addActionListener(
                e -> deleteSelected()
        );

        actions.add(searchField);
        actions.add(searchBtn);
        actions.add(resetBtn);
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);

        header.add(actions, BorderLayout.CENTER);

        return header;
    }

    private void search() {

        String kw = searchField.getText().trim();

        if (kw.isEmpty()) {
            loadData();
            return;
        }

        try {

            currentList =
                    medicineDAO.searchMedicines(kw);

            populateTable(currentList);

        } catch (RuntimeException ex) {

            showError(ex);
        }
    }

    public void loadData() {

        try {

            currentList =
                    medicineDAO.getAllMedicines();

            populateTable(currentList);

        } catch (RuntimeException ex) {

            showError(ex);
        }
    }

    private void populateTable(List<Medicine> meds) {

        tableModel.setRowCount(0);

        for (Medicine m : meds) {

            String status =
                    m.isExpired()
                            ? "EXPIRED"
                            : m.isExpiringSoon(30)
                            ? "EXPIRING SOON"
                            : m.isLowStock()
                            ? "LOW STOCK"
                            : "OK";

            tableModel.addRow(
                    new Object[]{
                            m.getMedicineId(),
                            m.getName(),
                            m.getCompany(),
                            m.getMedicineType(),
                            m.getPrice(),
                            m.getQuantityInStock(),
                            m.getReorderLevel(),
                            m.getExpiryDate() != null
                                    ? m.getExpiryDate().toString()
                                    : "-",
                            m.getSupplierName() != null
                                    ? m.getSupplierName()
                                    : "-",
                            status
                    }
            );
        }
    }

    private Medicine getSelectedMedicine() {

        int row = table.getSelectedRow();

        if (row < 0) {
            return null;
        }

        int id =
                (int) tableModel.getValueAt(row, 0);

        return currentList.stream()
                .filter(
                        m -> m.getMedicineId() == id
                )
                .findFirst()
                .orElse(null);
    }

    private void openAddDialog() {

        MedicineDialog dialog =
                new MedicineDialog(
                        (Frame) SwingUtilities
                                .getWindowAncestor(this),
                        null
                );

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void openEditDialog() {

        Medicine selected =
                getSelectedMedicine();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        MedicineDialog dialog =
                new MedicineDialog(
                        (Frame) SwingUtilities
                                .getWindowAncestor(this),
                        selected
                );

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void deleteSelected() {

        Medicine selected =
                getSelectedMedicine();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete \"" +
                                selected.getName() +
                                "\" permanently? This cannot be undone.",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                medicineDAO.deleteMedicine(
                        selected.getMedicineId()
                );

                loadData();

            } catch (RuntimeException ex) {

                showError(ex);
            }
        }
    }

    private void showError(RuntimeException ex) {

        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Renders the status column with clear colours.
     */
    private static class StatusCellRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JLabel label =
                    (JLabel) super
                            .getTableCellRendererComponent(
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

            String status =
                    String.valueOf(value);

            switch (status) {

                case "EXPIRED" -> {
                    label.setForeground(Color.WHITE);
                    label.setBackground(
                            UIStyle.DANGER
                    );
                    label.setOpaque(true);
                }

                case "EXPIRING SOON" -> {
                    label.setForeground(
                            UIStyle.DANGER
                    );
                    label.setBackground(
                            UIStyle.DANGER_BG
                    );
                    label.setOpaque(true);
                }

                case "LOW STOCK" -> {
                    label.setForeground(
                            new Color(150, 100, 0)
                    );
                    label.setBackground(
                            UIStyle.WARNING_BG
                    );
                    label.setOpaque(true);
                }

                default -> {
                    label.setForeground(
                            UIStyle.SUCCESS
                    );
                    label.setBackground(
                            UIStyle.SUCCESS_BG
                    );
                    label.setOpaque(true);
                }
            }

            if (isSelected) {
                label.setBackground(
                        label.getBackground().darker()
                );
            }

            return label;
        }
    }
}