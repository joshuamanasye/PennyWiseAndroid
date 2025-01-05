package id.ac.pennywise.factories;

import id.ac.pennywise.models.CategoryModel;

public class CategoryFactory {
    public static CategoryModel createCategory(String name, boolean income) {
        return new CategoryModel(name, income);
    }
}
