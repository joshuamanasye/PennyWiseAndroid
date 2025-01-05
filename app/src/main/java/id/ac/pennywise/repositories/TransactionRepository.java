package id.ac.pennywise.repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;
import id.ac.pennywise.utils.DatabaseHelper;

public class TransactionRepository {
    public final DatabaseHelper dbHelper;

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

    private TransactionModel createTransactionFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
        boolean isIncome = cursor.getInt(cursor.getColumnIndexOrThrow("income")) == 1;
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

        // Parse the date into LocalDate
        String dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);

        // Create the CategoryModel and TransactionModel
        CategoryModel category = new CategoryModel(categoryName, isIncome);
        return new TransactionModel(id, category, amount, description, date);
    }

}
