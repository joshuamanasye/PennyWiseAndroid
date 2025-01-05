package id.ac.pennywise.handlers;

import android.content.Context;

import id.ac.pennywise.factories.CategoryFactory;
import id.ac.pennywise.repositories.CategoryRepository;

public class CategoryHandler {
    private final CategoryRepository repository;

    public CategoryHandler(Context context) {
        repository = new CategoryRepository(context);
    }

    public void insertInitialCategories() {
        String[] expenseCategories = {"Food & Beverage", "Entertainment", "Bill", "Transportation"};
        String[] incomeCategories = {"Salary", "Pocket Money"};

        for (String category : expenseCategories) {
            if (!repository.isExists(category)) {
                repository.insertCategory(CategoryFactory.createCategory(category, false));
            }
        }

        for (String category : incomeCategories) {
            if (!repository.isExists(category)) {
                repository.insertCategory(CategoryFactory.createCategory(category, true));
            }
        }
    }
}
