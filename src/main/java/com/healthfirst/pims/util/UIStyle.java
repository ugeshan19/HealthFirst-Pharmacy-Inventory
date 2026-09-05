package com.healthfirst.pims.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

//Ugeshan Naicker Student No.402313594.
public final class UIStyle {

    private UIStyle() {
    }

    // =========================================================
    // COLOURS
    // =========================================================

    // Main brand colour
    public static final Color PRIMARY =
            new Color(0xC9, 0x7A, 0x2B);

    public static final Color PRIMARY_DARK =
            new Color(0x9F, 0x5E, 0x1D);

    public static final Color PRIMARY_LIGHT =
            new Color(0xF6, 0xEA, 0xD8);

    // Secondary accent
    public static final Color ACCENT =
            new Color(0xE7, 0xA9, 0x77);

    public static final Color ACCENT_DARK =
            new Color(0xC9, 0x86, 0x5C);

    public static final Color ACCENT_LIGHT =
            new Color(0xFB, 0xEF, 0xE5);

    // Sidebar
    public static final Color SIDEBAR_BG =
            new Color(0x18, 0x2B, 0x3A);

    public static final Color SIDEBAR_HOVER =
            new Color(0x2B, 0x42, 0x52);

    public static final Color SIDEBAR_SELECTED =
            new Color(0xC9, 0x7A, 0x2B);

    public static final Color SIDEBAR_TEXT =
            new Color(0xF4, 0xF1, 0xEA);

    public static final Color SIDEBAR_MUTED =
            new Color(0xA9, 0xB5, 0xBC);

    // Main background
    public static final Color BG_LIGHT =
            new Color(0xF5, 0xF3, 0xEF);

    public static final Color CARD_BG =
            new Color(0xFF, 0xFF, 0xFD);

    public static final Color TABLE_HEADER =
            new Color(0xF6, 0xEA, 0xD8);

    // Borders
    public static final Color BORDER =
            new Color(0xDD, 0xD9, 0xD1);

    // Text
    public static final Color TEXT_DARK =
            new Color(0x27, 0x2C, 0x30);

    public static final Color TEXT_MUTED =
            new Color(0x70, 0x76, 0x79);

    public static final Color TEXT_LIGHT =
            new Color(0xF8, 0xF6, 0xF3);

    // Status colours
    public static final Color SUCCESS =
            new Color(0x35, 0x8A, 0x5B);

    public static final Color SUCCESS_BG =
            new Color(0xE7, 0xF3, 0xEA);

    public static final Color WARNING =
            new Color(0xC9, 0x7A, 0x2B);

    public static final Color WARNING_BG =
            new Color(0xF8, 0xED, 0xDB);

    public static final Color DANGER =
            new Color(0xC9, 0x4B, 0x4B);

    public static final Color DANGER_BG =
            new Color(0xF8, 0xE7, 0xE7);


    // =========================================================
    // FONTS
    // =========================================================

    private static final String FONT_NAME = "Segoe UI";

    public static final Font FONT_TITLE =
            new Font(FONT_NAME, Font.BOLD, 24);

    public static final Font FONT_HEADING =
            new Font(FONT_NAME, Font.BOLD, 18);

    public static final Font FONT_SUBHEAD =
            new Font(FONT_NAME, Font.BOLD, 14);

    public static final Font FONT_BODY =
            new Font(FONT_NAME, Font.PLAIN, 13);

    public static final Font FONT_SMALL =
            new Font(FONT_NAME, Font.PLAIN, 12);

    public static final Font FONT_BUTTON =
            new Font(FONT_NAME, Font.BOLD, 13);

    public static final Font FONT_NAV =
            new Font(FONT_NAME, Font.PLAIN, 14);

    public static final Font FONT_MONO_LG =
            new Font("Consolas", Font.BOLD, 20);


    // =========================================================
    // GLOBAL UI
    // =========================================================

