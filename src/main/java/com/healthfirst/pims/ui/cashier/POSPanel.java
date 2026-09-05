package com.healthfirst.pims.ui.cashier;

import com.healthfirst.pims.dao.MedicineDAO;
import com.healthfirst.pims.dao.SalesDAO;
import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.model.Sale;
import com.healthfirst.pims.model.SaleItem;
import com.healthfirst.pims.util.SessionManager;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Ugeshan Naicker Student No.402313594
 * Point-of-Sale screen: search/select medicines on the left,
 * running cart on the right.
 *
 * Checkout produces a Sale (persisted atomically)
 * and opens a printable bill.
 */
public class POSPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SalesDAO salesDAO = new SalesDAO();

    private DefaultTableModel catalogModel;
    private JTable catalogTable;
    private final JTextField searchField = new JTextField();

    private DefaultTableModel cartModel;
    private JTable cartTable;
    private final JLabel totalLabel = new JLabel("R 0.00");

    private final List<Medicine> catalogList = new ArrayList<>();
    private final List<CartLine> cart = new ArrayList<>();

    public POSPanel() {

        setLayout(new BorderLayout(0, 16));
        setBackground(UIStyle.BG_LIGHT);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        // ---------------------------------------------------------
        // Title
        // ---------------------------------------------------------

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(
                new BoxLayout(
                        titleBlock,
                        BoxLayout.Y_AXIS
                )
        );

        titleBlock.add(
                UIStyle.heading("Point of Sale")
        );

        titleBlock.add(
                UIStyle.muted(
                        "Search for medicines, add to cart, and checkout"
                )
        );

        add(
                titleBlock,
                BorderLayout.NORTH
        );

        // ---------------------------------------------------------
        // Main split
        // ---------------------------------------------------------

        JPanel splitPanel =
                new JPanel(
                        new GridLayout(1, 2, 20, 0)
                );

        splitPanel.setOpaque(false);

        splitPanel.add(
                buildCatalogPanel()
        );

        splitPanel.add(
                buildCartPanel()
        );

        add(
                splitPanel,
                BorderLayout.CENTER
        );

        catalogModel =
                (DefaultTableModel)
                        catalogTable.getModel();

        cartModel =
                (DefaultTableModel)
                        cartTable.getModel();

        loadCatalog();
    }

    // ------------------------------------------------------------------
    // Catalog
    // ------------------------------------------------------------------

    private JPanel buildCatalogPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(0, 10)
                );

        panel.setOpaque(false);

        // Search row
        JPanel searchRow =
                new JPanel(
                        new BorderLayout(8, 0)
                );

        searchRow.setOpaque(false);

        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIStyle.BORDER,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                6,
                                12,
                                6,
                                12
                        )
                )
        );

        searchField.addActionListener(
                e -> loadCatalog()
        );

        JButton addBtn =
                UIStyle.primaryButton(
                        "Add to Cart"
                );

        addBtn.addActionListener(
                e -> addSelectedToCart()
        );

        // Simple label instead of emoji
        searchRow.add(
                new JLabel("Search:"),
                BorderLayout.WEST
        );

        searchRow.add(
                searchField,
                BorderLayout.CENTER
        );

        searchRow.add(
                addBtn,
                BorderLayout.EAST
        );

        panel.add(
                searchRow,
                BorderLayout.NORTH
        );

        // Catalog table
        DefaultTableModel model =
                new DefaultTableModel(
                        new Object[]{
                                "Medicine",
                                "Type",
                                "Price (R)",
                                "In Stock"
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
                new JTable(model);

        table.setRowHeight(32);
        table.setFont(UIStyle.FONT_BODY);

        // Selection colours
        table.setSelectionBackground(
                UIStyle.PRIMARY_LIGHT
        );

        // FIX: keep selected text readable
        table.setSelectionForeground(
                UIStyle.TEXT_DARK
        );

        UIStyle.styleTableHeader(
                table.getTableHeader()
        );

        this.catalogTable = table;

        JScrollPane scroll =
                new JScrollPane(table);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        return panel;
    }

    private void loadCatalog() {

        try {

            String kw =
                    searchField
                            .getText()
                            .trim();

            catalogList.clear();

            catalogList.addAll(
                    kw.isEmpty()
                            ? medicineDAO.getAllMedicines()
                            : medicineDAO.searchMedicines(kw)
            );

            catalogModel.setRowCount(0);

            for (Medicine m : catalogList) {

                catalogModel.addRow(
                        new Object[]{
                                m.getName(),
                                m.getMedicineType(),
                                m.getPrice(),
                                m.getQuantityInStock()
                        }
                );
            }

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

    private void addSelectedToCart() {

        int row =
                catalogTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Medicine selected =
                catalogList.get(row);

        if (selected.getQuantityInStock() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    selected.getName()
                            + " is out of stock.",
                    "Out of Stock",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String qtyStr =
                JOptionPane.showInputDialog(
                        this,
                        "Quantity for "
                                + selected.getName()
                                + " (available: "
                                + selected.getQuantityInStock()
                                + "):",
                        "1"
                );

        if (qtyStr == null) {
            return;
        }

        int qty;

        try {

            qty =
                    Integer.parseInt(
                            qtyStr.trim()
                    );

            if (qty <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid positive quantity.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int alreadyInCart =
                cart.stream()
                        .filter(
                                c ->
                                        c.medicine.getMedicineId()
                                                == selected.getMedicineId()
                        )
                        .mapToInt(
                                c -> c.quantity
                        )
                        .sum();

        if (alreadyInCart + qty
                > selected.getQuantityInStock()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Not enough stock. Only "
                            + (
                            selected.getQuantityInStock()
                                    - alreadyInCart
                    )
                            + " more available.",
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        boolean merged = false;

        for (CartLine line : cart) {

            if (line.medicine.getMedicineId()
                    == selected.getMedicineId()) {

                line.quantity += qty;
                merged = true;
                break;
            }
        }

        if (!merged) {

            cart.add(
                    new CartLine(
                            selected,
                            qty
                    )
            );
        }

        refreshCart();
    }

    // ------------------------------------------------------------------
    // Cart
    // ------------------------------------------------------------------

    private JPanel buildCartPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(0, 10)
                );

        panel.setOpaque(false);

        JLabel cartTitle =
                UIStyle.subheading(
                        "Current Sale"
                );

        panel.add(
                cartTitle,
                BorderLayout.NORTH
        );

        DefaultTableModel model =
                new DefaultTableModel(
                        new Object[]{
                                "Medicine",
                                "Qty",
                                "Unit Price",
                                "Line Total"
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
                new JTable(model);

        table.setRowHeight(32);
        table.setFont(UIStyle.FONT_BODY);

        // Selection colours
        table.setSelectionBackground(
                UIStyle.PRIMARY_LIGHT
        );

        // FIX: keep selected cart text readable
        table.setSelectionForeground(
                UIStyle.TEXT_DARK
        );

        UIStyle.styleTableHeader(
                table.getTableHeader()
        );

        this.cartTable = table;

        JScrollPane scroll =
                new JScrollPane(table);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        // ---------------------------------------------------------
        // Bottom section
        // ---------------------------------------------------------

        JPanel bottom =
                new JPanel();

        bottom.setOpaque(false);

        bottom.setLayout(
                new BoxLayout(
                        bottom,
                        BoxLayout.Y_AXIS
                )
        );

        bottom.setBorder(
                new EmptyBorder(
                        12,
                        0,
                        0,
                        0
                )
        );

        JPanel totalRow =
                new JPanel(
                        new BorderLayout()
                );

        totalRow.setOpaque(false);

        JLabel totalCaption =
                UIStyle.subheading("Total:");

        totalLabel.setFont(
                UIStyle.FONT_MONO_LG
        );

        totalLabel.setForeground(
                UIStyle.PRIMARY_DARK
        );

        totalRow.add(
                totalCaption,
                BorderLayout.WEST
        );

        totalRow.add(
                totalLabel,
                BorderLayout.EAST
        );

        bottom.add(totalRow);

        bottom.add(
                Box.createRigidArea(
                        new Dimension(0, 12)
                )
        );

        // Buttons
        JPanel btnRow =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                8,
                                0
                        )
                );

        btnRow.setOpaque(false);

        JButton removeBtn =
                UIStyle.secondaryButton(
                        "Remove Item"
                );

        removeBtn.addActionListener(
                e -> removeSelectedCartItem()
        );

        JButton clearBtn =
                UIStyle.dangerButton(
                        "Clear Cart"
                );

        clearBtn.addActionListener(
                e -> clearCart()
        );

        JButton checkoutBtn =
                UIStyle.primaryButton(
                        "Checkout"
                );

        checkoutBtn.addActionListener(
                e -> checkout()
        );

        btnRow.add(removeBtn);
        btnRow.add(clearBtn);
        btnRow.add(checkoutBtn);

        bottom.add(btnRow);

        panel.add(
                bottom,
                BorderLayout.SOUTH
        );

        return panel;
    }

    private void refreshCart() {

        cartModel.setRowCount(0);

        BigDecimal total =
                BigDecimal.ZERO;

        for (CartLine line : cart) {

            BigDecimal lineTotal =
                    line.medicine
                            .getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            line.quantity
                                    )
                            );

            total =
                    total.add(lineTotal);

            cartModel.addRow(
                    new Object[]{
                            line.medicine.getName(),
                            line.quantity,
                            line.medicine.getPrice(),
                            lineTotal
                    }
            );
        }

        totalLabel.setText(
                "R "
                        + total.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );
    }

    private void removeSelectedCartItem() {

        int row =
                cartTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a cart item to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        cart.remove(row);

        refreshCart();
    }

    private void clearCart() {

        if (cart.isEmpty()) {
            return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Clear the entire cart?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm ==
                JOptionPane.YES_OPTION) {

            cart.clear();

            refreshCart();
        }
    }

    // ------------------------------------------------------------------
    // Checkout
    // ------------------------------------------------------------------

    private void checkout() {

        if (cart.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "The cart is empty.",
                    "Nothing to Checkout",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Sale sale =
                new Sale();

        sale.setUserId(
                SessionManager.getCurrentUser() != null
                        ? SessionManager
                        .getCurrentUser()
                        .getUserId()
                        : null
        );

        BigDecimal total =
                BigDecimal.ZERO;

        List<SaleItem> items =
                new ArrayList<>();

        for (CartLine line : cart) {

            items.add(
                    new SaleItem(
                            line.medicine.getMedicineId(),
                            line.medicine.getName(),
                            line.quantity,
                            line.medicine.getPrice()
                    )
            );

            total =
                    total.add(
                            line.medicine
                                    .getPrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    line.quantity
                                            )
                                    )
                    );
        }

        sale.setItems(items);
        sale.setTotalAmount(total);

        try {

            int saleId =
                    salesDAO.recordSale(sale);

            sale.setSaleId(saleId);

            sale.setSaleDate(
                    java.time.LocalDateTime.now()
            );

            BillDialog bill =
                    new BillDialog(
                            (Frame)
                                    SwingUtilities
                                            .getWindowAncestor(
                                                    this
                                            ),
                            sale,
                            SessionManager
                                    .getCurrentUser() != null
                                    ? SessionManager
                                    .getCurrentUser()
                                    .getFullName()
                                    : "Cashier"
                    );

            bill.setVisible(true);

            cart.clear();

            refreshCart();

            loadCatalog();

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Checkout failed: "
                            + ex.getMessage(),
                    "Transaction Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ------------------------------------------------------------------
    // Cart Line
    // ------------------------------------------------------------------

    private static class CartLine {

        final Medicine medicine;
        int quantity;

        CartLine(
                Medicine medicine,
                int quantity
        ) {
            this.medicine = medicine;
            this.quantity = quantity;
        }
    }
}