package id.ac.pennywise.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import id.ac.pennywise.activities.LoginActivity;
import id.ac.pennywise.handlers.CategoryHandler;
import id.ac.pennywise.utils.SessionManager;

public class MainController {
    public static final int LOGIN_REQUEST_CODE = 1; // Code for identifying login activity result

    public String getUserSession(Activity activity) {
        String userId = SessionManager.getUserSession(activity);

        if (userId == null) {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }

        return userId;
    }

    public void insertInitialCategories(Context context) {
        CategoryHandler categoryHandler = new CategoryHandler(context);

        categoryHandler.insertInitialCategories();
    }
}
