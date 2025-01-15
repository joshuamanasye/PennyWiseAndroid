package id.ac.pennywise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import id.ac.pennywise.R;
import id.ac.pennywise.controllers.TransactionController;
import id.ac.pennywise.utils.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView emailTxt;
    private Button logoutBtn;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }
        String userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        emailTxt = findViewById(R.id.emailTxt);
        logoutBtn = findViewById(R.id.logoutBtn);

        emailTxt.setText(currentUser.getEmail());

        logoutBtn.setOnClickListener(v -> {
            logoutUser();
        });
    }

    private void logoutUser() {
        PreferenceManager.clearUserSession(this);

        TransactionController controller = new TransactionController(this);
        controller.clearTransactions();
        controller.clearCategories();

        firebaseAuth.signOut();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
