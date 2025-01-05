package id.ac.pennywise.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;
import id.ac.pennywise.repositories.TransactionRepository;
import id.ac.pennywise.utils.DatabaseHelper;

public class TransactionHandler {
    private final TransactionRepository repository;

    public TransactionHandler(Context context) {
        repository = new TransactionRepository(context);
    }

    public List<TransactionModel> getAllTransaction() {
        SQLiteDatabase db = repository.dbHelper.getReadableDatabase();
        List<TransactionModel> transactions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT t.id, t.amount, t.description, t.date, c.category_name, c.income " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_name = c.category_name", null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                boolean isIncome = cursor.getInt(cursor.getColumnIndexOrThrow("income")) == 1;
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                CategoryModel category = new CategoryModel(categoryName, isIncome);
                TransactionModel transaction = new TransactionModel(id, category, amount, description, LocalDate.parse(dateStr));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return transactions;
    }

    public void addTransaction(TransactionModel transaction) {
        SQLiteDatabase db = repository.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", transaction.getCategory().getName());
        values.put("amount", transaction.getAmount());
        values.put("description", transaction.getDescription());
        values.put("date", transaction.getDate().toString());

        db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values);
    }

    public TransactionModel getTransactionById(String transactionId) {
        return repository.getTransactionById(transactionId);
    }

}
