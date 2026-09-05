package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.MedicineDAO;
import com.healthfirst.pims.dao.SupplierDAO;
import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.model.Supplier;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Ugeshan Naicker Student No.402313594
 * Dialog used for adding a new medicine or editing an existing medicine.
 */
public class MedicineDialog extends JDialog {

    private static final String[] TYPES = {
            "Tablet",
            "Capsule",
            "Syrup",
            "Injection",
            "Cream",
            "Ointment",
            "Drops",
            "Other"
    };

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private final JTextField nameField = new JTextField();
    private final JTextField companyField = new JTextField();
    private final JComboBox<String> typeCombo =
            new JComboBox<>(TYPES);
    private final JTextField priceField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JTextField reorderField = new JTextField();
    private final JTextField expiryField = new JTextField();
    private final JComboBox<Supplier> supplierCombo =
            new JComboBox<>();

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final Medicine editingMedicine;

    private boolean saved = false;

    public MedicineDialog(Frame owner, Medicine existing) {

        super(
                owner,
                existing == null
                        ? "Add New Medicine"
                        : "Edit Medicine",
                true
        );

        this.editingMedicine = existing;

        setSize(460, 560);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(
                new BoxLayout(content, BoxLayout.Y_AXIS)
        );
        content.setBorder(
                new EmptyBorder(24, 28, 24, 28)
        );

        // Title
        String title = existing == null
                ? "Add New Medicine"
                : "Edit Medicine";

        content.add(UIStyle.heading(title));
        content.add(
                Box.createRigidArea(
                        new Dimension(0, 16)
                )
        );

        // Load suppliers before populating an existing medicine
        loadSuppliers();

        // Form fields
        content.add(
                fieldBlock(
                        "Medicine Name",
                        nameField
                )
        );

        content.add(
                fieldBlock(
                        "Company / Manufacturer",
                        companyField
                )
        );

        content.add(
                fieldBlock(
                        "Type",
                        typeCombo
                )
        );

        content.add(
                fieldBlock(
                        "Price (R)",
                        priceField
                )
        );

        content.add(
                fieldBlock(
                        "Quantity in Stock",
                        stockField
                )
        );

        content.add(
                fieldBlock(
                        "Reorder Level",
                        reorderField
                )
        );

        content.add(
                fieldBlock(
                        "Expiry Date (YYYY-MM-DD)",
                        expiryField
                )
        );

        content.add(
                fieldBlock(
                        "Supplier",
                        supplierCombo
                )
        );

        // Fill fields when editing
        if (existing != null) {
            populateFields(existing);
        }

        // Buttons
        JPanel buttonRow = new JPanel(
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
                        12,
                        0,
                        0,
                        0
                )
        );

        JButton cancelButton =
                UIStyle.secondaryButton("Cancel");

        cancelButton.addActionListener(
                e -> dispose()
        );

        JButton saveButton =
                UIStyle.primaryButton("Save Medicine");

        saveButton.addActionListener(
                e -> onSave()
        );

        buttonRow.add(cancelButton);
        buttonRow.add(saveButton);

        content.add(buttonRow);

        // Allow the form to scroll if necessary
        JScrollPane scrollPane =
                new JScrollPane(content);

        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(12);

        setContentPane(scrollPane);

