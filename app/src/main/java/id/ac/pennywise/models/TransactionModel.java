package id.ac.pennywise.models;

import java.time.LocalDate;
import java.util.Date;

public class TransactionModel {
    private String id;
    private CategoryModel category;
    private double amount;
    private String description;
    private LocalDate date;

    public TransactionModel(String id, CategoryModel category, double amount, String description, LocalDate date) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
