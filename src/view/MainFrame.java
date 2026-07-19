package view;

import model.Expense;
import model.ExpenseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

/**
 * Main application window for the expense tracker.
 * Provides forms for adding/removing expenses, a sortable table,
 * and a dashboard tab for summaries and charts.
 */
public class MainFrame extends JFrame {
    private ExpenseManager manager;
    private DashboardPanel dashboardPanel;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtDate, txtAmount, txtQuantity, txtDescription;
    private JComboBox<String> cbCategory;

    /**
     * Creates the main window, initializes the UI, and loads saved expenses.
     */
    public MainFrame() {
        manager = new ExpenseManager();

        setTitle("Simple Expense Tracker");
        setSize(1000, 600);
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

    /**
     * Builds the expense entry panel, including input fields, buttons,
     * sorting controls, and the expense table.
     *
     * @return the created panel
     */
    private JPanel createDataEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtDate = new JTextField(LocalDate.now().toString());
        cbCategory = new JComboBox<>(new String[]{"Food", "Transport", "Utilities", "Entertainment", "Other"});
        txtAmount = new JTextField();
        txtQuantity = new JTextField("1");
        txtDescription = new JTextField();

        inputPanel.add(new JLabel("Tanggal (YYYY-MM-DD):"));
        inputPanel.add(new JLabel("Kategori:"));
        inputPanel.add(new JLabel("Harga Satuan (Rp):"));
        inputPanel.add(new JLabel("Jumlahnya:"));
        inputPanel.add(new JLabel("Keterangan:"));
        inputPanel.add(new JLabel(""));

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

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Tanggal", "Kategori", "Harga Satuan", "Qty", "Subtotal", "Keterangan"}, 0
        ) {
            /**
             * Returns the Java type for each table column so sorting and rendering work correctly.
             *
             * @param columnIndex the column index
             * @return the column class
             */
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3 || columnIndex == 5) return Double.class;
                if (columnIndex == 4) return Integer.class;
                if (columnIndex == 1) return LocalDate.class;
                return String.class;
            }
        };
        table = new JTable(tableModel);

        DefaultTableCellRenderer currencyRenderer = new CurrencyRenderer();
        table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.DESCENDING)));

        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        sortPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblSort = new JLabel("Urutkan berdasarkan:");
        ButtonGroup sortGroup = new ButtonGroup();

        JToggleButton pillDate = new JToggleButton("Tanggal");
        JToggleButton pillAmount = new JToggleButton("Nominal");

        pillDate.setFocusPainted(false);
        pillAmount.setFocusPainted(false);
        pillDate.setMargin(new Insets(5, 15, 5, 15));
        pillAmount.setMargin(new Insets(5, 15, 5, 15));
        pillDate.setSelected(true);

        pillDate.addChangeListener(e -> stylePill(pillDate));
        pillAmount.addChangeListener(e -> stylePill(pillAmount));
        stylePill(pillDate);

        sortGroup.add(pillDate);
        sortGroup.add(pillAmount);

        pillDate.addActionListener(e ->
            sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.DESCENDING)))
        );
        pillAmount.addActionListener(e ->
            sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(5, SortOrder.DESCENDING)))
        );

        sortPanel.add(lblSort);
        sortPanel.add(pillDate);
        sortPanel.add(pillAmount);

        centerPanel.add(sortPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addExpense());
        btnDelete.addActionListener(e -> deleteExpense());

        return panel;
    }

    /**
     * Updates the visual style of a toggle button so it looks like an active pill.
     *
     * @param btn the toggle button to style
     */
    private void stylePill(JToggleButton btn) {
        if (btn.isSelected()) {
            btn.setBackground(new Color(0, 120, 215));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(UIManager.getColor("Button.background"));
            btn.setForeground(Color.BLACK);
        }
    }

    /**
     * Table cell renderer that formats numeric values as Indonesian Rupiah.
     */
    class CurrencyRenderer extends DefaultTableCellRenderer {
        /**
         * Formats table values for display.
         *
         * @param table the table
         * @param value the cell value
         * @param isSelected whether the cell is selected
         * @param hasFocus whether the cell has focus
         * @param row the row index
         * @param column the column index
         * @return the component used to render the cell
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Double) {
                setText(String.format("Rp %,.0f", (Double) value));
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
            return this;
        }
    }

    /**
     * Reads the form values, creates a new expense, and adds it to storage.
     */
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
            JOptionPane.showMessageDialog(this, "Cek input kamu! Harga harus angka, jumlah harus bilangan bulat.");
        }
    }

    /**
     * Deletes the selected expense from the table and data store.
     */
    private void deleteExpense() {
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        if (row >= 0) {
            String id = tableModel.getValueAt(row, 0).toString();
            manager.deleteExpense(id);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang mau dihapus dulu.");
        }
    }

    /**
     * Reloads all expense data into the table and refreshes the dashboard.
     */
    private void refreshData() {
        tableModel.setRowCount(0);
        for (Expense exp : manager.getExpenses()) {
            tableModel.addRow(new Object[]{
                exp.getId(),
                exp.getDate(),
                exp.getCategory(),
                exp.getUnitPrice(),
                exp.getQuantity(),
                exp.getSubtotal(),
                exp.getDescription()
            });
        }
        dashboardPanel.refreshDashboard();
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
