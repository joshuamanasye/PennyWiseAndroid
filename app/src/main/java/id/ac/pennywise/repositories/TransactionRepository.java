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

        String query = "SELECT t." + DatabaseHelper.COLUMN_TRANSACTION_ID + ", t." + DatabaseHelper.COLUMN_TRANSACTION_CATEGORY_NAME +
                ", t." + DatabaseHelper.COLUMN_AMOUNT + ", t." + DatabaseHelper.COLUMN_DESCRIPTION +
                ", t." + DatabaseHelper.COLUMN_DATE + ", c." + DatabaseHelper.COLUMN_INCOME +
                " FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                "JOIN " + DatabaseHelper.TABLE_CATEGORIES + " c ON t." + DatabaseHelper.COLUMN_TRANSACTION_CATEGORY_NAME +
                " = c." + DatabaseHelper.COLUMN_CATEGORY_NAME +
                " WHERE t." + DatabaseHelper.COLUMN_TRANSACTION_ID + " = ?";
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
        values.put(DatabaseHelper.COLUMN_TRANSACTION_CATEGORY_NAME, transaction.getCategory().getName());
        values.put(DatabaseHelper.COLUMN_AMOUNT, transaction.getAmount());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(DatabaseHelper.COLUMN_DATE, transaction.getDate().format(DateTimeFormatter.ISO_DATE));

        db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values);
        db.close();
    }

    public List<TransactionModel> getAllTransactions() {
        List<TransactionModel> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t." + DatabaseHelper.COLUMN_TRANSACTION_ID + ", t." + DatabaseHelper.COLUMN_TRANSACTION_CATEGORY_NAME +
                ", t." + DatabaseHelper.COLUMN_AMOUNT + ", t." + DatabaseHelper.COLUMN_DESCRIPTION +
                ", t." + DatabaseHelper.COLUMN_DATE + ", c." + DatabaseHelper.COLUMN_INCOME +
                " FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                "JOIN " + DatabaseHelper.TABLE_CATEGORIES + " c ON t." + DatabaseHelper.COLUMN_TRANSACTION_CATEGORY_NAME +
                " = c." + DatabaseHelper.COLUMN_CATEGORY_NAME +
                " ORDER BY t." + DatabaseHelper.COLUMN_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            transactions.add(createTransactionFromCursor(cursor));
        }

        cursor.close();
        db.close();

        return transactions;
    }

    private TransactionModel createTransactionFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSACTION_ID));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSACTION_CATEGORY_NAME));
        boolean isIncome = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME)) == 1;
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));

        String dateString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);

        CategoryModel category = new CategoryModel(categoryName, isIncome);
        return new TransactionModel(id, category, amount, description, date);
    }

    public boolean updateTransaction(TransactionModel transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TRANSACTION_CATEGORY_NAME, transaction.getCategory().getName());
        values.put(DatabaseHelper.COLUMN_AMOUNT, transaction.getAmount());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(DatabaseHelper.COLUMN_DATE, transaction.getDate().format(DateTimeFormatter.ISO_DATE));

        int rowsUpdated = db.update(DatabaseHelper.TABLE_TRANSACTIONS, values,
                DatabaseHelper.COLUMN_TRANSACTION_ID + " = ?",
                new String[]{transaction.getId()});
        db.close();

        return rowsUpdated > 0;
    }

    public boolean deleteTransaction(String transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_TRANSACTIONS,
                DatabaseHelper.COLUMN_TRANSACTION_ID + " = ?",
                new String[]{transactionId});
        db.close();
        return rowsDeleted > 0;
    }
}
