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

    public void insertCategory(CategoryModel category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_INCOME, category.isIncome());
        db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
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

        return !exists;
    }
}
