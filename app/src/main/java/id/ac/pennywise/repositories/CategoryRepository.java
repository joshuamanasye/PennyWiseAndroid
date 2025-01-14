package id.ac.pennywise.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.utils.DatabaseHelper;

public class CategoryRepository {
    private final DatabaseHelper dbHelper;

    public CategoryRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Insert a single category
    public void insertCategory(CategoryModel category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_INCOME, category.isIncome() ? 1 : 0); // Store boolean as integer
        db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        db.close();
    }

    public void insertCategories(List<CategoryModel> categories) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            for (CategoryModel category : categories) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
                values.put(DatabaseHelper.COLUMN_INCOME, category.isIncome() ? 1 : 0);
                db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public List<CategoryModel> getAllCategories() {
        List<CategoryModel> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
            boolean income = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME)) == 1;
            categories.add(new CategoryModel(name, income));
        }
        cursor.close();
        db.close();

        return categories;
    }

    public boolean notExists(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES,
                null,
                DatabaseHelper.COLUMN_CATEGORY_NAME + " = ?",
                new String[]{name},
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return !exists;
    }

    public boolean updateCategory(String oldName, String newName, boolean isIncome) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, newName);
        values.put(DatabaseHelper.COLUMN_INCOME, isIncome ? 1 : 0);

        int rowsUpdated = db.update(DatabaseHelper.TABLE_CATEGORIES,
                values,
                DatabaseHelper.COLUMN_CATEGORY_NAME + " = ?",
                new String[]{oldName});

        db.close();
        return rowsUpdated > 0;
    }

    public boolean deleteCategory(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_CATEGORIES,
                DatabaseHelper.COLUMN_CATEGORY_NAME + " = ?",
                new String[]{name});
        db.close();
        return rowsDeleted > 0;
    }

    public void clearAllCategories() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CATEGORIES, null, null);
        db.close();
    }
}
