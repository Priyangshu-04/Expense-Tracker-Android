package com.example.expensetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddIncomeActivity extends AppCompatActivity {

    private EditText etIncomeAmount;
    private EditText etIncomeSource;
    private EditText etIncomeNote;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_income);

        databaseHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etIncomeAmount = findViewById(R.id.etIncomeAmount);
        etIncomeSource = findViewById(R.id.etIncomeSource);
        etIncomeNote = findViewById(R.id.etIncomeNote);
        Button btnSaveIncome = findViewById(R.id.btnSaveIncome);

        btnSaveIncome.setOnClickListener(v -> saveIncome());
    }

    private void saveIncome() {
        String amountStr = etIncomeAmount.getText().toString().trim();
        String sourceStr = etIncomeSource.getText().toString().trim();
        String noteStr = etIncomeNote.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            etIncomeAmount.setError("Please enter amount");
            return;
        }

        if (TextUtils.isEmpty(sourceStr)) {
            etIncomeSource.setError("Please enter income source");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etIncomeAmount.setError("Invalid amount format");
            return;
        }

        Transaction transaction = new Transaction(
                Transaction.TYPE_INCOME,
                amount,
                sourceStr,
                noteStr,
                System.currentTimeMillis()
        );

        boolean isSaved = databaseHelper.addTransaction(transaction);
        if (isSaved) {
            Toast.makeText(this, "Income saved successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to save income", Toast.LENGTH_SHORT).show();
        }
    }
}