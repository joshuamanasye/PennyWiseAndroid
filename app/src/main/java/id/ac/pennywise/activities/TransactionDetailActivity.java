package id.ac.pennywise.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import id.ac.pennywise.R;
import id.ac.pennywise.controllers.TransactionController;
import id.ac.pennywise.models.TransactionModel;

public class TransactionDetailActivity extends AppCompatActivity {

    private TextView categoryTxt, amountTxt, dateTxt, descriptionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        categoryTxt = findViewById(R.id.categoryTxt);
        amountTxt = findViewById(R.id.amountTxt);
        dateTxt = findViewById(R.id.dateTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);

        String transactionId = getIntent().getStringExtra("transaction_id");

        if (transactionId != null && !transactionId.isBlank()) {
            TransactionController controller = new TransactionController(this);
            TransactionModel transaction = controller.getTransactionById(transactionId);

            if (transaction != null) {
                categoryTxt.setText(transaction.getCategory().getName());
                amountTxt.setText(String.valueOf(transaction.getAmount()));
                dateTxt.setText(String.format("Date: %s", transaction.getDate().toString()));
                descriptionTxt.setText(transaction.getDescription());
            }
        }
    }
}
