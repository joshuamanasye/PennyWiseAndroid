package id.ac.pennywise.activities;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import id.ac.pennywise.R;
import id.ac.pennywise.controllers.TransactionController;
import id.ac.pennywise.models.TransactionModel;

public class TransactionDetailActivity extends AppCompatActivity {

    private EditText amountEdt, descriptionEdt;
    private Spinner categorySpinner;
    private TextView dateTxt;
    private RadioGroup typeRg;
    private RadioButton expenseRb, incomeRb;
    private Button editBtn, deleteBtn;

    private TransactionController controller;
    private TransactionModel transaction;
    private String transactionId;

    private void setRadioColor() {
        RadioButton expenseRb = findViewById(R.id.expenseRb);
        RadioButton incomeRb = findViewById(R.id.incomeRb);

        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.green_teal);
        expenseRb.setButtonTintList(colorStateList);
        incomeRb.setButtonTintList(colorStateList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        setRadioColor();

        amountEdt = findViewById(R.id.amountEdt);
        categorySpinner = findViewById(R.id.categorySpinner);
        dateTxt = findViewById(R.id.dateTxt);
        descriptionEdt = findViewById(R.id.descriptionEdt);
        typeRg = findViewById(R.id.typeRg);
        expenseRb = findViewById(R.id.expenseRb);
        incomeRb = findViewById(R.id.incomeRb);
        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        controller = new TransactionController(this);
        transactionId = getIntent().getStringExtra("transaction_id");

        if (transactionId != null && !transactionId.isBlank()) {
            transaction = controller.getTransactionById(transactionId);

            if (transaction != null) {
                populateFields();
            }
        }

        dateTxt.setOnClickListener(v -> showDatePicker());

        typeRg.setOnCheckedChangeListener((group, checkedId) -> updateCategorySpinner());

        editBtn.setOnClickListener(v -> updateTransaction());
        deleteBtn.setOnClickListener(v -> deleteTransaction());
    }

    private void populateFields() {
        amountEdt.setText(String.valueOf(transaction.getAmount()));
        dateTxt.setText(transaction.getDate().toString());
        descriptionEdt.setText(transaction.getDescription());

        if (transaction.getCategory().isIncome()) {
            incomeRb.setChecked(true);
        } else {
            expenseRb.setChecked(true);
        }

        updateCategorySpinner();
        categorySpinner.setSelection(controller.getCategoryPosition(transaction.getCategory().getName(), incomeRb.isChecked()));
    }

    private void showDatePicker() {
        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(dateTxt.getText(), DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                R.style.CustomDatePickerDialogTheme,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = String.format(getString(R.string.d_02d_02d), year, month + 1, dayOfMonth);
                    dateTxt.setText(dateStr);
                },
                selectedDate.getYear(),
                selectedDate.getMonthValue() - 1,
                selectedDate.getDayOfMonth()
        );

        datePicker.show();
    }

    private void updateCategorySpinner() {
        List<String> categories = controller.getCategoriesByType(incomeRb.isChecked());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void updateTransaction() {
        String amountStr = amountEdt.getText().toString().trim();
        String description = descriptionEdt.getText().toString().trim();
        String dateStr = dateTxt.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        boolean isIncome = incomeRb.isChecked();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
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

        boolean oldIsIncome = transaction.getCategory().isIncome();
        double oldAmount = transaction.getAmount();

        transaction.getCategory().setIncome(isIncome);
        transaction.getCategory().setName(category);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(date);

        if (controller.updateTransaction(transaction, this)) {
            if (!oldIsIncome) { oldAmount *= -1; }
            controller.addBalance(this, -oldAmount);

            if (!transaction.getCategory().isIncome()) { amount *= -1; }
            controller.addBalance(this, amount);

            Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTransaction() {
        if (transaction != null && transaction.getId() != null) {
            boolean isIncome = transaction.getCategory().isIncome();
            double amount = transaction.getAmount();

            boolean isDeleted = controller.deleteTransaction(transaction.getId(), this);
            if (isDeleted) {
                Toast.makeText(this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();

                if (!isIncome) { amount *= -1; }
                controller.addBalance(this, -amount);

                finish();
            } else {
                Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show();
        }
    }

}
