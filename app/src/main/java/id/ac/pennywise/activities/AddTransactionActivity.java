package id.ac.pennywise.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.stream.Collectors;

import id.ac.pennywise.R;
import id.ac.pennywise.handlers.CategoryHandler;
import id.ac.pennywise.models.CategoryModel;
import id.ac.pennywise.repositories.CategoryRepository;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText amountEdt;
    private RadioGroup typeRg;
    private Spinner categorySpinner;
    private Button addTransactionBtn;

    private CategoryRepository categoryRepository;

    private void populateCategories(boolean isIncome) {
        // Filter categories by type
        List<CategoryModel> categories = categoryRepository.getAllCategories()
                .stream()
                .filter(category -> category.isIncome() == isIncome)
                .collect(Collectors.toList());

        // Map to names for Spinner
        List<String> categoryNames = categories.stream()
                .map(CategoryModel::getName)
                .collect(Collectors.toList());

        // Populate Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void addTransaction() {
        String amountStr = amountEdt.getText().toString().trim();
        String selectedCategory = (String) categorySpinner.getSelectedItem();

        if (amountStr.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        boolean isIncome = typeRg.getCheckedRadioButtonId() == R.id.incomeRb;

        // Here, handle transaction insertion into the database
        // Example:
        // transactionHandler.addTransaction(selectedCategory, amount, isIncome);

        Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI Components
        amountEdt = findViewById(R.id.amountEdt);
        typeRg = findViewById(R.id.typeRg);
        categorySpinner = findViewById(R.id.categorySpinner);
        addTransactionBtn = findViewById(R.id.addTransactionBtn);

        // Initialize Repository and Handler
        categoryRepository = new CategoryRepository(this);
        CategoryHandler categoryHandler = new CategoryHandler(this);

        // Ensure default categories exist
        categoryHandler.insertInitialCategories();

        // Populate categories (default to expense)
        populateCategories(false);

        // Handle type changes (Expense/Income)
        typeRg.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isIncome = checkedId == R.id.incomeRb;
            populateCategories(isIncome);
        });

        // Handle Add Transaction Button Click
        addTransactionBtn.setOnClickListener(view -> addTransaction());
    }
}