package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.SupplierDAO;
import com.healthfirst.pims.model.Supplier;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

//Ugeshan Naicker Student No.402313594

public class ManageSuppliersPanel extends JPanel {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField = new JTextField();

    private List<Supplier> currentList;

    public ManageSuppliersPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIStyle.BG_LIGHT);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        add(buildHeader(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Contact Person", "Phone", "Email", "Address"}, 0) {

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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER, 1, true)
        );

        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);

        // Title
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        titleBlock.add(UIStyle.heading("Manage Suppliers"));
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(
                UIStyle.muted("Keep your supplier contact details up to date")
        );

        header.add(titleBlock, BorderLayout.NORTH);

        // Actions
        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        actions.setOpaque(false);

        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIStyle.BORDER, 1),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );

        searchField.addActionListener(e -> search());

        JButton searchBtn = UIStyle.secondaryButton("Search");
        searchBtn.addActionListener(e -> search());

        JButton resetBtn = UIStyle.secondaryButton("Reset");
        resetBtn.addActionListener(e -> resetSearch());

        JButton addBtn = UIStyle.primaryButton("+ Add Supplier");
        addBtn.addActionListener(e -> openAddDialog());

        JButton editBtn = UIStyle.secondaryButton("Edit Selected");
        editBtn.addActionListener(e -> openEditDialog());

        JButton deleteBtn = UIStyle.dangerButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelected());

        actions.add(searchField);
        actions.add(searchBtn);
        actions.add(resetBtn);
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);

        header.add(actions, BorderLayout.CENTER);

        return header;
    }

    public void loadData() {

        try {
            currentList = supplierDAO.getAllSuppliers();

            displaySuppliers(currentList);

        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void displaySuppliers(List<Supplier> suppliers) {

        tableModel.setRowCount(0);

        for (Supplier s : suppliers) {

            tableModel.addRow(new Object[]{
                    s.getSupplierId(),
                    s.getName(),
                    s.getContactPerson(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getAddress()
            });
        }
    }

    private void search() {

        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            displaySuppliers(currentList);
            return;
        }

        List<Supplier> filtered = currentList.stream()
                .filter(s ->
                        String.valueOf(s.getSupplierId()).contains(keyword)
                                || safe(s.getName()).contains(keyword)
                                || safe(s.getContactPerson()).contains(keyword)
                                || safe(s.getPhone()).contains(keyword)
                                || safe(s.getEmail()).contains(keyword)
                                || safe(s.getAddress()).contains(keyword)
                )
                .collect(Collectors.toList());

        displaySuppliers(filtered);
    }

    private void resetSearch() {

        searchField.setText("");
        displaySuppliers(currentList);
        table.clearSelection();
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.toLowerCase();
    }

    private Supplier getSelected() {

        int row = table.getSelectedRow();

        if (row < 0) {
            return null;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        return currentList.stream()
                .filter(s -> s.getSupplierId() == id)
                .findFirst()
                .orElse(null);
    }

    private void openAddDialog() {

        SupplierDialog dialog = new SupplierDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null
        );

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void openEditDialog() {

        Supplier selected = getSelected();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        SupplierDialog dialog = new SupplierDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                selected
        );

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
        }
    }

    private void deleteSelected() {

        Supplier selected = getSelected();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete supplier \"" + selected.getName()
                        + "\"? Medicines linked to it will keep their record but lose the supplier reference.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                supplierDAO.deleteSupplier(selected.getSupplierId());

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
}