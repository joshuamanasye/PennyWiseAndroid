package id.ac.pennywise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import id.ac.pennywise.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEdt, passwordEdt, confirmPasswordEdt;
    private Button registerBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        confirmPasswordEdt = findViewById(R.id.confirmPasswordEdt);
        registerBtn = findViewById(R.id.registerBtn);
        setLoginLink();

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void setLoginLink() {
        TextView loginLink = findViewById(R.id.loginTxt);

        String fullText = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(fullText);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(RegisterActivity.this, R.color.green_teal));
        spannableString.setSpan(colorSpan, fullText.indexOf("Login"), fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginLink.setText(spannableString);

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // waktunya gk cukup bu pake mvc, kalau saya lanjut project ini saya baru benerin lagi
    private void registerUser() {
        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        String confirmPassword = confirmPasswordEdt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEdt.setError("Email is required");
            emailEdt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEdt.setError("Password is required");
            passwordEdt.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEdt.setError("Password must be at least 6 characters");
            passwordEdt.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEdt.setError("Passwords do not match");
            confirmPasswordEdt.requestFocus();
            return;
        }

        // firebase registration
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();

                        // redirect to login
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
