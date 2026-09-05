package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.SupplierDAO;
import com.healthfirst.pims.model.Supplier;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//Ugeshan Naicker Student No.402313594
public class SupplierDialog extends JDialog {

    private final JTextField nameField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextArea addressArea = new JTextArea(4, 20);

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final Supplier editingSupplier;

    private boolean saved = false;

    public SupplierDialog(Frame owner, Supplier existing) {

        super(
                owner,
                existing == null
                        ? "Add New Supplier"
                        : "Edit Supplier",
                true
        );

        this.editingSupplier = existing;

        setSize(440, 510);
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

        JLabel title = UIStyle.heading(
                existing == null
                        ? "Add New Supplier"
                        : "Edit Supplier"
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
        // FIELDS
        // ---------------------------------------------------------

        content.add(
                fieldBlock(
                        "Supplier Name",
                        nameField
                )
        );

        content.add(
                fieldBlock(
                        "Contact Person",
                        contactField
                )
        );

        content.add(
                fieldBlock(
                        "Phone",
                        phoneField
                )
        );

        content.add(
                fieldBlock(
                        "Email",
                        emailField
                )
        );

        content.add(
                fieldBlockArea(
                        "Address",
                        addressArea
                )
        );

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
                        8,
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
                UIStyle.primaryButton(
                        "Save Supplier"
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
                () -> nameField.requestFocusInWindow()
        );
    }

    // =============================================================
    // POPULATE EXISTING SUPPLIER
    // =============================================================

    private void populateFields(Supplier s) {

        nameField.setText(
                s.getName() != null
                        ? s.getName()
                        : ""
        );

        contactField.setText(
                s.getContactPerson() != null
                        ? s.getContactPerson()
                        : ""
        );

        phoneField.setText(
                s.getPhone() != null
                        ? s.getPhone()
                        : ""
        );

        emailField.setText(
                s.getEmail() != null
                        ? s.getEmail()
                        : ""
        );

        addressArea.setText(
                s.getAddress() != null
                        ? s.getAddress()
                        : ""
        );
    }

    // =============================================================
    // TEXT FIELD BLOCK
    // =============================================================

    private JPanel fieldBlock(
            String label,
            JTextField field
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

        block.add(labelComponent);
        block.add(field);

        return block;
    }

    // =============================================================
    // ADDRESS FIELD BLOCK
    // =============================================================

    private JPanel fieldBlockArea(
            String label,
            JTextArea area
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
                        8,
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

        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setFont(
                UIStyle.FONT_BODY
        );

        area.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyle.BORDER,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                6,
                                10,
                                6,
                                10
                        )
                )
        );

        JScrollPane addressScroll =
                new JScrollPane(area);

        addressScroll.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        addressScroll.setBorder(null);

        addressScroll.setPreferredSize(
                new Dimension(
                        0,
                        80
                )
        );

        addressScroll.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        80
                )
        );

        block.add(labelComponent);
        block.add(addressScroll);

        return block;
    }

    // =============================================================
    // SAVE
    // =============================================================

    private void onSave() {

        String name =
                nameField.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            nameField.requestFocusInWindow();
            return;
        }

        Supplier supplier;

        if (editingSupplier != null) {
            supplier = editingSupplier;
        } else {
            supplier = new Supplier();
        }

        supplier.setName(name);

        supplier.setContactPerson(
                contactField
                        .getText()
                        .trim()
        );

        supplier.setPhone(
                phoneField
                        .getText()
                        .trim()
        );

        supplier.setEmail(
                emailField
                        .getText()
                        .trim()
        );

        supplier.setAddress(
                addressArea
                        .getText()
                        .trim()
        );

        try {

            if (editingSupplier == null) {

                supplierDAO.addSupplier(
                        supplier
                );

            } else {

                supplierDAO.updateSupplier(
                        supplier
                );
            }

            saved = true;

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save supplier:\n"
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