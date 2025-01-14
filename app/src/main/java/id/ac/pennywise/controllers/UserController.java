package id.ac.pennywise.controllers;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import id.ac.pennywise.factories.CategoryFactory;
import id.ac.pennywise.factories.TransactionFactory;
import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.models.TransactionModel;
import id.ac.pennywise.utils.PreferenceManager;

public class UserController {

    private final Context context;

    public UserController(Context context) {
        this.context = context;
    }

    public interface LoginCallback {
        void onLoginComplete(boolean success);
    }

    public void login(String email, String password, LoginCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            PreferenceManager.saveUserSession(context, userId);

                            callback.onLoginComplete(true);
                        } else {
                            callback.onLoginComplete(false);
                        }
                    } else {
                        callback.onLoginComplete(false);
                    }
                });
    }

}
