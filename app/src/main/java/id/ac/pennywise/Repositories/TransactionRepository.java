package id.ac.pennywise.Repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import id.ac.pennywise.utils.DatabaseSingleton;

public class TransactionRepository {
    private final SQLiteDatabase db;

    public TransactionRepository(Context context) {
        this.db = DatabaseSingleton.getInstance(context).getDatabase();
    }


}
