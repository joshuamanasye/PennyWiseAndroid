package id.ac.pennywise.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import id.ac.pennywise.activities.LoginActivity;
import id.ac.pennywise.handlers.CategoryHandler;
import id.ac.pennywise.utils.PreferenceManager;

public class MainController {
    public static final int LOGIN_REQUEST_CODE = 1;

    public String getUserSession(Activity activity) {
        String userId = PreferenceManager.getUserSession(activity);

        if (userId == null) {
            // redirect to LoginActivity if no session exists
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        }

        return userId;
    }


    public void insertInitialCategories(Context context) {
        CategoryHandler categoryHandler = new CategoryHandler(context);

        categoryHandler.insertInitialCategories();
    }
}

