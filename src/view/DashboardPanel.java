package view;

import model.ExpenseManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private ExpenseManager manager;
    private JLabel lblDailyTotal, lblMonthlyTotal, lblTotal, lblBudgetRemaining;
    private NativePieChart pieChart;

    public DashboardPanel(ExpenseManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDailyTotal = new JLabel();
        lblMonthlyTotal = new JLabel();
        lblTotal = new JLabel();
        lblBudgetRemaining = new JLabel();

        Font boldFont = new Font("Arial", Font.BOLD, 14);
        lblDailyTotal.setFont(boldFont);
        lblMonthlyTotal.setFont(boldFont);
        lblTotal.setFont(boldFont);
        lblBudgetRemaining.setFont(boldFont);

        summaryPanel.add(lblDailyTotal);
        summaryPanel.add(lblMonthlyTotal);
        summaryPanel.add(lblTotal);
        summaryPanel.add(lblBudgetRemaining);

        add(summaryPanel, BorderLayout.NORTH);

        pieChart = new NativePieChart();
        add(pieChart, BorderLayout.CENTER);
    }

    public void refreshDashboard() {
        double daily = manager.calculateTotal(LocalDate.now(), null);
        double monthly = manager.calculateTotal(null, YearMonth.now());
        double allTime = manager.calculateTotal(null, null);
        double remaining = manager.getMonthlyBudget() - monthly;

        lblDailyTotal.setText(String.format("Daily Total: Rp %,.0f", daily));
        lblMonthlyTotal.setText(String.format("Monthly Total: Rp %,.0f", monthly));
        lblTotal.setText(String.format("All-Time: Rp %,.0f", allTime));

        lblBudgetRemaining.setText(String.format("Budget Left: Rp %,.0f", remaining));
        lblBudgetRemaining.setForeground(remaining < 0 ? Color.RED : new Color(0, 128, 0));

        pieChart.setData(manager.getCategoryTotalsForCurrentMonth());
        pieChart.repaint(); // Tell Java to redraw the chart
    }

    // --- CUSTOM NATIVE PIE CHART (NO EXTERNAL JARS NEEDED) ---
    private class NativePieChart extends JPanel {
        private Map<String, Double> data;
        private Color[] colors = { Color.decode("#FF6B6B"), Color.decode("#4ECDC4"),
                Color.decode("#45B7D1"), Color.decode("#F9CA24"),
                Color.decode("#A55EEA") };

        public void setData(Map<String, Double> data) {
            this.data = data;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.drawString("No expenses this month to graph.", 20, 20);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            int startAngle = 0;
            int colorIndex = 0;
            int legendY = 20;

            // Draw the Pie and the Legend
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int arcAngle = (int) Math.round((entry.getValue() / total) * 360.0);

                g2d.setColor(colors[colorIndex % colors.length]);
                g2d.fillArc(50, 20, 250, 250, startAngle, arcAngle);

                // Draw Legend text
                g2d.fillRect(350, legendY - 10, 15, 15);
                g2d.setColor(Color.BLACK);
                String legendText = String.format("%s: Rp %,.0f", entry.getKey(), entry.getValue());
                g2d.drawString(legendText, 375, legendY + 2);

                startAngle += arcAngle;
                colorIndex++;
                legendY += 30;
            }
        }
    }
}
