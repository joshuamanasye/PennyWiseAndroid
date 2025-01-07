package id.ac.pennywise.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

import id.ac.pennywise.R;
import id.ac.pennywise.controllers.TransactionController;
import id.ac.pennywise.handlers.CategoryHandler;

public class AddTransactionActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText amountEdt, descriptionEdt;
    private RadioGroup typeRg;
    private Spinner categorySpinner;
    private Button addTransactionBtn;

    private TransactionController transactionController;

    private void setRadioColor() {
        RadioButton expenseRb = findViewById(R.id.expenseRb);
        RadioButton incomeRb = findViewById(R.id.incomeRb);

        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.green_teal);
        expenseRb.setButtonTintList(colorStateList);
        incomeRb.setButtonTintList(colorStateList);
    }

    private void populateCategories(boolean isIncome) {
        List<String> categoryNames = transactionController.getCategoriesByType(isIncome);

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

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please inset amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String selectedCategory = (String) categorySpinner.getSelectedItem();
        boolean isIncome = typeRg.getCheckedRadioButtonId() == R.id.incomeRb;
        String description = descriptionEdt.getText().toString();

        transactionController.insertTransaction(selectedCategory, amount, isIncome, description);

        Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_transaction);

        setRadioColor();

        backBtn = findViewById(R.id.backBtn);
        amountEdt = findViewById(R.id.amountEdt);
        typeRg = findViewById(R.id.typeRg);
        categorySpinner = findViewById(R.id.categorySpinner);
        addTransactionBtn = findViewById(R.id.addTransactionBtn);
        descriptionEdt = findViewById(R.id.descriptionEdt);

        transactionController = new TransactionController(this);

        CategoryHandler categoryHandler = new CategoryHandler(this);
        categoryHandler.insertInitialCategories();

        populateCategories(false);

        typeRg.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isIncome = checkedId == R.id.incomeRb;
            populateCategories(isIncome);
        });

        backBtn.setOnClickListener(v -> finish());

        addTransactionBtn.setOnClickListener(v -> addTransaction());
    }
}
