package id.ac.pennywise.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PennyWiseDB";
    private static final int DATABASE_VERSION = 1;

    // tables
    public static final String TABLE_TRANSACTIONS = "Transactions";
    public static final String TABLE_BUDGETS = "Budgets";

    // create table queries
    private static final String CREATE_TABLE_TRANSACTIONS =
            "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                    "id INTEGER PRIMARY KEY, " +
                    "user_id INTEGER NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "description TEXT, " +
                    "date TEXT NOT NULL)";

    private static final String CREATE_TABLE_BUDGETS =
            "CREATE TABLE " + TABLE_BUDGETS + " (" +
                    "id INTEGER PRIMARY KEY, " +
                    "user_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "amount REAL NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_BUDGETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);

        onCreate(db);
    }
}
