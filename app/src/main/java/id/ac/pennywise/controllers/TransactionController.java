package id.ac.pennywise.controllers;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.pennywise.factories.CategoryFactory;
import id.ac.pennywise.factories.TransactionFactory;
import id.ac.pennywise.handlers.TransactionHandler;
import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;
import id.ac.pennywise.utils.PreferenceManager;

public class TransactionController {
    private final TransactionHandler handler;

    public TransactionController(Context context) {
        handler = new TransactionHandler(context);
    }

    public TransactionModel getTransactionById(String transactionId) {
        return handler.getTransactionById(transactionId);
    }

    public List<TransactionModel> getAllTransactions() {
        return handler.getAllTransactions();
    }

    public List<String> getCategoriesByType(boolean isIncome) {
        return handler.getAllCategories()
                .stream()
                .filter(category -> category.isIncome() == isIncome)
                .map(CategoryModel::getName)
                .collect(Collectors.toList());
    }

    public void insertTransaction(String categoryName, double amount, boolean isIncome, String description, Context context) {
        CategoryModel category = CategoryFactory.createCategory(categoryName, isIncome);
        TransactionModel transaction = TransactionFactory.createTransaction(category, amount, description, LocalDate.now());
        handler.addTransaction(transaction);

        // insert into firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String userId = PreferenceManager.getUserSession(context);
        String transactionId = dbRef.child("transactions").child(userId).push().getKey();
        if (transactionId != null) {
            dbRef.child("transactions").child(userId).child(transactionId).setValue(transaction.toFirebaseMap());
        }
    }

    public boolean updateTransaction(TransactionModel transaction, Context context) {
        if (handler.updateTransaction(transaction)) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            String userId = PreferenceManager.getUserSession(context);
            dbRef.child("transactions").child(userId).child(transaction.getId()).setValue(transaction.toFirebaseMap());
            return true;
        }
        return false;
    }

    public int getCategoryPosition(String categoryName, boolean isIncome) {
        List<String> categories = getCategoriesByType(isIncome);
        return categories.indexOf(categoryName);
    }

    public boolean deleteTransaction(String transactionId, Context context) {
        TransactionModel transaction = handler.getTransactionById(transactionId);

        if (transaction != null && handler.deleteTransaction(transactionId)) {
            // delete from Firebase
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            String userId = PreferenceManager.getUserSession(context);
            dbRef.child("transactions").child(userId).child(transactionId).removeValue();
            return true;
        }

        return false;
    }

    public void addBalance(Context context, double amount) {
        double newBalance = PreferenceManager.getUserBalance(context) + amount;

        PreferenceManager.setUserBalance(context, newBalance);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String userId = PreferenceManager.getUserSession(context);
        dbRef.child("users").child(userId).child("balance").setValue(newBalance);
    }

    public void clearCategories() {
        handler.clearCategories();
    }

    public void clearTransactions() {
        handler.clearTransactions();
    }

    public void insertCategories(List<CategoryModel> categories) {
        for (CategoryModel category : categories) {
            handler.addCategory(category);
        }
    }

    public void insertTransactions(List<TransactionModel> transactions) {
        for (TransactionModel transaction : transactions) {
            handler.addTransaction(transaction);
        }
    }

    public void loadDataFromFirebase(String userId, UserController.LoginCallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // clear local categories and transactions
        clearCategories();
        clearTransactions();

        // load categories from Firebase
        dbRef.child("categories").orderByChild("userId").equalTo(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<CategoryModel> categories = new ArrayList<>();
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            String name = snapshot.child("name").getValue(String.class);
                            boolean isIncome = Boolean.TRUE.equals(snapshot.child("isIncome").getValue(Boolean.class));

                            if (name != null) {
                                categories.add(CategoryFactory.createCategory(name, isIncome));
                            }
                        }
                        insertCategories(categories);
                    }
                });

        // load transactions from Firebase
        dbRef.child("transactions").orderByChild("userId").equalTo(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<TransactionModel> transactions = new ArrayList<>();
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            String id = snapshot.child("id").getValue(String.class);
                            String categoryName = snapshot.child("category").getValue(String.class);
                            Double amount = snapshot.child("amount").getValue(Double.class);
                            String description = snapshot.child("description").getValue(String.class);
                            String dateStr = snapshot.child("date").getValue(String.class);

                            if (id != null && categoryName != null && amount != null && dateStr != null) {
                                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                                boolean isIncome = getCategoriesByType(true).contains(categoryName);
                                CategoryModel category = CategoryFactory.createCategory(categoryName, isIncome);

                                transactions.add(TransactionFactory.createTransaction(id, category, amount, description, date));
                            }
                        }

                        insertTransactions(transactions);
                    }

                    // notify that data load is complete
                    callback.onLoginComplete(true);
                });
    }

}
