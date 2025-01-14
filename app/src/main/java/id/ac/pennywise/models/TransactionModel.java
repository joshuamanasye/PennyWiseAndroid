package id.ac.pennywise.models;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransactionModel {
    private String id;
    private CategoryModel category;
    private double amount;
    private String description;
    private LocalDate date;

    public Map<String, Object> toFirebaseMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("categoryName", category.getName());
        map.put("amount", amount);
        map.put("description", description);
        map.put("date", date.toString());
        map.put("type", category.isIncome() ? "Income" : "Expense");

        return map;
    }

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
