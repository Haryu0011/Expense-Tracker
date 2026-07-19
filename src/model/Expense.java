package model;

import java.time.LocalDate;

/**
 * Represents a single expense entry with date, category, price, quantity,
 * and description.
 */
public class Expense {
    private String id;
    private LocalDate date;
    private String category;
    private double unitPrice;
    private int quantity;
    private String description;

    /**
     * Creates a new expense.
     *
     * @param id the unique expense ID
     * @param date the expense date
     * @param category the expense category
     * @param unitPrice the price per item
     * @param quantity the number of items
     * @param description the expense description
     */
    public Expense(String id, LocalDate date, String category, double unitPrice, int quantity, String description) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.description = description;
    }

    /**
     * Returns the expense ID.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the expense date.
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the expense category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the unit price.
     *
     * @return the unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Returns the quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Calculates and returns the subtotal.
     *
     * @return unitPrice multiplied by quantity
     */
    public double getSubtotal() {
        return unitPrice * quantity;
    }

    /**
     * Returns the expense description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Converts this expense to a CSV-formatted string.
     *
     * @return CSV representation of the expense
     */
    public String toCSV() {
        return id + "," + date.toString() + "," + category + "," + unitPrice + "," + quantity + "," + description;
    }
}
