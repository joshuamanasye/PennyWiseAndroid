package id.ac.pennywise.models;

import java.util.HashMap;
import java.util.Map;

public class CategoryModel {
    private String name;
    private boolean isIncome;

    public Map<String, Object> toFirebaseMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("categoryName", name);
        map.put("type", isIncome ? "Income" : "Expense");
        return map;
    }

    public CategoryModel(String name, boolean isIncome) {
        this.name = name;
        this.isIncome = isIncome;
    }

    public String getName() {
        return name;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
}