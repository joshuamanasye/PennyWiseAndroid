package id.ac.pennywise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

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

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

//        FragmentViewProject fragmentViewProject = new FragmentViewProject();
//        FragmentAddProject fragmentAddProject = new FragmentAddProject();
//
//        Button viewButton = findViewById(R.id.viewBtn);
//        Button addButton = findViewById(R.id.addBtn);
//
//        loadFragment(fragmentViewProject);
//
//        viewButton.setOnClickListener(v -> {
//            loadFragment(fragmentViewProject);
//        });
//
//        addButton.setOnClickListener(v -> {
//            loadFragment(fragmentAddProject);
//        });
    }
}