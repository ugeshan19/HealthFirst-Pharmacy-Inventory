package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.UserDAO;
import com.healthfirst.pims.model.User;
import com.healthfirst.pims.util.SessionManager;
import com.healthfirst.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

//Ugeshan Naicker Student No.402313594
public class ManageUsersPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<User> currentList;

    public ManageUsersPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIStyle.BG_LIGHT);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        add(buildHeader(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Username", "Full Name", "Role", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(UIStyle.FONT_BODY);
        table.setSelectionBackground(UIStyle.PRIMARY_LIGHT);
        table.setSelectionForeground(UIStyle.TEXT_DARK);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        UIStyle.styleTableHeader(table.getTableHeader());
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER, 1, true));
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(UIStyle.heading("Manage Users"));
        titleBlock.add(UIStyle.muted("Create and manage Administrator and Cashier accounts"));
        header.add(titleBlock, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton addBtn = UIStyle.primaryButton("+ Add User");
        addBtn.addActionListener(e -> openAddDialog());
        JButton editBtn = UIStyle.secondaryButton("Edit Selected");
        editBtn.addActionListener(e -> openEditDialog());
        JButton deleteBtn = UIStyle.dangerButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelected());

        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(addBtn);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    public void loadData() {
        try {
            currentList = userDAO.getAllUsers();
            tableModel.setRowCount(0);
            for (User u : currentList) {
                tableModel.addRow(new Object[]{
                        u.getUserId(), u.getUsername(), u.getFullName(), u.getRole(),
                        u.isActive() ? "Active" : "Disabled"
                });
            }
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private User getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int id = (int) tableModel.getValueAt(row, 0);
        return currentList.stream().filter(u -> u.getUserId() == id).findFirst().orElse(null);
    }

    private void openAddDialog() {
        UserDialog dialog = new UserDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadData();
    }

    private void openEditDialog() {
        User selected = getSelected();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UserDialog dialog = new UserDialog((Frame) SwingUtilities.getWindowAncestor(this), selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadData();
    }

    private void deleteSelected() {
        User selected = getSelected();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User current = SessionManager.getCurrentUser();
        if (current != null && current.getUserId() == selected.getUserId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete the account you are currently logged in as.", "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user \"" + selected.getUsername() + "\"? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.deleteUser(selected.getUserId());
                loadData();
            } catch (RuntimeException ex) {
                showError(ex);
            }
        }
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(UIStyle.FONT_SMALL.deriveFont(Font.BOLD));
            boolean active = "Active".equals(value);
            label.setOpaque(true);
            label.setForeground(active ? UIStyle.SUCCESS : UIStyle.DANGER);
            label.setBackground(active ? UIStyle.SUCCESS_BG : UIStyle.DANGER_BG);
            return label;
        }
    }
}
