package com.example.expensetracker;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView tvSelectedYear;
    private TextView tvYearlyIncome;
    private TextView tvYearlyExpense;
    private TextView tvYearlyNet;
    private TextView tvNoDataYear;
    private LinearLayout layoutMonthlyBreakdown;

    private DatabaseHelper databaseHelper;
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analytics);

        databaseHelper = new DatabaseHelper(this);
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvSelectedYear = findViewById(R.id.tvSelectedYear);
        tvYearlyIncome = findViewById(R.id.tvYearlyIncome);
        tvYearlyExpense = findViewById(R.id.tvYearlyExpense);
        tvYearlyNet = findViewById(R.id.tvYearlyNet);
        tvNoDataYear = findViewById(R.id.tvNoDataYear);
        layoutMonthlyBreakdown = findViewById(R.id.layoutMonthlyBreakdown);

        ImageButton btnPrevYear = findViewById(R.id.btnPrevYear);
        ImageButton btnNextYear = findViewById(R.id.btnNextYear);

        btnPrevYear.setOnClickListener(v -> {
            selectedYear--;
            loadAnalyticsData();
        });

        btnNextYear.setOnClickListener(v -> {
            selectedYear++;
            loadAnalyticsData();
        });

        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        tvSelectedYear.setText(String.format(Locale.getDefault(), "%d", selectedYear));

        List<Transaction> allTransactions = databaseHelper.getAllTransactions();
        Calendar cal = Calendar.getInstance();

        // 12 months data
        int[] monthTransactionCounts = new int[12];
        double[] monthIncomes = new double[12];
        double[] monthExpenses = new double[12];

        boolean hasDataForYear = false;

        for (Transaction transaction : allTransactions) {
            cal.setTimeInMillis(transaction.getTimestamp());
            if (cal.get(Calendar.YEAR) == selectedYear) {
                hasDataForYear = true;
                int month = cal.get(Calendar.MONTH); // 0-11
                monthTransactionCounts[month]++;

                String type = transaction.getType();
                if (type != null) {
                    switch (type) {
                        case Transaction.TYPE_INCOME:
                            monthIncomes[month] += transaction.getAmount();
                            break;
                        case Transaction.TYPE_EXPENSE:
                            monthExpenses[month] += transaction.getAmount();
                            break;
                    }
                }
            }
        }

        double yearlyIncome = 0.0;
        double yearlyExpense = 0.0;
        int m = 0;
        while (m < 12) {
            yearlyIncome += monthIncomes[m];
            yearlyExpense += monthExpenses[m];
            m++;
        }

        double yearlyNet = yearlyIncome - yearlyExpense;

        tvYearlyIncome.setText(String.format(Locale.getDefault(), "₹%.2f", yearlyIncome));
        tvYearlyExpense.setText(String.format(Locale.getDefault(), "₹%.2f", yearlyExpense));
        tvYearlyNet.setText(String.format(Locale.getDefault(), "₹%.2f", yearlyNet));

        if (!hasDataForYear) {
            tvNoDataYear.setVisibility(View.VISIBLE);
            layoutMonthlyBreakdown.removeAllViews();
            layoutMonthlyBreakdown.setVisibility(View.GONE);
            return;
        }

        tvNoDataYear.setVisibility(View.GONE);
        layoutMonthlyBreakdown.removeAllViews();
        layoutMonthlyBreakdown.setVisibility(View.VISIBLE);

        String[] monthNames = new DateFormatSymbols(Locale.getDefault()).getMonths();

        List<MonthSummary> monthSummaries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            if (monthTransactionCounts[i] > 0) {
                monthSummaries.add(new MonthSummary(
                        monthNames[i],
                        monthTransactionCounts[i],
                        monthIncomes[i],
                        monthExpenses[i]
                ));
            }
        }

        for (MonthSummary monthSummary : monthSummaries) {
            View monthView = createMonthSummaryView(monthSummary);
            layoutMonthlyBreakdown.addView(monthView);
        }
    }

    private View createMonthSummaryView(MonthSummary summary) {
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

        // Header Row: Month Name + Transaction Count
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvMonthName = new TextView(this);
        tvMonthName.setText(summary.getMonthName());
        tvMonthName.setTextSize(17);
        tvMonthName.setTypeface(null, Typeface.BOLD);
        tvMonthName.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvMonthName.setLayoutParams(titleParams);

        TextView tvCount = new TextView(this);
        String countText = summary.getTransactionCount() + " transaction" + (summary.getTransactionCount() > 1 ? "s" : "");
        tvCount.setText(countText);
        tvCount.setTextSize(12);
        tvCount.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        headerRow.addView(tvMonthName);
        headerRow.addView(tvCount);
        card.addView(headerRow);

        // Stats Row: Income & Expense
        LinearLayout statsRow = new LinearLayout(this);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setPadding(0, 12, 0, 0);

        TextView tvIncome = new TextView(this);
        tvIncome.setText(String.format(Locale.getDefault(), "Income: +₹%.2f", summary.getTotalIncome()));
        tvIncome.setTextSize(13);
        tvIncome.setTypeface(null, Typeface.BOLD);
        tvIncome.setTextColor(ContextCompat.getColor(this, R.color.income_green));
        LinearLayout.LayoutParams incomeParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvIncome.setLayoutParams(incomeParams);

        TextView tvExpense = new TextView(this);
        tvExpense.setText(String.format(Locale.getDefault(), "Spent: -₹%.2f", summary.getTotalExpense()));
        tvExpense.setTextSize(13);
        tvExpense.setTypeface(null, Typeface.BOLD);
        tvExpense.setTextColor(ContextCompat.getColor(this, R.color.expense_red));

        statsRow.addView(tvIncome);
        statsRow.addView(tvExpense);
        card.addView(statsRow);

        // Net Balance Row
        TextView tvNet = new TextView(this);
        double net = summary.getNetBalance();
        tvNet.setText(String.format(Locale.getDefault(), "Net: ₹%.2f", net));
        tvNet.setTextSize(12);
        tvNet.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tvNet.setPadding(0, 6, 0, 0);
        card.addView(tvNet);

        // Tap Hint Text
        TextView tvHint = new TextView(this);
        tvHint.setText(R.string.hint_show_pie);
        tvHint.setTextSize(11);
        tvHint.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
        tvHint.setPadding(0, 12, 0, 0);
        card.addView(tvHint);

        // Hidden Expandable Pie Chart Section
        LinearLayout layoutPieSection = new LinearLayout(this);
        layoutPieSection.setOrientation(LinearLayout.VERTICAL);
        layoutPieSection.setGravity(Gravity.CENTER_HORIZONTAL);
        layoutPieSection.setPadding(0, 16, 0, 0);
        layoutPieSection.setVisibility(View.GONE);

        // Pie Chart View
        PieChartView pieChartView = new PieChartView(this);
        int pieSize = Math.round(getResources().getDisplayMetrics().density * 200);
        LinearLayout.LayoutParams pieParams = new LinearLayout.LayoutParams(pieSize, pieSize);
        pieParams.gravity = Gravity.CENTER_HORIZONTAL;
        pieChartView.setLayoutParams(pieParams);
        pieChartView.setData(summary.getTotalIncome(), summary.getTotalExpense());
        layoutPieSection.addView(pieChartView);

        // Percentage Legend Row
        double total = summary.getTotalIncome() + summary.getTotalExpense();
        int incomePercent = total > 0 ? (int) Math.round((summary.getTotalIncome() / total) * 100) : 0;
        int expensePercent = total > 0 ? (int) Math.round((summary.getTotalExpense() / total) * 100) : 0;

        TextView tvLegend = new TextView(this);
        tvLegend.setText(String.format(Locale.getDefault(), "● Income: %d%%  |  ● Expenses: %d%%", incomePercent, expensePercent));
        tvLegend.setTextSize(13);
        tvLegend.setTypeface(null, Typeface.BOLD);
        tvLegend.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvLegend.setPadding(0, 12, 0, 0);
        tvLegend.setGravity(Gravity.CENTER_HORIZONTAL);
        layoutPieSection.addView(tvLegend);

        card.addView(layoutPieSection);

        // Toggle on Card Click
        card.setOnClickListener(v -> {
            switch (layoutPieSection.getVisibility()) {
                case View.VISIBLE:
                    layoutPieSection.setVisibility(View.GONE);
                    tvHint.setText(R.string.hint_show_pie);
                    break;
                case View.GONE:
                default:
                    layoutPieSection.setVisibility(View.VISIBLE);
                    tvHint.setText(R.string.hint_hide_pie);
                    break;
            }
        });

        return card;
    }
}