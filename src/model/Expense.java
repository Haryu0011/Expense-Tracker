package model;

import java.time.LocalDate;

public class Expense {
    private String id;
    private LocalDate date;
    private String category;
    private double unitPrice;
    private int quantity;
    private String description;

    public Expense(String id, LocalDate date, String category, double unitPrice, int quantity, String description) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.description = description;
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return unitPrice * quantity; }
    public String getDescription() { return description; }

    // CSV now includes 6 columns instead of 5
    public String toCSV() {
        return id + "," + date.toString() + "," + category + "," + unitPrice + "," + quantity + "," + description;
    }
}