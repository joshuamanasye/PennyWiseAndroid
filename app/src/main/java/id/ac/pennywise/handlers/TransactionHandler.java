package id.ac.pennywise.handlers;

import android.content.Context;

import java.util.List;

import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;
import id.ac.pennywise.repositories.CategoryRepository;
import id.ac.pennywise.repositories.TransactionRepository;

public class TransactionHandler {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public TransactionHandler(Context context) {
        categoryRepository = new CategoryRepository(context);
        transactionRepository = new TransactionRepository(context);
    }

    public List<CategoryModel> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public TransactionModel getTransactionById(String transactionId) {
        return transactionRepository.getTransactionById(transactionId);
    }

    public void addTransaction(TransactionModel transaction) {
        transactionRepository.addTransaction(transaction);
    }

    public List<TransactionModel> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }
}
