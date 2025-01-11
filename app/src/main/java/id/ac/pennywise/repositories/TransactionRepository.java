package id.ac.pennywise.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;
import id.ac.pennywise.utils.DatabaseHelper;

public class TransactionRepository {
    private final DatabaseHelper dbHelper;

    public TransactionRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public TransactionModel getTransactionById(String transactionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        TransactionModel transaction = null;

        String query = "SELECT t.id, t.category_name, t.amount, t.description, t.date, c.income " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_name = c.category_name " +
                "WHERE t.id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{transactionId});

        if (cursor.moveToFirst()) {
            transaction = createTransactionFromCursor(cursor);
        }

        cursor.close();
        db.close();

        return transaction;
    }

    public void addTransaction(TransactionModel transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("category_name", transaction.getCategory().getName());
        values.put("amount", transaction.getAmount());
        values.put("description", transaction.getDescription());
        values.put("date", transaction.getDate().format(DateTimeFormatter.ISO_DATE));

        db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values);
        db.close();
    }

    public List<TransactionModel> getAllTransactions() {
        List<TransactionModel> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t.id, t.category_name, t.amount, t.description, t.date, c.income " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_name = c.category_name";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            transactions.add(createTransactionFromCursor(cursor));
        }

        cursor.close();
        db.close();

        return transactions;
    }

    private TransactionModel createTransactionFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
        boolean isIncome = cursor.getInt(cursor.getColumnIndexOrThrow("income")) == 1;
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

        String dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);

        CategoryModel category = new CategoryModel(categoryName, isIncome);
        return new TransactionModel(id, category, amount, description, date);
    }

    public boolean updateTransaction(TransactionModel transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("category_name", transaction.getCategory().getName());
        values.put("amount", transaction.getAmount());
        values.put("description", transaction.getDescription());
        values.put("date", transaction.getDate().format(DateTimeFormatter.ISO_DATE));

        int rowsUpdated = db.update(DatabaseHelper.TABLE_TRANSACTIONS, values, "id = ?", new String[]{transaction.getId()});
        db.close();

        return rowsUpdated > 0;
    }

    public boolean deleteTransaction(String transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_TRANSACTIONS, "id = ?", new String[]{transactionId});
        db.close();
        return rowsDeleted > 0;
    }
}
