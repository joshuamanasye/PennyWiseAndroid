package id.ac.pennywise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import id.ac.pennywise.R;
import id.ac.pennywise.controllers.TransactionController;
import id.ac.pennywise.controllers.UserController;
import id.ac.pennywise.utils.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdt, passwordEdt;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        loginBtn = findViewById(R.id.loginBtn);

        setRegisterLink();

        loginBtn.setOnClickListener(v -> loginUser());
    }

    private void setRegisterLink() {
        TextView registerLink = findViewById(R.id.registerTxt);

        String fullText = "Don't have an account? Register";
        SpannableString spannableString = new SpannableString(fullText);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(LoginActivity.this, R.color.green_teal));
        spannableString.setSpan(colorSpan, fullText.indexOf("Register"), fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerLink.setText(spannableString);

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser() {
        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        UserController userController = new UserController(this);
        userController.login(email, password, success -> {
            if (success) {
                String userId = PreferenceManager.getUserSession(this);

                TransactionController transactionController = new TransactionController(this);
                transactionController.loadDataFromFirebase(this, userId, isDataLoaded -> {
                    if (isDataLoaded) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