        // Start with the name field selected
        SwingUtilities.invokeLater(
                () -> nameField.requestFocusInWindow()
        );
    }

    private void loadSuppliers() {

        // First option allows a medicine to have no supplier
        supplierCombo.addItem(null);

        try {

            SupplierDAO supplierDAO =
                    new SupplierDAO();

            List<Supplier> suppliers =
                    supplierDAO.getAllSuppliers();

            for (Supplier supplier : suppliers) {
                supplierCombo.addItem(supplier);
            }

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load suppliers: "
                            + ex.getMessage(),
                    "Supplier Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        supplierCombo.setRenderer(
                new DefaultListCellRendererNullSafe()
        );
    }

    private void populateFields(Medicine medicine) {

        nameField.setText(
                medicine.getName() != null
                        ? medicine.getName()
                        : ""
        );

        companyField.setText(
                medicine.getCompany() != null
                        ? medicine.getCompany()
                        : ""
        );

        typeCombo.setSelectedItem(
                medicine.getMedicineType()
        );

        priceField.setText(
                medicine.getPrice() != null
                        ? medicine.getPrice().toPlainString()
                        : ""
        );

        stockField.setText(
                String.valueOf(
                        medicine.getQuantityInStock()
                )
        );

        reorderField.setText(
                String.valueOf(
                        medicine.getReorderLevel()
                )
        );

        expiryField.setText(
                medicine.getExpiryDate() != null
                        ? medicine.getExpiryDate().format(FMT)
                        : ""
        );

        // Select the existing supplier
        if (medicine.getSupplierId() != null) {

            for (int i = 0;
                 i < supplierCombo.getItemCount();
                 i++) {

                Supplier supplier =
                        supplierCombo.getItemAt(i);

                if (supplier != null
                        && supplier.getSupplierId()
                        == medicine.getSupplierId()) {

                    supplierCombo.setSelectedItem(
                            supplier
                    );

                    break;
                }
            }
        }
    }

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
                        10,
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

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        34
                )
        );

        if (field instanceof JTextField textField) {

            textField.setBorder(
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

        block.add(labelComponent);
        block.add(field);

        return block;
    }

    private void onSave() {

        String name =
                nameField.getText().trim();

        String company =
                companyField.getText().trim();

        String type =
                (String) typeCombo.getSelectedItem();

        String priceText =
                priceField.getText().trim();

        String stockText =
                stockField.getText().trim();

        String reorderText =
                reorderField.getText().trim();

        String expiryText =
                expiryField.getText().trim();

        Supplier supplier =
                (Supplier) supplierCombo.getSelectedItem();

        // -------------------------------
        // Basic validation
        // -------------------------------

        if (name.isEmpty()) {

            validationError(
                    "Medicine name is required."
            );

            nameField.requestFocusInWindow();
            return;
        }

        if (type == null || type.trim().isEmpty()) {

            validationError(
                    "Please select a medicine type."
            );

            return;
        }

        // -------------------------------
        // Price validation
        // -------------------------------

        BigDecimal price;

        try {

            if (priceText.isEmpty()) {
                throw new NumberFormatException();
            }

            price = new BigDecimal(priceText);

            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            validationError(
                    "Enter a valid, non-negative price."
            );

            priceField.requestFocusInWindow();
            return;
        }

        // -------------------------------
        // Stock validation
        // -------------------------------

        int stock;

        try {

            if (stockText.isEmpty()) {
                throw new NumberFormatException();
            }

            stock = Integer.parseInt(stockText);

            if (stock < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            validationError(
                    "Enter a valid, non-negative stock quantity."
            );

            stockField.requestFocusInWindow();
            return;
        }

        // -------------------------------
        // Reorder level validation
        // -------------------------------

        int reorder;

        try {

            if (reorderText.isEmpty()) {
                throw new NumberFormatException();
            }

            reorder = Integer.parseInt(reorderText);

            if (reorder < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            validationError(
                    "Enter a valid, non-negative reorder level."
            );

            reorderField.requestFocusInWindow();
            return;
        }

        // -------------------------------
        // Expiry date validation
        // -------------------------------

        LocalDate expiry = null;

        if (!expiryText.isEmpty()) {

            try {

                expiry =
                        LocalDate.parse(
                                expiryText,
                                FMT
                        );

            } catch (DateTimeParseException ex) {

                validationError(
                        "Expiry date must be in YYYY-MM-DD format."
                );

                expiryField.requestFocusInWindow();
                return;
            }
        }

        // -------------------------------
        // Create/update medicine
        // -------------------------------

        Medicine medicine;

        if (editingMedicine != null) {
            medicine = editingMedicine;
        } else {
            medicine = new Medicine();
        }

        medicine.setName(name);
        medicine.setCompany(company);
        medicine.setMedicineType(type);
        medicine.setPrice(price);
        medicine.setQuantityInStock(stock);
        medicine.setReorderLevel(reorder);
        medicine.setExpiryDate(expiry);

        if (supplier != null) {
            medicine.setSupplierId(
                    supplier.getSupplierId()
            );
        } else {
            medicine.setSupplierId(null);
        }

        // -------------------------------
        // Save to database
        // -------------------------------

        try {

            if (editingMedicine == null) {

                medicineDAO.addMedicine(medicine);

            } else {

                medicineDAO.updateMedicine(medicine);
            }

            saved = true;

            JOptionPane.showMessageDialog(
                    this,
                    editingMedicine == null
                            ? "Medicine added successfully."
                            : "Medicine updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save medicine: "
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validationError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public boolean isSaved() {
        return saved;
    }

    /**
     * Displays supplier names correctly when the combo box
     * contains a null "No Supplier" option.
     */
    private static class DefaultListCellRendererNullSafe
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {

            String text;

            if (value == null) {

                text = "(No Supplier)";

            } else {

                Supplier supplier =
                        (Supplier) value;

                text = supplier.getName();
            }

            return super.getListCellRendererComponent(
                    list,
                    text,
                    index,
                    isSelected,
                    cellHasFocus
            );
        }
    }
}