package id.ac.pennywise.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PennyWiseDB";
    private static final int DATABASE_VERSION = 2;

    // tables
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_BUDGETS = "budgets";

    // categories column
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_INCOME = "income";

    // transactions column
    public static final String COLUMN_TRANSACTION_ID = "id";
    public static final String COLUMN_TRANSACTION_CATEGORY_NAME = "category_name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";

    // budgets column
    public static final String COLUMN_BUDGET_CATEGORY_NAME = "category_name";
    public static final String COLUMN_BUDGET_AMOUNT = "amount";

    // table queries
    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COLUMN_CATEGORY_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_INCOME + " BOOLEAN NOT NULL)";

    private static final String CREATE_TABLE_TRANSACTIONS =
            "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                    COLUMN_TRANSACTION_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_TRANSACTION_CATEGORY_NAME + " TEXT NOT NULL, " +
                    COLUMN_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_DATE + " DATE NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_TRANSACTION_CATEGORY_NAME + ") REFERENCES " +
                    TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_NAME + "))";

    private static final String CREATE_TABLE_BUDGETS =
            "CREATE TABLE " + TABLE_BUDGETS + " (" +
                    COLUMN_BUDGET_CATEGORY_NAME + " TEXT NOT NULL, " +
                    COLUMN_BUDGET_AMOUNT + " REAL NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_BUDGET_CATEGORY_NAME + ") REFERENCES " +
                    TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_NAME + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] expenseCategories = {"Food & Beverage", "Entertainment", "Bill", "Transportation"};
        String[] incomeCategories = {"Salary", "Pocket Money"};

        for (String category : expenseCategories) {
            db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" +
                    COLUMN_CATEGORY_NAME + ", " + COLUMN_INCOME + ") VALUES ('" + category + "', 0)");
        }
        for (String category : incomeCategories) {
            db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" +
                    COLUMN_CATEGORY_NAME + ", " + COLUMN_INCOME + ") VALUES ('" + category + "', 1)");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        db.execSQL(CREATE_TABLE_BUDGETS);

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
