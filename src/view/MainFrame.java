package view;

import model.Expense;
import model.ExpenseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.UUID;

public class MainFrame extends JFrame {
    private ExpenseManager manager;
    private DashboardPanel dashboardPanel;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtDate, txtAmount, txtQuantity, txtDescription;
    private JComboBox<String> cbCategory;

    public MainFrame() {
        manager = new ExpenseManager();
        
        setTitle("Simple Expense Tracker");
        setSize(1000, 600); // Widened slightly to fit the new table columns comfortably
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Expenses", createDataEntryPanel());
        
        dashboardPanel = new DashboardPanel(manager);
        tabbedPane.addTab("Dashboard & Charts", dashboardPanel);

        add(tabbedPane);
        refreshData();
        setVisible(true);

        tabbedPane.addChangeListener(e -> refreshData());
    }

    private JPanel createDataEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Expanded grid to 2 rows and 6 columns
        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtDate = new JTextField(LocalDate.now().toString());
        cbCategory = new JComboBox<>(new String[]{"Food", "Transport", "Utilities", "Entertainment", "Other"});
        txtAmount = new JTextField();
        txtQuantity = new JTextField("1"); // Default to 1 for quick data entry
        txtDescription = new JTextField();

        // Row 1: Labels
        inputPanel.add(new JLabel("Tanggal (YYYY/MM/DD):"));
        inputPanel.add(new JLabel("Kategori:"));
        inputPanel.add(new JLabel("Harga Satuan (Rp):"));
        inputPanel.add(new JLabel("Jumlahnya:"));
        inputPanel.add(new JLabel("Keterangan:"));
        inputPanel.add(new JLabel("")); // Blank spacer label above buttons

        // Row 2: Inputs & Actions
        inputPanel.add(txtDate);
        inputPanel.add(cbCategory);
        inputPanel.add(txtAmount);
        inputPanel.add(txtQuantity);
        inputPanel.add(txtDescription);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton btnAdd = new JButton("Tambah");
        JButton btnDelete = new JButton("Hapus");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        inputPanel.add(buttonPanel);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Table layout with clear pricing breakdowns
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Date", "Category", "Unit Price", "Qty", "Subtotal", "Description"}, 0
        );
        table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addExpense());
        btnDelete.addActionListener(e -> deleteExpense());

        return panel;
    }

    private void addExpense() {
        try {
            Expense exp = new Expense(
                    UUID.randomUUID().toString().substring(0, 8),
                    LocalDate.parse(txtDate.getText()),
                    cbCategory.getSelectedItem().toString(),
                    Double.parseDouble(txtAmount.getText()),
                    Integer.parseInt(txtQuantity.getText()),
                    txtDescription.getText()
            );
            manager.addExpense(exp);
            txtAmount.setText("");
            txtQuantity.setText("1");
            txtDescription.setText("");
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Check your inputs! Ensure price is a number and quantity is a whole number.");
        }
    }

    private void deleteExpense() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String id = tableModel.getValueAt(row, 0).toString();
            manager.deleteExpense(id);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
        }
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        for (Expense exp : manager.getExpenses()) {
            tableModel.addRow(new Object[]{
                exp.getId(), 
                exp.getDate(), 
                exp.getCategory(), 
                String.format("Rp %,.0f", exp.getUnitPrice()), 
                exp.getQuantity(), 
                String.format("Rp %,.0f", exp.getSubtotal()), 
                exp.getDescription()
            });
        }
        dashboardPanel.refreshDashboard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}