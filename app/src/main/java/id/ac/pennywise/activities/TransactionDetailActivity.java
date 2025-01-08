package id.ac.pennywise.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import id.ac.pennywise.R;
import id.ac.pennywise.controllers.TransactionController;
import id.ac.pennywise.models.TransactionModel;

public class TransactionDetailActivity extends AppCompatActivity {

    private EditText amountEdt, descriptionEdt;
    private Spinner categorySpinner;
    private TextView dateTxt;
    private Button editBtn;
    private RadioGroup typeRg;
    private RadioButton expenseRb, incomeRb;

    private TransactionController controller;
    private TransactionModel transaction;
    private String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        // Initialize views
        amountEdt = findViewById(R.id.amountEdt);
        categorySpinner = findViewById(R.id.categorySpinner);
        dateTxt = findViewById(R.id.dateTxt);
        descriptionEdt = findViewById(R.id.descriptionEdt);
        editBtn = findViewById(R.id.editBtn);
        typeRg = findViewById(R.id.typeRg);
        expenseRb = findViewById(R.id.expenseRb);
        incomeRb = findViewById(R.id.incomeRb);

        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        controller = new TransactionController(this);
        transactionId = getIntent().getStringExtra("transaction_id");

        if (transactionId != null && !transactionId.isBlank()) {
            transaction = controller.getTransactionById(transactionId);

            if (transaction != null) {
                // Populate fields with transaction details
                amountEdt.setText(String.valueOf(transaction.getAmount()));
                dateTxt.setText(transaction.getDate().toString());
                descriptionEdt.setText(transaction.getDescription());

                if (transaction.getCategory().isIncome()) {
                    incomeRb.setChecked(true);
                    populateCategorySpinner(getIncomeCategories());
                } else {
                    expenseRb.setChecked(true);
                    populateCategorySpinner(getExpenseCategories());
                }

                categorySpinner.setSelection(getCategoryPosition(transaction.getCategory().getName()));
            }
        }

        // Set a DatePickerDialog for the date field
        dateTxt.setOnClickListener(v -> showDatePicker());

        // Update category list when the transaction type is changed
        typeRg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.incomeRb) {
                populateCategorySpinner(getIncomeCategories());
            } else {
                populateCategorySpinner(getExpenseCategories());
            }
        });

        // Handle edit button click
        editBtn.setOnClickListener(v -> updateTransaction());
    }

    private void showDatePicker() {
        LocalDate currentDate = LocalDate.parse(dateTxt.getText().toString(), DateTimeFormatter.ISO_DATE);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    dateTxt.setText(selectedDate.toString());
                },
                currentDate.getYear(),
                currentDate.getMonthValue() - 1,
                currentDate.getDayOfMonth()
        );

        datePicker.show();
    }

    private void updateTransaction() {
        String amountStr = amountEdt.getText().toString().trim();
        String description = descriptionEdt.getText().toString().trim();
        String dateStr = dateTxt.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        boolean isIncome = incomeRb.isChecked();

        if (amountStr.isEmpty() || description.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the transaction object
        transaction.getCategory().setIncome(isIncome);
        transaction.getCategory().setName(category);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(date);

        // Update in the database TODO
//        controller.updateTransaction(transaction);

        Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void populateCategorySpinner(List<String> categories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private List<String> getIncomeCategories() {
        return Arrays.asList("Salary", "Bonus", "Investments", "Others");
    }

    private List<String> getExpenseCategories() {
        return Arrays.asList("Food", "Transport", "Shopping", "Bills");
    }

    private int getCategoryPosition(String categoryName) {
        List<String> categories = incomeRb.isChecked() ? getIncomeCategories() : getExpenseCategories();
        return categories.indexOf(categoryName);
    }
}
