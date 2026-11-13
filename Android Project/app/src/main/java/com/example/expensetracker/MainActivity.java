package com.example.expensetracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvBalance;
    private TextView tvIncome;
    private TextView tvExpenses;
    private TextView tvNoTransactions;
    private LinearLayout layoutTransactionsList;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBalance = findViewById(R.id.tvBalance);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpenses = findViewById(R.id.tvExpenses);
        tvNoTransactions = findViewById(R.id.tvNoTransactions);
        layoutTransactionsList = findViewById(R.id.layoutTransactionsList);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        View menuDashboard = findViewById(R.id.menuDashboard);
        menuDashboard.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        View menuAnalytics = findViewById(R.id.menuAnalytics);
        menuAnalytics.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        View menuCalculator = findViewById(R.id.menuCalculator);
        menuCalculator.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
            startActivity(intent);
        });

        View menuSettings = findViewById(R.id.menuSettings);
        menuSettings.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        LinearLayout cardAnalyticsPreview = findViewById(R.id.cardAnalyticsPreview);
        cardAnalyticsPreview.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        Button btnAddIncome = findViewById(R.id.btnAddIncome);
        btnAddIncome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddIncomeActivity.class);
            startActivity(intent);
        });

        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        double totalIncome = databaseHelper.getTotalIncome();
        double totalExpense = databaseHelper.getTotalExpense();
        double balance = totalIncome - totalExpense;

        tvIncome.setText(String.format(Locale.getDefault(), "₹%.2f", totalIncome));
        tvExpenses.setText(String.format(Locale.getDefault(), "₹%.2f", totalExpense));
        tvBalance.setText(String.format(Locale.getDefault(), "₹%.2f", balance));

        List<Transaction> transactionList = databaseHelper.getAllTransactions();

        if (transactionList.isEmpty()) {
            tvNoTransactions.setVisibility(View.VISIBLE);
            layoutTransactionsList.removeAllViews();
            layoutTransactionsList.setVisibility(View.GONE);
        } else {
            tvNoTransactions.setVisibility(View.GONE);
            layoutTransactionsList.removeAllViews();
            layoutTransactionsList.setVisibility(View.VISIBLE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

            for (Transaction transaction : transactionList) {
                View itemView = createTransactionItemView(transaction, dateFormat);
                layoutTransactionsList.addView(itemView);
            }
        }
    }

    private View createTransactionItemView(Transaction transaction, SimpleDateFormat dateFormat) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_modern);
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);

        int padPx = Math.round(getResources().getDisplayMetrics().density * 20);
        card.setPadding(padPx, padPx, padPx, padPx);

        // Extract Category and Display Title
        String rawTitle = transaction.getTitle() != null ? transaction.getTitle() : "";
        String category = "General";
        String displayTitle = rawTitle;

        if (rawTitle.contains(" - ")) {
            String[] parts = rawTitle.split(" - ", 2);
            category = parts[0].trim();
            displayTitle = parts[1].trim();
        } else if (Transaction.TYPE_INCOME.equals(transaction.getType())) {
            category = "Income";
        }

        // Header row: Title + Pill Badge on left, Amount & Delete Icon on right
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titleAndPill = new LinearLayout(this);
        titleAndPill.setOrientation(LinearLayout.HORIZONTAL);
        titleAndPill.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titlePillParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titleAndPill.setLayoutParams(titlePillParams);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(displayTitle);
        tvTitle.setTextSize(15);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        titleAndPill.addView(tvTitle);

        // Category Pill Badge
        TextView tvCategoryPill = new TextView(this);
        tvCategoryPill.setText(category);
        tvCategoryPill.setTextSize(11);
        tvCategoryPill.setTypeface(null, Typeface.BOLD);
        tvCategoryPill.setBackgroundResource(R.drawable.bg_pill_badge);

        if ("Food".equalsIgnoreCase(category) || "Shopping".equalsIgnoreCase(category)) {
            tvCategoryPill.setTextColor(ContextCompat.getColor(this, R.color.badge_orange_text));
        } else if ("Income".equalsIgnoreCase(category) || Transaction.TYPE_INCOME.equals(transaction.getType())) {
            tvCategoryPill.setTextColor(ContextCompat.getColor(this, R.color.badge_teal_text));
        } else {
            tvCategoryPill.setTextColor(ContextCompat.getColor(this, R.color.badge_blue_text));
        }

        LinearLayout.LayoutParams pillParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pillParams.setMargins(12, 0, 0, 0);
        tvCategoryPill.setLayoutParams(pillParams);
        titleAndPill.addView(tvCategoryPill);

        headerRow.addView(titleAndPill);

        TextView tvAmount = new TextView(this);
        boolean isIncome = Transaction.TYPE_INCOME.equals(transaction.getType());
        String prefix = isIncome ? "+₹" : "-₹";
        tvAmount.setText(String.format(Locale.getDefault(), "%s%.2f", prefix, transaction.getAmount()));
        tvAmount.setTextSize(15);
        tvAmount.setTypeface(null, Typeface.BOLD);
        int amountColorRes = isIncome ? R.color.income_green : R.color.expense_red;
        tvAmount.setTextColor(ContextCompat.getColor(this, amountColorRes));
        headerRow.addView(tvAmount);

        // Delete Button Icon (Compact & Stylish)
        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setBackgroundResource(R.drawable.bg_icon_button);
        int deleteSize = Math.round(getResources().getDisplayMetrics().density * 32);
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(deleteSize, deleteSize);
        deleteParams.setMargins(10, 0, 0, 0);
        btnDelete.setLayoutParams(deleteParams);
        btnDelete.setPadding(12, 12, 12, 12);
        btnDelete.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        btnDelete.setContentDescription(getString(R.string.desc_delete_transaction));
        btnDelete.setColorFilter(ContextCompat.getColor(this, R.color.text_muted));
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(transaction));
        headerRow.addView(btnDelete);

        card.addView(headerRow);

        // Subtitle row: Date
        TextView tvDate = new TextView(this);
        tvDate.setText(dateFormat.format(new Date(transaction.getTimestamp())));
        tvDate.setTextSize(12);
        tvDate.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tvDate.setPadding(0, 6, 0, 0);
        card.addView(tvDate);

        // Optional Note
        if (!TextUtils.isEmpty(transaction.getNote())) {
            TextView tvNote = new TextView(this);
            tvNote.setText(transaction.getNote());
            tvNote.setTextSize(12);
            tvNote.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            tvNote.setPadding(0, 4, 0, 0);
            card.addView(tvNote);
        }

        // Long click to delete as well
        card.setOnLongClickListener(v -> {
            showDeleteConfirmationDialog(transaction);
            return true;
        });

        return card;
    }

    private void showDeleteConfirmationDialog(Transaction transaction) {
        String detailText = transaction.getTitle() + " (₹" + String.format(Locale.getDefault(), "%.2f", transaction.getAmount()) + ")";
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message) + "\n\n" + detailText)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    boolean isDeleted = databaseHelper.deleteTransaction(transaction.getId());
                    if (isDeleted) {
                        Toast.makeText(MainActivity.this, R.string.toast_transaction_deleted, Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}