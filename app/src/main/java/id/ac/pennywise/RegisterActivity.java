package id.ac.pennywise;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView registerLink = findViewById(R.id.loginTxt);

        String fullText = "Already an account? Login";
        SpannableString spannableString = new SpannableString(fullText);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.green_teal, getTheme()));
        spannableString.setSpan(colorSpan, fullText.indexOf("Login"), fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerLink.setText(spannableString);

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);

            finish();
        });
    }
}