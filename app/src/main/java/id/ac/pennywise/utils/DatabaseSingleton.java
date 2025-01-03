package id.ac.pennywise.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseSingleton {
    private static DatabaseSingleton instance;
    private final SQLiteDatabase database;

    private DatabaseSingleton(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.database = dbHelper.getWritableDatabase();
    }

    public static synchronized DatabaseSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseSingleton(context.getApplicationContext());
        }

        return instance;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
