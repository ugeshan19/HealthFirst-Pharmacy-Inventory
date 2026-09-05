package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.MedicineDAO;
import com.healthfirst.pims.dao.SalesDAO;
import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.model.Sale;
import com.healthfirst.pims.util.UIStyle;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

//Ugeshan Naicker Student No.402313594
public class ReportsPanel extends JPanel {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private final SalesDAO salesDAO = new SalesDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();

    // =========================================================
    // SALES REPORT
    // =========================================================

    private final JTextField salesStartField =
            new JTextField();

    private final JTextField salesEndField =
            new JTextField();

    private final DefaultTableModel salesTableModel =
            new DefaultTableModel(
                    new Object[]{
                            "Sale ID",
                            "Date / Time",
                            "Cashier",
                            "Total (R)"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

    private final JLabel salesSummaryLabel =
            new JLabel(" ");

    private JTable salesTable;

    // =========================================================
    // ITEM-WISE SALES
    // =========================================================

    private final JTextField itemStartField =
            new JTextField();

    private final JTextField itemEndField =
            new JTextField();

    private final DefaultTableModel itemWiseTableModel =
            new DefaultTableModel(
                    new Object[]{
                            "Medicine",
                            "Quantity Sold",
                            "Revenue (R)"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

    private final JLabel itemWiseSummaryLabel =
            new JLabel(" ");

    private JTable itemWiseTable;

    // =========================================================
    // LOW STOCK
    // =========================================================

    private final DefaultTableModel lowStockTableModel =
            new DefaultTableModel(
                    new Object[]{
                            "Medicine",
                            "Company",
                            "Stock",
                            "Reorder Level",
                            "Supplier"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

    private final JLabel lowStockSummaryLabel =
            new JLabel(" ");

    private JTable lowStockTable;

    // =========================================================
    // EXPIRY
    // =========================================================

    private final DefaultTableModel expiryTableModel =
            new DefaultTableModel(
                    new Object[]{
                            "Medicine",
                            "Company",
                            "Stock",
                            "Expiry Date",
                            "Days Left"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

    private final JComboBox<String> expiryWindowCombo =
            new JComboBox<>(
                    new String[]{
                            "7 days",
                            "14 days",
                            "30 days",
                            "60 days",
                            "90 days"
                    }
            );

    private JTable expiryTable;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportsPanel() {

        setLayout(
                new BorderLayout(
                        0,
                        16
                )
        );

        setBackground(
                UIStyle.BG_LIGHT
        );

        setBorder(
                new EmptyBorder(
                        28,
                        32,
                        28,
                        32
                )
        );

        JPanel titleBlock =
                new JPanel();

        titleBlock.setOpaque(false);

        titleBlock.setLayout(
                new BoxLayout(
                        titleBlock,
                        BoxLayout.Y_AXIS
                )
        );

        titleBlock.add(
                UIStyle.heading("Reports")
        );

        titleBlock.add(
                UIStyle.muted(
                        "Sales performance and inventory reports"
                )
        );

        add(
                titleBlock,
                BorderLayout.NORTH
        );

        JTabbedPane tabs =
                new JTabbedPane();

        tabs.setFont(
                UIStyle.FONT_SUBHEAD
                        .deriveFont(13f)
        );

        tabs.addTab(
                "Sales Report",
                buildSalesReportTab()
        );

        tabs.addTab(
                "Item-Wise Sales",
                buildItemWiseReportTab()
        );

        tabs.addTab(
                "Low Stock",
                buildLowStockReportTab()
        );

        tabs.addTab(
                "Expiry Report",
                buildExpiryReportTab()
        );

        add(
                tabs,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // SALES REPORT
    // =========================================================

    private JPanel buildSalesReportTab() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        panel.setBackground(
                UIStyle.BG_LIGHT
        );

        panel.setBorder(
                new EmptyBorder(
                        16,
                        0,
                        0,
                        0
                )
        );

        LocalDate today =
                LocalDate.now();

        salesStartField.setText(
                today.minusDays(7)
                        .format(FMT)
        );

        salesEndField.setText(
                today.format(FMT)
        );

        styleDateField(
                salesStartField
        );

        styleDateField(
                salesEndField
        );

        JPanel controls =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        controls.setOpaque(false);

        controls.add(
                new JLabel("From:")
        );

        controls.add(
                salesStartField
        );

        controls.add(
                new JLabel("To:")
        );

        controls.add(
                salesEndField
        );

        JButton runButton =
                UIStyle.primaryButton(
                        "Run Report"
                );

        runButton.addActionListener(
                e -> runSalesReport()
        );

        JButton printButton =
                UIStyle.secondaryButton(
                        "Print Report"
                );

        printButton.addActionListener(
                e -> showPrintPreview(
                        salesTable,
                        "Sales Report",
                        salesSummaryLabel.getText()
                )
        );

        JButton pdfButton =
                UIStyle.secondaryButton(
                        "Export PDF"
                );

        pdfButton.addActionListener(
                e -> exportSalesPDF()
        );

        JButton todayButton =
                UIStyle.secondaryButton(
                        "Today"
                );

        todayButton.addActionListener(
                e -> {

                    salesStartField.setText(
                            today.format(FMT)
                    );

                    salesEndField.setText(
                            today.format(FMT)
                    );

                    runSalesReport();
                }
        );

        JButton last30Button =
                UIStyle.secondaryButton(
                        "Last 30 Days"
                );

        last30Button.addActionListener(
                e -> {

                    salesStartField.setText(
                            today.minusDays(30)
                                    .format(FMT)
                    );

                    salesEndField.setText(
                            today.format(FMT)
                    );

                    runSalesReport();
                }
        );

        controls.add(runButton);
        controls.add(printButton);
        controls.add(pdfButton);
        controls.add(todayButton);
        controls.add(last30Button);

        panel.add(
                controls,
                BorderLayout.NORTH
        );

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
                        )
                );

        content.setOpaque(false);

        JPanel summaryCard =
                UIStyle.card();

        summaryCard.setLayout(
                new BorderLayout()
        );

        salesSummaryLabel.setFont(
                UIStyle.FONT_HEADING
        );

        salesSummaryLabel.setForeground(
                UIStyle.PRIMARY_DARK
        );

        summaryCard.add(
                salesSummaryLabel,
                BorderLayout.CENTER
        );

        content.add(
                summaryCard,
                BorderLayout.NORTH
        );

        salesTable =
                new JTable(
                        salesTableModel
                );

        styleTable(
                salesTable
        );

        JScrollPane scroll =
                new JScrollPane(
                        salesTable
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        content.add(
                scroll,
                BorderLayout.CENTER
        );

        panel.add(
                content,
                BorderLayout.CENTER
        );

        runSalesReport();

        return panel;
    }

    private void runSalesReport() {

        LocalDate start;
        LocalDate end;

        try {

            start =
                    LocalDate.parse(
                            salesStartField
                                    .getText()
                                    .trim(),
                            FMT
                    );

            end =
                    LocalDate.parse(
                            salesEndField
                                    .getText()
                                    .trim(),
                            FMT
                    );

        } catch (DateTimeParseException e) {

            showWarning(
                    "Dates must be in YYYY-MM-DD format."
            );

            return;
        }

        if (end.isBefore(start)) {

            showWarning(
                    "End date must be on or after start date."
            );

            return;
        }

        try {

            List<Sale> sales =
                    salesDAO.getSalesBetween(
                            start,
                            end
                    );

            salesTableModel.setRowCount(0);

            DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd HH:mm"
                    );

            for (Sale sale : sales) {

                salesTableModel.addRow(
                        new Object[]{
                                sale.getSaleId(),
                                sale.getSaleDate()
                                        .format(
                                                dateTimeFormatter
                                        ),
                                sale.getCashierName() != null
                                        ? sale.getCashierName()
                                        : "-",
                                sale.getTotalAmount()
                        }
                );
            }

            BigDecimal total =
                    salesDAO.getTotalRevenueBetween(
                            start,
                            end
                    );

            salesSummaryLabel.setText(
                    "R "
                    + total.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
                    + " total revenue    •    "
                    + sales.size()
                    + " transactions"
            );

        } catch (RuntimeException ex) {

            showDatabaseError(
                    ex
            );
        }
    }

    // =========================================================
    // ITEM-WISE SALES
    // =========================================================

    private JPanel buildItemWiseReportTab() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        panel.setBackground(
                UIStyle.BG_LIGHT
        );

        panel.setBorder(
                new EmptyBorder(
                        16,
                        0,
                        0,
                        0
                )
        );

        LocalDate today =
                LocalDate.now();

        itemStartField.setText(
                today.minusDays(7)
                        .format(FMT)
        );

        itemEndField.setText(
                today.format(FMT)
        );

        styleDateField(
                itemStartField
        );

        styleDateField(
                itemEndField
        );

        JPanel controls =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        controls.setOpaque(false);

        controls.add(
                new JLabel("From:")
        );

        controls.add(
                itemStartField
        );

        controls.add(
                new JLabel("To:")
        );

        controls.add(
                itemEndField
        );

        JButton runButton =
                UIStyle.primaryButton(
                        "Run Report"
                );

        runButton.addActionListener(
                e -> runItemWiseReport()
        );

        JButton printButton =
                UIStyle.secondaryButton(
                        "Print Report"
                );

        printButton.addActionListener(
                e -> showPrintPreview(
                        itemWiseTable,
                        "Item-Wise Sales Report",
                        itemWiseSummaryLabel.getText()
                )
        );

        JButton pdfButton =
                UIStyle.secondaryButton(
                        "Export PDF"
                );

        pdfButton.addActionListener(
                e -> exportItemWisePDF()
        );

        JButton last30Button =
                UIStyle.secondaryButton(
                        "Last 30 Days"
                );

        last30Button.addActionListener(
                e -> {

                    itemStartField.setText(
                            today.minusDays(30)
                                    .format(FMT)
                    );

                    itemEndField.setText(
                            today.format(FMT)
                    );

                    runItemWiseReport();
                }
        );

        controls.add(runButton);
        controls.add(printButton);
        controls.add(pdfButton);
        controls.add(last30Button);

        panel.add(
                controls,
                BorderLayout.NORTH
        );

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
                        )
                );

        content.setOpaque(false);

        JPanel summaryCard =
                UIStyle.card();

        summaryCard.setLayout(
                new BorderLayout()
        );

        itemWiseSummaryLabel.setFont(
                UIStyle.FONT_HEADING
        );

        itemWiseSummaryLabel.setForeground(
                UIStyle.PRIMARY_DARK
        );

        summaryCard.add(
                itemWiseSummaryLabel,
                BorderLayout.CENTER
        );

        content.add(
                summaryCard,
                BorderLayout.NORTH
        );

        itemWiseTable =
                new JTable(
                        itemWiseTableModel
                );

        styleTable(
                itemWiseTable
        );

        JScrollPane scroll =
                new JScrollPane(
                        itemWiseTable
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        content.add(
                scroll,
                BorderLayout.CENTER
        );

        panel.add(
                content,
                BorderLayout.CENTER
        );

        runItemWiseReport();

        return panel;
    }

    private void runItemWiseReport() {

        LocalDate start;
        LocalDate end;

        try {

            start =
                    LocalDate.parse(
                            itemStartField
                                    .getText()
                                    .trim(),
                            FMT
                    );

            end =
                    LocalDate.parse(
                            itemEndField
                                    .getText()
                                    .trim(),
                            FMT
                    );

        } catch (DateTimeParseException e) {

            showWarning(
                    "Dates must be in YYYY-MM-DD format."
            );

            return;
        }

        if (end.isBefore(start)) {

            showWarning(
                    "End date must be on or after start date."
            );

            return;
        }

        try {

            List<Object[]> items =
                    salesDAO.getItemWiseSales(
                            start,
                            end
                    );

            itemWiseTableModel.setRowCount(0);

            int totalQuantity = 0;

            BigDecimal totalRevenue =
                    BigDecimal.ZERO;

            for (Object[] item : items) {

                int quantity =
                        ((Number) item[1])
                                .intValue();

                BigDecimal revenue;

                if (item[2] instanceof BigDecimal) {

                    revenue =
                            (BigDecimal) item[2];

                } else {

                    revenue =
                            new BigDecimal(
                                    String.valueOf(
                                            item[2]
                                    )
                            );
                }

                totalQuantity +=
                        quantity;

                totalRevenue =
                        totalRevenue.add(
                                revenue
                        );

                itemWiseTableModel.addRow(
                        new Object[]{
                                item[0],
                                quantity,
                                revenue
                        }
                );
            }

            itemWiseSummaryLabel.setText(
                    items.size()
                    + " medicines sold    •    "
                    + totalQuantity
                    + " units    •    R "
                    + totalRevenue.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
                    + " revenue"
            );

        } catch (RuntimeException ex) {

            showDatabaseError(
                    ex
            );
        }
    }

    // =========================================================
    // LOW STOCK REPORT
    // =========================================================

    private JPanel buildLowStockReportTab() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        panel.setBackground(
                UIStyle.BG_LIGHT
        );

        panel.setBorder(
                new EmptyBorder(
                        16,
                        0,
                        0,
                        0
                )
        );

        JPanel controls =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        controls.setOpaque(false);

        controls.add(
                UIStyle.muted(
                        "Medicines at or below their reorder level"
                )
        );

        JButton refreshButton =
                UIStyle.primaryButton(
                        "Refresh Report"
                );

        refreshButton.addActionListener(
                e -> runLowStockReport()
        );

        JButton printButton =
                UIStyle.secondaryButton(
                        "Print Report"
                );

        printButton.addActionListener(
                e -> showPrintPreview(
                        lowStockTable,
                        "Low Stock Report",
                        lowStockSummaryLabel.getText()
                )
        );

        JButton pdfButton =
                UIStyle.secondaryButton(
                        "Export PDF"
                );

        pdfButton.addActionListener(
                e -> exportLowStockPDF()
        );

        controls.add(
                refreshButton
        );

        controls.add(
                printButton
        );

        controls.add(
                pdfButton
        );

        panel.add(
                controls,
                BorderLayout.NORTH
        );

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
                        )
                );

        content.setOpaque(false);

        JPanel summaryCard =
                UIStyle.card();

        summaryCard.setLayout(
                new BorderLayout()
        );

        lowStockSummaryLabel.setFont(
                UIStyle.FONT_HEADING
        );

        lowStockSummaryLabel.setForeground(
                UIStyle.WARNING
        );

        summaryCard.add(
                lowStockSummaryLabel,
                BorderLayout.CENTER
        );

        content.add(
                summaryCard,
                BorderLayout.NORTH
        );

        lowStockTable =
                new JTable(
                        lowStockTableModel
                );

        styleTable(
                lowStockTable
        );

        JScrollPane scroll =
                new JScrollPane(
                        lowStockTable
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        content.add(
                scroll,
                BorderLayout.CENTER
        );

        panel.add(
                content,
                BorderLayout.CENTER
        );

        runLowStockReport();

        return panel;
    }

    private void runLowStockReport() {

        try {

            List<Medicine> medicines =
                    medicineDAO.getLowStock();

            lowStockTableModel.setRowCount(0);

            for (Medicine medicine : medicines) {

                lowStockTableModel.addRow(
                        new Object[]{
                                medicine.getName(),
                                medicine.getCompany(),
                                medicine.getQuantityInStock(),
                                medicine.getReorderLevel(),
                                medicine.getSupplierName() != null
                                        ? medicine.getSupplierName()
                                        : "-"
                        }
                );
            }

            lowStockSummaryLabel.setText(
                    medicines.size()
                    + " medicine"
                    + (
                            medicines.size() == 1
                                    ? ""
                                    : "s"
                    )
                    + " currently require attention"
            );

        } catch (RuntimeException ex) {

            showDatabaseError(
                    ex
            );
        }
    }

    // =========================================================
    // EXPIRY REPORT
    // =========================================================

    private JPanel buildExpiryReportTab() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        panel.setBackground(
                UIStyle.BG_LIGHT
        );

        panel.setBorder(
                new EmptyBorder(
                        16,
                        0,
                        0,
                        0
                )
        );

        expiryWindowCombo.setSelectedItem(
                "30 days"
        );

        JPanel controls =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        controls.setOpaque(false);

        controls.add(
                new JLabel(
                        "Show medicines expiring within:"
                )
        );

        controls.add(
                expiryWindowCombo
        );

        JButton runButton =
                UIStyle.primaryButton(
                        "Run Report"
                );

        runButton.addActionListener(
                e -> runExpiryReport()
        );

        JButton printButton =
                UIStyle.secondaryButton(
                        "Print Report"
                );

        printButton.addActionListener(
                e -> showPrintPreview(
                        expiryTable,
                        "Expiry Report",
                        "Medicines expiring within "
                        + expiryWindowCombo
                                .getSelectedItem()
                )
        );

        JButton pdfButton =
                UIStyle.secondaryButton(
                        "Export PDF"
                );

        pdfButton.addActionListener(
                e -> exportExpiryPDF()
        );

        controls.add(runButton);
        controls.add(printButton);
        controls.add(pdfButton);

        panel.add(
                controls,
                BorderLayout.NORTH
        );

        expiryTable =
                new JTable(
                        expiryTableModel
                );

        styleTable(
                expiryTable
        );

        expiryTable
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new DaysLeftRenderer()
                );

        JScrollPane scroll =
                new JScrollPane(
                        expiryTable
                );

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

        runExpiryReport();

        return panel;
    }

    private void runExpiryReport() {

        String selected =
                String.valueOf(
                        expiryWindowCombo
                                .getSelectedItem()
                );

        int days =
                Integer.parseInt(
                        selected.split(" ")[0]
                );

        try {

            List<Medicine> medicines =
                    medicineDAO.getExpiringWithinDays(
                            days
                    );

            expiryTableModel.setRowCount(0);

            for (Medicine medicine : medicines) {

                if (medicine.getExpiryDate() == null) {
                    continue;
                }

                long daysLeft =
                        java.time.temporal.ChronoUnit.DAYS
                                .between(
                                        LocalDate.now(),
                                        medicine.getExpiryDate()
                                );

                expiryTableModel.addRow(
                        new Object[]{
                                medicine.getName(),
                                medicine.getCompany(),
                                medicine.getQuantityInStock(),
                                medicine.getExpiryDate(),
                                daysLeft
                        }
                );
            }

        } catch (RuntimeException ex) {

            showDatabaseError(
                    ex
            );
        }
    }

    // =========================================================
    // PRINT PREVIEW
    // =========================================================

    private void showPrintPreview(
            JTable table,
            String reportTitle,
            String summary
    ) {

        if (table == null ||
                table.getRowCount() == 0) {

            showWarning(
                    "There is no report data to print."
            );

            return;
        }

        JDialog previewDialog =
                new JDialog(
                        SwingUtilities
                                .getWindowAncestor(this),
                        "Print Preview - "
                        + reportTitle,
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        previewDialog.setSize(
                900,
                650
        );

        previewDialog.setLocationRelativeTo(
                this
        );

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        mainPanel.setBackground(
                UIStyle.BG_LIGHT
        );

        mainPanel.setBorder(
                new EmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        // -----------------------------------------------------
        // PREVIEW HEADER
        // -----------------------------------------------------

        JPanel header =
                new JPanel();

        header.setOpaque(false);

        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel pharmacyLabel =
                new JLabel(
                        "HealthFirst Pharmacy"
                );

        pharmacyLabel.setFont(
                UIStyle.FONT_TITLE
        );

        pharmacyLabel.setForeground(
                UIStyle.PRIMARY_DARK
        );

        JLabel titleLabel =
                new JLabel(
                        reportTitle
                );

        titleLabel.setFont(
                UIStyle.FONT_HEADING
        );

        titleLabel.setForeground(
                UIStyle.TEXT_DARK
        );

        JLabel dateLabel =
                new JLabel(
                        "Generated: "
                        + LocalDate.now()
                                .format(FMT)
                );

        dateLabel.setFont(
                UIStyle.FONT_SMALL
        );

        dateLabel.setForeground(
                UIStyle.TEXT_MUTED
        );

        JLabel summaryLabel =
                new JLabel(
                        summary
                );

        summaryLabel.setFont(
                UIStyle.FONT_BODY
        );

        summaryLabel.setForeground(
                UIStyle.TEXT_DARK
        );

        header.add(
                pharmacyLabel
        );

        header.add(
                Box.createVerticalStrut(5)
        );

        header.add(
                titleLabel
        );

        header.add(
                Box.createVerticalStrut(4)
        );

        header.add(
                dateLabel
        );

        header.add(
                Box.createVerticalStrut(5)
        );

        header.add(
                summaryLabel
        );

        mainPanel.add(
                header,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // PREVIEW CONTENT
        // -----------------------------------------------------

        JTextArea previewArea =
                new JTextArea();

        previewArea.setEditable(
                false
        );

        /*
         * Fully qualify java.awt.Font here because
         * OpenPDF also has a class called Font.
         */
        previewArea.setFont(
                new java.awt.Font(
                        java.awt.Font.MONOSPACED,
                        java.awt.Font.PLAIN,
                        12
                )
        );

        previewArea.setBackground(
                Color.WHITE
        );

        previewArea.setForeground(
                Color.BLACK
        );

        previewArea.setMargin(
                new Insets(
                        12,
                        12,
                        12,
                        12
                )
        );

        previewArea.setText(
                createPreviewText(
                        table
                )
        );

        previewArea.setCaretPosition(
                0
        );

        JScrollPane previewScroll =
                new JScrollPane(
                        previewArea
                );

        previewScroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER
                )
        );

        mainPanel.add(
                previewScroll,
                BorderLayout.CENTER
        );

        // -----------------------------------------------------
        // PREVIEW BUTTONS
        // -----------------------------------------------------

        JPanel buttons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        buttons.setOpaque(false);

        JButton printButton =
                UIStyle.primaryButton(
                        "Print"
                );

        printButton.addActionListener(
                e -> {

                    try {

                        PrinterJob printerJob =
                                PrinterJob
                                        .getPrinterJob();

                        printerJob.setJobName(
                                "HealthFirst Pharmacy - "
                                + reportTitle
                        );

                        /*
                         * MessageFormat is required here.
                         * Do not pass reportTitle directly.
                         */
                        printerJob.setPrintable(
                                previewArea.getPrintable(
                                        new MessageFormat(
                                                reportTitle
                                        ),
                                        null
                                )
                        );

                        boolean proceed =
                                printerJob.printDialog();

                        if (!proceed) {
                            return;
                        }

                        printerJob.print();

                        JOptionPane.showMessageDialog(
                                previewDialog,
                                "Report printed successfully.",
                                "Print Complete",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } catch (PrinterException ex) {

                        JOptionPane.showMessageDialog(
                                previewDialog,
                                "Unable to print the report:\n\n"
                                + ex.getMessage(),
                                "Print Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
        );

        JButton closeButton =
                UIStyle.secondaryButton(
                        "Close"
                );

        closeButton.addActionListener(
                e -> previewDialog.dispose()
        );

        buttons.add(
                printButton
        );

        buttons.add(
                closeButton
        );

        mainPanel.add(
                buttons,
                BorderLayout.SOUTH
        );

        previewDialog.setContentPane(
                mainPanel
        );

        previewDialog.setVisible(
                true
        );
    }

    // =========================================================
    // PREVIEW TEXT
    // =========================================================

    private String createPreviewText(
            JTable table
    ) {

        StringBuilder text =
                new StringBuilder();

        int columnCount =
                table.getColumnCount();

        int[] widths =
                new int[columnCount];

        for (
                int column = 0;
                column < columnCount;
                column++
        ) {

            widths[column] =
                    table.getColumnName(
                            column
                    ).length();
        }

        for (
                int row = 0;
                row < table.getRowCount();
                row++
        ) {

            for (
                    int column = 0;
                    column < columnCount;
                    column++
            ) {

                Object value =
                        table.getValueAt(
                                row,
                                column
                        );

                String valueText =
                        value == null
                                ? ""
                                : value.toString();

                widths[column] =
                        Math.max(
                                widths[column],
                                valueText.length()
                        );
            }
        }

        // Header
        for (
                int column = 0;
                column < columnCount;
                column++
        ) {

            text.append(
                    padRight(
                            table.getColumnName(
                                    column
                            ),
                            widths[column]
                    )
            );

            if (
                    column <
                    columnCount - 1
            ) {

                text.append(
                        "  |  "
                );
            }
        }

        text.append("\n");

        // Divider
        for (
                int column = 0;
                column < columnCount;
                column++
        ) {

            text.append(
                    "-".repeat(
                            widths[column]
                    )
            );

            if (
                    column <
                    columnCount - 1
            ) {

                text.append(
                        "--+--"
                );
            }
        }

        text.append("\n");

        // Data
        for (
                int row = 0;
                row < table.getRowCount();
                row++
        ) {

            for (
                    int column = 0;
                    column < columnCount;
                    column++
            ) {

                Object value =
                        table.getValueAt(
                                row,
                                column
                        );

                String valueText =
                        value == null
                                ? ""
                                : value.toString();

                text.append(
                        padRight(
                                valueText,
                                widths[column]
                        )
                );

                if (
                        column <
                        columnCount - 1
                ) {

                    text.append(
                            "  |  "
                    );
                }
            }

            text.append("\n");
        }

        return text.toString();
    }

    private String padRight(
            String text,
            int width
    ) {

        if (text.length() >= width) {
            return text;
        }

        return text +
                " ".repeat(
                        width - text.length()
                );
    }

    // =========================================================
    // SALES PDF
    // =========================================================

    private void exportSalesPDF() {

        JFileChooser chooser =
                createPDFChooser(
                        "Sales_Report.pdf"
                );

        if (
                chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File outputFile =
                ensurePDFExtension(
                        chooser.getSelectedFile()
                );

        try {

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(
                            outputFile
                    )
            );

            document.open();

            addPDFHeader(
                    document,
                    "Sales Report",
                    salesSummaryLabel.getText()
            );

            addTableToPDF(
                    document,
                    salesTableModel,
                    new String[]{
                            "Sale ID",
                            "Date / Time",
                            "Cashier",
                            "Total (R)"
                    }
            );

            document.close();

            showExportSuccess(
                    outputFile
            );

        } catch (Exception ex) {

            showPDFError(
                    ex
            );
        }
    }

    // =========================================================
    // ITEM-WISE PDF
    // =========================================================

    private void exportItemWisePDF() {

        JFileChooser chooser =
                createPDFChooser(
                        "Item_Wise_Sales_Report.pdf"
                );

        if (
                chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File outputFile =
                ensurePDFExtension(
                        chooser.getSelectedFile()
                );

        try {

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(
                            outputFile
                    )
            );

            document.open();

            addPDFHeader(
                    document,
                    "Item-Wise Sales Report",
                    itemWiseSummaryLabel.getText()
            );

            addTableToPDF(
                    document,
                    itemWiseTableModel,
                    new String[]{
                            "Medicine",
                            "Quantity Sold",
                            "Revenue (R)"
                    }
            );

            document.close();

            showExportSuccess(
                    outputFile
            );

        } catch (Exception ex) {

            showPDFError(
                    ex
            );
        }
    }

    // =========================================================
    // LOW STOCK PDF
    // =========================================================

    private void exportLowStockPDF() {

        JFileChooser chooser =
                createPDFChooser(
                        "Low_Stock_Report.pdf"
                );

        if (
                chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File outputFile =
                ensurePDFExtension(
                        chooser.getSelectedFile()
                );

        try {

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(
                            outputFile
                    )
            );

            document.open();

            addPDFHeader(
                    document,
                    "Low Stock Report",
                    lowStockSummaryLabel.getText()
            );

            addTableToPDF(
                    document,
                    lowStockTableModel,
                    new String[]{
                            "Medicine",
                            "Company",
                            "Stock",
                            "Reorder Level",
                            "Supplier"
                    }
            );

            document.close();

            showExportSuccess(
                    outputFile
            );

        } catch (Exception ex) {

            showPDFError(
                    ex
            );
        }
    }

    // =========================================================
    // EXPIRY PDF
    // =========================================================

    private void exportExpiryPDF() {

        JFileChooser chooser =
                createPDFChooser(
                        "Expiry_Report.pdf"
                );

        if (
                chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File outputFile =
                ensurePDFExtension(
                        chooser.getSelectedFile()
                );

        try {

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(
                            outputFile
                    )
            );

            document.open();

            String window =
                    String.valueOf(
                            expiryWindowCombo
                                    .getSelectedItem()
                    );

            addPDFHeader(
                    document,
                    "Expiry Report",
                    "Medicines expiring within "
                    + window
            );

            addTableToPDF(
                    document,
                    expiryTableModel,
                    new String[]{
                            "Medicine",
                            "Company",
                            "Stock",
                            "Expiry Date",
                            "Days Left"
                    }
            );

            document.close();

            showExportSuccess(
                    outputFile
            );

        } catch (Exception ex) {

            showPDFError(
                    ex
            );
        }
    }

    // =========================================================
    // PDF HELPERS
    // =========================================================

    private void addPDFHeader(
            Document document,
            String title,
            String summary
    ) throws DocumentException {

        Font titleFont =
                new Font(
                        Font.HELVETICA,
                        18,
                        Font.BOLD
                );

        Font normalFont =
                new Font(
                        Font.HELVETICA,
                        10,
                        Font.NORMAL
                );

        Paragraph pharmacy =
                new Paragraph(
                        "HealthFirst Pharmacy",
                        titleFont
                );

        pharmacy.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(
                pharmacy
        );

        Paragraph reportTitle =
                new Paragraph(
                        title,
                        new Font(
                                Font.HELVETICA,
                                14,
                                Font.BOLD
                        )
                );

        reportTitle.setSpacingBefore(
                10
        );

        reportTitle.setSpacingAfter(
                5
        );

        document.add(
                reportTitle
        );

        document.add(
                new Paragraph(
                        summary,
                        normalFont
                )
        );

        document.add(
                new Paragraph(
                        "Generated: "
                        + LocalDate.now()
                                .format(FMT),
                        normalFont
                )
        );

        document.add(
                new Paragraph(" ")
        );
    }

    private void addTableToPDF(
            Document document,
            DefaultTableModel model,
            String[] columns
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(
                        columns.length
                );

        table.setWidthPercentage(
                100
        );

        Font headerFont =
                new Font(
                        Font.HELVETICA,
                        9,
                        Font.BOLD
                );

        Font bodyFont =
                new Font(
                        Font.HELVETICA,
                        9,
                        Font.NORMAL
                );

        for (String column : columns) {

            table.addCell(
                    new Phrase(
                            column,
                            headerFont
                    )
            );
        }

        for (
                int row = 0;
                row < model.getRowCount();
                row++
        ) {

            for (
                    int column = 0;
                    column < model.getColumnCount();
                    column++
            ) {

                Object value =
                        model.getValueAt(
                                row,
                                column
                        );

                table.addCell(
                        new Phrase(
                                value == null
                                        ? ""
                                        : value.toString(),
                                bodyFont
                        )
                );
            }
        }

        document.add(
                table
        );
    }

    private JFileChooser createPDFChooser(
            String fileName
    ) {

        JFileChooser chooser =
                new JFileChooser();

        chooser.setDialogTitle(
                "Export Report as PDF"
        );

        chooser.setSelectedFile(
                new File(
                        fileName
                )
        );

        return chooser;
    }

    private File ensurePDFExtension(
            File file
    ) {

        String path =
                file.getAbsolutePath();

        if (
                !path
                        .toLowerCase()
                        .endsWith(".pdf")
        ) {

            return new File(
                    path + ".pdf"
            );
        }

        return file;
    }

    // =========================================================
    // TABLE STYLING
    // =========================================================

    private void styleTable(
            JTable table
    ) {

        table.setRowHeight(
                32
        );

        table.setFont(
                UIStyle.FONT_BODY
        );

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setAutoCreateRowSorter(
                true
        );

        UIStyle.styleTableHeader(
                table.getTableHeader()
        );
    }

    private void styleDateField(
            JTextField field
    ) {

        field.setFont(
                UIStyle.FONT_BODY
        );

        field.setPreferredSize(
                new Dimension(
                        120,
                        32
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
                                8,
                                4,
                                8
                        )
                )
        );
    }

    // =========================================================
    // DAYS LEFT RENDERER
    // =========================================================

    private static class DaysLeftRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component
        getTableCellRendererComponent(
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
                    UIStyle.FONT_SMALL
                            .deriveFont(
                                    java.awt.Font.BOLD
                            )
            );

            long days;

            try {

                days =
                        Long.parseLong(
                                String.valueOf(
                                        value
                                )
                        );

            } catch (NumberFormatException e) {

                days = 0;
            }

            label.setOpaque(
                    true
            );

            if (days <= 7) {

                label.setForeground(
                        Color.WHITE
                );

                label.setBackground(
                        UIStyle.DANGER
                );

            } else if (days <= 30) {

                label.setForeground(
                        UIStyle.DANGER
                );

                label.setBackground(
                        UIStyle.DANGER_BG
                );

            } else {

                label.setForeground(
                        new Color(
                                150,
                                100,
                                0
                        )
                );

                label.setBackground(
                        UIStyle.WARNING_BG
                );
            }

            label.setText(
                    days
                    + " day"
                    + (
                            days == 1
                                    ? ""
                                    : "s"
                    )
            );

            return label;
        }
    }

    // =========================================================
    // MESSAGES
    // =========================================================

    private void showWarning(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid Information",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showDatabaseError(
            RuntimeException ex
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Failed to generate report:\n"
                + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showPDFError(
            Exception ex
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Unable to export the report:\n"
                + ex.getMessage(),
                "PDF Export Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showExportSuccess(
            File file
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Report exported successfully.\n\n"
                + file.getAbsolutePath(),
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}