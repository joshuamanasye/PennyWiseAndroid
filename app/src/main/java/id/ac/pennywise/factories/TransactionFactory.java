package id.ac.pennywise.factories;

import java.time.LocalDate;

import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;

public class TransactionFactory {
    public static TransactionModel createTransaction(String id, CategoryModel category, double amount, String description, LocalDate date) {
        return new TransactionModel(id, category, amount, description, date);
    }
}
