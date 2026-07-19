package model;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Manages a collection of expenses, including loading, saving,
 * adding, deleting, and calculating expense totals.
 */
public class ExpenseManager {
    private List<Expense> expenses = new ArrayList<>();
    private final String FILE_NAME = "expenses.csv";
    private final double MONTHLY_BUDGET = 500000.0;

    /**
     * Creates an ExpenseManager and loads existing expense data from file.
     */
    public ExpenseManager() {
        loadData();
    }

    /**
     * Adds a new expense and saves the updated data.
     *
     * @param exp the expense to add
     */
    public void addExpense(Expense exp) {
        expenses.add(exp);
        saveData();
    }

    /**
     * Deletes an expense by its ID and saves the updated data.
     *
     * @param id the ID of the expense to delete
     */
    public void deleteExpense(String id) {
        expenses.removeIf(exp -> exp.getId().equals(id));
        saveData();
    }

    /**
     * Returns all stored expenses.
     *
     * @return list of expenses
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Returns the monthly budget.
     *
     * @return monthly budget value
     */
    public double getMonthlyBudget() {
        return MONTHLY_BUDGET;
    }

    /**
     * Calculates the total expenses for a specific date, month, or all expenses.
     *
     * @param dateFilter the exact date to filter by, or null
     * @param monthFilter the month to filter by, or null
     * @return total expense amount
     */
    public double calculateTotal(LocalDate dateFilter, YearMonth monthFilter) {
        double total = 0;
        for (Expense exp : expenses) {
            if (dateFilter != null && exp.getDate().equals(dateFilter)) {
                total += exp.getSubtotal();
            } else if (monthFilter != null && YearMonth.from(exp.getDate()).equals(monthFilter)) {
                total += exp.getSubtotal();
            } else if (dateFilter == null && monthFilter == null) {
                total += exp.getSubtotal();
            }
        }
        return total;
    }

    /**
     * Returns the total spending per category for the current month.
     *
     * @return map of category names to total amounts
     */
    public Map<String, Double> getCategoryTotalsForCurrentMonth() {
        Map<String, Double> categoryTotals = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        for (Expense exp : expenses) {
            if (YearMonth.from(exp.getDate()).equals(currentMonth)) {
                categoryTotals.put(exp.getCategory(),
                    categoryTotals.getOrDefault(exp.getCategory(), 0.0) + exp.getSubtotal());
            }
        }
        return categoryTotals;
    }

    /**
     * Saves all expenses to the CSV file.
     */
    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense exp : expenses) {
                writer.println(exp.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    /**
     * Loads expenses from the CSV file if it exists.
     */
    private void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    expenses.add(new Expense(
                        data[0],
                        LocalDate.parse(data[1]),
                        data[2],
                        Double.parseDouble(data[3]),
                        Integer.parseInt(data[4]),
                        data[5]
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}
