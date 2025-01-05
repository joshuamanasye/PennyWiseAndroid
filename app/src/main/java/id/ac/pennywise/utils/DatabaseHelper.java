package id.ac.pennywise.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PennyWiseDB";
    private static final int DATABASE_VERSION = 1;

    // tables
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_BUDGETS = "budgets";

    // create table queries
    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    "category_name TEXT PRIMARY KEY, " +
                    "income BOOLEAN NOT NULL)";
    private static final String CREATE_TABLE_TRANSACTIONS =
            "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_name TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "description TEXT, " +
                    "date DATE NOT NULL, " +
                    "FOREIGN KEY (category_name) REFERENCES " + TABLE_CATEGORIES + "(category_name))";

    private static final String CREATE_TABLE_BUDGETS =
            "CREATE TABLE " + TABLE_BUDGETS + " (" +
                    "category_name TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "FOREIGN KEY (category_name) REFERENCES " + TABLE_CATEGORIES + "(category_name))";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] expenseCategories = {"Food & Beverage", "Entertainment", "Bill", "Transportation"};
        String[] incomeCategories = {"Salary", "Pocket Money"};

        for (String category : expenseCategories) {
            db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (category_name, income) VALUES ('" + category + "', 0)");
        }
        for (String category : incomeCategories) {
            db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (category_name, income) VALUES ('" + category + "', 1)");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_BUDGETS);

        // insert default categories
        insertDefaultCategories(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);

        onCreate(db);
    }

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

}
