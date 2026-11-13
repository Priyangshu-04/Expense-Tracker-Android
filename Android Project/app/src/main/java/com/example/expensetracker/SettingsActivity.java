package com.example.expensetracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SwitchCompat switchDarkMode = findViewById(R.id.switchDarkMode);
        Button btnClearData = findViewById(R.id.btnClearData);

        // Set initial dark mode state
        switchDarkMode.setChecked(ThemeManager.isDarkMode(this));

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                ThemeManager.setDarkMode(SettingsActivity.this, isChecked));

        btnClearData.setOnClickListener(v -> showClearDataConfirmationDialog());
    }

    private void showClearDataConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_clear_title)
                .setMessage(R.string.dialog_clear_message)
                .setPositiveButton(R.string.btn_clear_data, (dialog, which) -> {
                    databaseHelper.clearAllTransactions();
                    Toast.makeText(SettingsActivity.this, "All transactions cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}