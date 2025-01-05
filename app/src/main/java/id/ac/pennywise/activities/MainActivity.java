package id.ac.pennywise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import id.ac.pennywise.R;
import id.ac.pennywise.controllers.MainController;
import id.ac.pennywise.fragments.HomeFragment;
import id.ac.pennywise.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private MainController controller;
    private String userId;

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentView, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // change navigation bar color
        getWindow().setNavigationBarColor(getResources().getColor(R.color.grey1, getTheme()));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // tes user
//        SessionManager.saveUserSession(MainActivity.this, "u9xlmpCBWqaiLc5TaggS7fbyXyF3");
//        SessionManager.clearUserSession(MainActivity.this);

        controller = new MainController();

        userId = controller.getUserSession(MainActivity.this);

        // init categories
        controller.insertInitialCategories(MainActivity.this);

        // fragments
        HomeFragment homeFragment = new HomeFragment();

        // nav buttons
        Button homeBtn = findViewById(R.id.homeBtn);
        Button transactionBtn = findViewById(R.id.transactionBtn);
        ImageButton addBtn = findViewById(R.id.addTransactionBtn);
        Button reportBtn = findViewById(R.id.reportBtn);
        Button budgetBtn = findViewById(R.id.budgetBtn);

        homeBtn.setOnClickListener(v -> {
            loadFragment(homeFragment);
        });

        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // init home
        loadFragment(homeFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainController.LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            // user logged in, fetch the session again
            userId = SessionManager.getUserSession(this);
        } else {
            // user cancels login
            finish();
        }
    }
}