    public static void applyGlobalUI() {

        UIManager.put("Panel.background", BG_LIGHT);
        UIManager.put("OptionPane.background", CARD_BG);

        UIManager.put("Label.foreground", TEXT_DARK);
        UIManager.put("Label.font", FONT_BODY);

        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("TextField.foreground", TEXT_DARK);
        UIManager.put("TextField.background", Color.WHITE);

        UIManager.put("PasswordField.font", FONT_BODY);
        UIManager.put("PasswordField.foreground", TEXT_DARK);
        UIManager.put("PasswordField.background", Color.WHITE);

        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("ComboBox.foreground", TEXT_DARK);
        UIManager.put("ComboBox.background", Color.WHITE);

        UIManager.put("Button.font", FONT_BUTTON);

        UIManager.put("CheckBox.font", FONT_BODY);
        UIManager.put("CheckBox.foreground", TEXT_DARK);

        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("Table.foreground", TEXT_DARK);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.gridColor", BORDER);

        UIManager.put("TableHeader.font", FONT_SUBHEAD);
        UIManager.put("TableHeader.foreground", TEXT_DARK);
        UIManager.put("TableHeader.background", TABLE_HEADER);

        UIManager.put("ScrollPane.background", BG_LIGHT);

        UIManager.put("TabbedPane.font", FONT_SUBHEAD);
        UIManager.put("TabbedPane.selected", CARD_BG);

        UIManager.put("ProgressBar.foreground", PRIMARY);
    }


    // =========================================================
    // LABEL HELPERS
    // =========================================================

    public static JLabel heading(String text) {

        JLabel label = new JLabel(text);

        label.setFont(FONT_HEADING);
        label.setForeground(TEXT_DARK);

        return label;
    }

    public static JLabel subheading(String text) {

        JLabel label = new JLabel(text);

        label.setFont(FONT_SUBHEAD);
        label.setForeground(TEXT_DARK);

        return label;
    }

    public static JLabel muted(String text) {

        JLabel label = new JLabel(text);

        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_MUTED);

        return label;
    }


    // =========================================================
    // BADGE
    // =========================================================

    public static JLabel badge(
            String text,
            Color foreground,
            Color background
    ) {

        JLabel label = new JLabel(text);

        label.setFont(FONT_SMALL);
        label.setForeground(foreground);
        label.setBackground(background);
        label.setOpaque(true);

        label.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                foreground,
                                1
                        ),
                        new EmptyBorder(
                                4,
                                8,
                                4,
                                8
                        )
                )
        );

        return label;
    }


    // =========================================================
    // CARDS
    // =========================================================

    public static JPanel card() {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(CARD_BG);

        Border border =
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER,
                                1
                        ),
                        new EmptyBorder(
                                16,
                                16,
                                16,
                                16
                        )
                );

        panel.setBorder(border);

        return panel;
    }


    // =========================================================
    // BUTTONS
    // =========================================================

    public static JButton primaryButton(String text) {

        JButton button = new JButton(text);

        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setBorder(
                new EmptyBorder(
                        9,
                        16,
                        9,
                        16
                )
        );

        return button;
    }


    public static JButton secondaryButton(String text) {

        JButton button = new JButton(text);

        button.setFont(FONT_BUTTON);
        button.setForeground(TEXT_DARK);
        button.setBackground(Color.WHITE);

        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER,
                                1
                        ),
                        new EmptyBorder(
                                8,
                                15,
                                8,
                                15
                        )
                )
        );

        return button;
    }


    public static JButton dangerButton(String text) {

        JButton button = new JButton(text);

        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setBorder(
                new EmptyBorder(
                        9,
                        16,
                        9,
                        16
                )
        );

        return button;
    }


    // =========================================================
    // TABLE HEADER
    // =========================================================

    public static void styleTableHeader(
            JTableHeader header
    ) {

        header.setFont(FONT_SUBHEAD);
        header.setForeground(TEXT_DARK);
        header.setBackground(TABLE_HEADER);

        header.setOpaque(true);

        header.setBorder(
                BorderFactory.createLineBorder(
                        BORDER,
                        1
                )
        );

        header.setPreferredSize(
                new Dimension(
                        header.getPreferredSize().width,
                        36
                )
        );
    }


    // =========================================================
    // FONT HELPERS
    // =========================================================

    public static Font bold(Font font) {

        return font.deriveFont(
                Font.BOLD
        );
    }

    public static Font regular(Font font) {

        return font.deriveFont(
                Font.PLAIN
        );
    }
}