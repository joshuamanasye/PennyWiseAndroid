package id.ac.pennywise.controllers;

import android.content.Context;

import java.util.List;

import id.ac.pennywise.handlers.TransactionHandler;
import id.ac.pennywise.models.TransactionModel;

public class HomeController {
    private final TransactionHandler transactionHandler;

    public HomeController(Context context) {
        transactionHandler = new TransactionHandler(context);
    }

    public List<TransactionModel> getAllTransactions() {
        return transactionHandler.getAllTransactions();
    }
}
