package id.ac.pennywise.controllers;

import android.content.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.pennywise.factories.CategoryFactory;
import id.ac.pennywise.factories.TransactionFactory;
import id.ac.pennywise.handlers.TransactionHandler;
import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;

public class TransactionController {
    private final TransactionHandler handler;

    public TransactionController(Context context) {
        handler = new TransactionHandler(context);
    }

    public TransactionModel getTransactionById(String transactionId) {
        return handler.getTransactionById(transactionId);
    }

    public List<String> getCategoriesByType(boolean isIncome) {
        return handler.getAllCategories()
                .stream()
                .filter(category -> category.isIncome() == isIncome)
                .map(CategoryModel::getName)
                .collect(Collectors.toList());
    }

    public void insertTransaction(String categoryName, double amount, boolean isIncome, String description) {
        CategoryModel category = CategoryFactory.createCategory(categoryName, isIncome);
        TransactionModel transaction = TransactionFactory.createTransaction(category, amount, description, LocalDate.now());
        handler.addTransaction(transaction);
    }

    public boolean updateTransaction(TransactionModel transaction) {
        return handler.updateTransaction(transaction);
    }

    public int getCategoryPosition(String categoryName, boolean isIncome) {
        List<String> categories = getCategoriesByType(isIncome);
        return categories.indexOf(categoryName);
    }

    public boolean deleteTransaction(String transactionId) {
        return handler.deleteTransaction(transactionId);
    }

}
