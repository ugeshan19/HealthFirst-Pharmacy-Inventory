package com.healthfirst.pims.ui.cashier;

import com.healthfirst.pims.model.Sale;
import com.healthfirst.pims.model.SaleItem;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

/**
 * Ugeshan Naicker Student No.402313594
 * Displays a receipt after a successful checkout.
 *
 * The cashier can:
 * - Preview the receipt
 * - Print the receipt
 * - Save the receipt as a text file
 * - Close the receipt
 */
public class BillDialog extends JDialog {

    public BillDialog(Frame owner, Sale sale, String cashierName) {
        super(owner, "Sale Receipt #" + sale.getSaleId(), true);

        setSize(420, 560);
        setLocationRelativeTo(owner);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = UIStyle.heading("Sale Completed");
        title.setForeground(UIStyle.SUCCESS);
        content.add(title, BorderLayout.NORTH);

        JTextArea receiptArea =
                new JTextArea(buildReceiptText(sale, cashierName));

        receiptArea.setEditable(false);
        receiptArea.setFont(
                new java.awt.Font(
                        java.awt.Font.MONOSPACED,
                        java.awt.Font.PLAIN,
                        13
                )
        );

        receiptArea.setBorder(
                new EmptyBorder(12, 12, 12, 12)
        );

        JScrollPane scroll = new JScrollPane(receiptArea);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1,
                        true
                )
        );

        content.add(scroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(
                new GridLayout(1, 3, 8, 0)
        );

        btnRow.setOpaque(false);
        btnRow.setBorder(
                new EmptyBorder(16, 0, 0, 0)
        );

        /*
         * Print button
         *
         * Opens a print preview first.
         */
        JButton printBtn = UIStyle.secondaryButton("Print");

        printBtn.addActionListener(e ->
                showPrintPreview(
                        receiptArea.getText(),
                        "Receipt #" + sale.getSaleId()
                )
        );

        /*
         * Save receipt as a text file.
         */
        JButton saveBtn = UIStyle.secondaryButton("Save As...");

        saveBtn.addActionListener(e ->
                saveToFile(
                        receiptArea.getText(),
                        sale.getSaleId()
                )
        );

        /*
         * Close the receipt window.
         */
        JButton doneBtn = UIStyle.primaryButton("Done");

        doneBtn.addActionListener(e ->
                dispose()
        );

        btnRow.add(printBtn);
        btnRow.add(saveBtn);
        btnRow.add(doneBtn);

        content.add(btnRow, BorderLayout.SOUTH);

        setContentPane(content);
    }

    /**
     * Creates the receipt text displayed to the cashier.
     */
    private String buildReceiptText(
            Sale sale,
            String cashierName
    ) {

        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("           HEALTHFIRST PHARMACY          \n");
        sb.append("========================================\n");

        sb.append("Receipt #: ")
                .append(sale.getSaleId())
                .append("\n");

        sb.append("Date:      ")
                .append(
                        sale.getSaleDate().format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm"
                                )
                        )
                )
                .append("\n");

        sb.append("Cashier:   ")
                .append(cashierName)
                .append("\n");

        sb.append("----------------------------------------\n");

        sb.append(
                String.format(
                        "%-20s %3s %10s%n",
                        "ITEM",
                        "QTY",
                        "TOTAL"
                )
        );

        sb.append("----------------------------------------\n");

        for (SaleItem item : sale.getItems()) {

            String name = item.getMedicineName();

            if (name == null) {
                name = "";
            }

            if (name.length() > 20) {
                name = name.substring(0, 20);
            }

            sb.append(
                    String.format(
                            "%-20s %3d %10s%n",
                            name,
                            item.getQuantitySold(),
                            "R" + item.getLineTotal()
                                    .setScale(
                                            2,
                                            RoundingMode.HALF_UP
                                    )
                    )
            );
        }

        sb.append("----------------------------------------\n");

        sb.append(
                String.format(
                        "%-24s %13s%n",
                        "TOTAL:",
                        "R" + sale.getTotalAmount()
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                )
                )
        );

        sb.append("========================================\n");
        sb.append("       Thank you for your purchase!       \n");
        sb.append("========================================\n");

        return sb.toString();
    }

    /**
     * Opens a simple print preview window.
     *
     * The cashier sees the receipt before printing.
     */
    private void showPrintPreview(
            String receiptText,
            String reportTitle
    ) {

        JDialog previewDialog = new JDialog(
                this,
                "Print Preview - " + reportTitle,
                true
        );

        previewDialog.setSize(650, 700);
        previewDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(
                new BorderLayout(0, 12)
        );

        panel.setBackground(UIStyle.BG_LIGHT);
        panel.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        JLabel heading = UIStyle.heading("Print Preview");

        panel.add(heading, BorderLayout.NORTH);

        JTextArea previewArea =
                new JTextArea(receiptText);

        previewArea.setEditable(false);
        previewArea.setFont(
                new java.awt.Font(
                        java.awt.Font.MONOSPACED,
                        java.awt.Font.PLAIN,
                        13
                )
        );

        previewArea.setBackground(Color.WHITE);
        previewArea.setForeground(Color.BLACK);

        previewArea.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        JScrollPane scrollPane =
                new JScrollPane(previewArea);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIStyle.BORDER,
                        1
                )
        );

        panel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        buttonPanel.setOpaque(false);

        JButton cancelButton =
                UIStyle.secondaryButton("Cancel");

        cancelButton.addActionListener(e ->
                previewDialog.dispose()
        );

        JButton printButton =
                UIStyle.primaryButton("Print");

        printButton.addActionListener(e -> {

            try {

                PrinterJob printerJob =
                        PrinterJob.getPrinterJob();

                printerJob.setJobName(
                        "HealthFirst Pharmacy - "
                                + reportTitle
                );

                /*
                 * Convert the receipt text area into
                 * something that Java's printer system
                 * can print.
                 */
                printerJob.setPrintable(
                        previewArea.getPrintable(
                                new MessageFormat(reportTitle),
                                null
                        )
                );

                /*
                 * Show the normal printer selection
                 * dialog.
                 */
                boolean proceed =
                        printerJob.printDialog();

                if (!proceed) {
                    return;
                }

                /*
                 * Send the receipt to the selected printer.
                 */
                printerJob.print();

                JOptionPane.showMessageDialog(
                        previewDialog,
                        "Receipt printed successfully.",
                        "Print Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );

                previewDialog.dispose();

            } catch (PrinterException ex) {

                JOptionPane.showMessageDialog(
                        previewDialog,
                        "Unable to print the receipt:\n\n"
                                + ex.getMessage(),
                        "Print Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(printButton);

        panel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        previewDialog.setContentPane(panel);

        previewDialog.setVisible(true);
    }

    /**
     * Saves the receipt as a text file.
     */
    private void saveToFile(
            String text,
            int saleId
    ) {

        JFileChooser chooser =
                new JFileChooser();

        chooser.setSelectedFile(
                new java.io.File(
                        "receipt_" + saleId + ".txt"
                )
        );

        int result =
                chooser.showSaveDialog(this);

        if (result ==
                JFileChooser.APPROVE_OPTION) {

            try (
                    FileWriter writer =
                            new FileWriter(
                                    chooser.getSelectedFile()
                            )
            ) {

                writer.write(text);

                JOptionPane.showMessageDialog(
                        this,
                        "Receipt saved successfully.",
                        "Saved",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Failed to save receipt: "
                                + ex.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}