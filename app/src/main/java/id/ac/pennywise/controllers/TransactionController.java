package id.ac.pennywise.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

        // insert into firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String userId = PreferenceManager.getUserSession(context);

        String transactionId = dbRef.child("users").child(userId).child("transactions").push().getKey();
        TransactionModel transaction = TransactionFactory.createTransaction(transactionId, category, amount, description, LocalDate.now());

        Log.d("TransactionKey", "Generated Key/ID: " + transaction.getId());

        if (transactionId != null) {
            dbRef.child("users").child(userId).child("transactions").child(transactionId).setValue(transaction.toFirebaseMap());
        }

        handler.addTransaction(transaction);
    }

    public boolean updateTransaction(TransactionModel transaction, Context context) {
        if (handler.updateTransaction(transaction)) {
            // update firebase
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            String userId = PreferenceManager.getUserSession(context);
            dbRef.child("users").child(userId).child("transactions").child(transaction.getId()).setValue(transaction.toFirebaseMap());

            Log.d("TransactionKey", "Edited transaction: " + transaction.getId());

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
            Toast.makeText(context, transactionId, Toast.LENGTH_LONG).show();
            // delete from Firebase
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            String userId = PreferenceManager.getUserSession(context);
            dbRef.child("users").child(userId).child("transactions").child(transactionId).removeValue();

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

    public void initFirebase(String userId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(userId).child("categories").get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().exists()) {
                        List<CategoryModel> defaultCategories = new ArrayList<>();
                        defaultCategories.add(CategoryFactory.createCategory("Food", false));
                        defaultCategories.add(CategoryFactory.createCategory("Salary", true));
                        defaultCategories.add(CategoryFactory.createCategory("Entertainment", false));

                        for (CategoryModel category : defaultCategories) {
                            dbRef.child("users").child(userId).child("categories").push().setValue(category.toFirebaseMap());
                        }
                    }
                });

    }

    public void loadDataFromFirebase(Context context, String userId, UserController.LoginCallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        initFirebase(userId);

        // clear local categories and transactions
        clearCategories();
        clearTransactions();

        // load categories from Firebase
        dbRef.child("users").child(userId).child("categories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<CategoryModel> categories = new ArrayList<>();
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            String name = snapshot.child("categoryName").getValue(String.class);
                            String type = snapshot.child("type").getValue(String.class);

                            if (name != null && type != null) {
                                // Check if the category is income or expense
                                boolean isIncome = type.equalsIgnoreCase("Income");
                                categories.add(CategoryFactory.createCategory(name, isIncome));
                            }
                        }

                        insertCategories(categories);
                    }
                });

        // load transactions from Firebase
        dbRef.child("users").child(userId).child("transactions").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<TransactionModel> transactions = new ArrayList<>();
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            String id = snapshot.getKey();
                            String categoryName = snapshot.child("categoryName").getValue(String.class);
                            Double amount = snapshot.child("amount").getValue(Double.class);
                            String type = snapshot.child("type").getValue(String.class);
                            String dateStr = snapshot.child("date").getValue(String.class);
                            String description = snapshot.child("description").getValue(String.class);

                            if (id != null && categoryName != null && amount != null && type != null && dateStr != null) {
                                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);

                                boolean isIncome = type.equalsIgnoreCase("Income");
                                CategoryModel category = CategoryFactory.createCategory(categoryName, isIncome);

                                transactions.add(TransactionFactory.createTransaction(id, category, amount, description, date));
                            }
                        }

                        insertTransactions(transactions);
                    }

                    callback.onLoginComplete(true);
                });

        // load balance
        dbRef.child("users").child(userId).child("balance").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Double balance = task.getResult().getValue(Double.class);

                        if (balance == null) {
                            balance = 0.0;
                            dbRef.child("users").child(userId).child("balance").setValue(balance); // Initialize in Firebase
                        }

                        PreferenceManager.setUserBalance(context, balance);
                    } else {
                        Exception e = task.getException();
                        e.printStackTrace();
                    }
                });
    }

}
