package com.example.expensetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etExpenseAmount;
    private Spinner spExpenseCategory;
    private EditText etExpenseDescription;
    private EditText etExpenseNote;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_expense);

        databaseHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etExpenseAmount = findViewById(R.id.etExpenseAmount);
        spExpenseCategory = findViewById(R.id.spExpenseCategory);
        etExpenseDescription = findViewById(R.id.etExpenseDescription);
        etExpenseNote = findViewById(R.id.etExpenseNote);
        Button btnSaveExpense = findViewById(R.id.btnSaveExpense);

        setupCategorySpinner();

        btnSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void setupCategorySpinner() {
        String[] categories = {
                "Food",
                "Transport",
                "Education",
                "Shopping",
                "Entertainment",
                "Bills",
                "Personal",
                "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spExpenseCategory.setAdapter(adapter);
    }

    private void saveExpense() {
        String amountStr = etExpenseAmount.getText().toString().trim();
        String descriptionStr = etExpenseDescription.getText().toString().trim();
        String noteStr = etExpenseNote != null ? etExpenseNote.getText().toString().trim() : "";
        String category = spExpenseCategory.getSelectedItem() != null ?
                spExpenseCategory.getSelectedItem().toString() : "Other";

        if (TextUtils.isEmpty(amountStr)) {
            etExpenseAmount.setError("Please enter amount");
            return;
        }

        if (TextUtils.isEmpty(descriptionStr)) {
            etExpenseDescription.setError("Please enter description");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etExpenseAmount.setError("Invalid amount format");
            return;
        }

        String fullTitle = category + " - " + descriptionStr;

        Transaction transaction = new Transaction(
                Transaction.TYPE_EXPENSE,
                amount,
                fullTitle,
                noteStr,
                System.currentTimeMillis()
        );

        boolean isSaved = databaseHelper.addTransaction(transaction);
        if (isSaved) {
            Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
        }
    }
}