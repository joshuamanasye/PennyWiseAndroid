package id.ac.pennywise.models;

public class CategoryModel {
    private String name;
    private boolean isIncome;

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
}