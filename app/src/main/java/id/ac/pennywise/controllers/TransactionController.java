package id.ac.pennywise.controllers;

import android.content.Context;

import id.ac.pennywise.handlers.TransactionHandler;
import id.ac.pennywise.models.TransactionModel;

public class TransactionController {
    private final TransactionHandler handler;

    public TransactionController(Context context) {
        handler = new TransactionHandler(context);
    }

    public TransactionModel getTransactionById(String transactionId) {
        return handler.getTransactionById(transactionId);
    }
}
