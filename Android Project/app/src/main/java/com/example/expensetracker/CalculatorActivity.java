package com.example.expensetracker;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class CalculatorActivity extends AppCompatActivity {

    private Button btnTabStandard;
    private Button btnTabEmi;

    private LinearLayout layoutStandardCalc;
    private LinearLayout layoutEmiCalc;

    // Standard Calc Views
    private TextView tvCalcExpression;
    private TextView tvCalcResult;
    private final StringBuilder expressionBuilder = new StringBuilder();

    // EMI Calc Views
    private EditText etEmiPrincipal;
    private EditText etEmiRate;
    private EditText etEmiTenure;
    private TextView tvEmiMonthly;
    private TextView tvEmiTotalInterest;
    private TextView tvEmiTotalPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnTabStandard = findViewById(R.id.btnTabStandard);
        btnTabEmi = findViewById(R.id.btnTabEmi);

        layoutStandardCalc = findViewById(R.id.layoutStandardCalc);
        layoutEmiCalc = findViewById(R.id.layoutEmiCalc);

        // Standard Calc Init
        tvCalcExpression = findViewById(R.id.tvCalcExpression);
        tvCalcResult = findViewById(R.id.tvCalcResult);

        setupStandardKeypad();

        // EMI Calc Init
        etEmiPrincipal = findViewById(R.id.etEmiPrincipal);
        etEmiRate = findViewById(R.id.etEmiRate);
        etEmiTenure = findViewById(R.id.etEmiTenure);
        tvEmiMonthly = findViewById(R.id.tvEmiMonthly);
        tvEmiTotalInterest = findViewById(R.id.tvEmiTotalInterest);
        tvEmiTotalPayment = findViewById(R.id.tvEmiTotalPayment);

        Button btnCalculateEmi = findViewById(R.id.btnCalculateEmi);
        btnCalculateEmi.setOnClickListener(v -> calculateEmi());

        btnTabStandard.setOnClickListener(v -> switchTab(true));
        btnTabEmi.setOnClickListener(v -> switchTab(false));
    }

    private void switchTab(boolean showStandard) {
        int accentColor = ContextCompat.getColor(this, R.color.accent_primary);
        int cardColor = ContextCompat.getColor(this, R.color.bg_card);
        int textPrimary = ContextCompat.getColor(this, R.color.text_primary);
        int white = ContextCompat.getColor(this, R.color.white);

        if (showStandard) {
            layoutStandardCalc.setVisibility(View.VISIBLE);
            layoutEmiCalc.setVisibility(View.GONE);

            btnTabStandard.setBackgroundTintList(ColorStateList.valueOf(accentColor));
            btnTabStandard.setTextColor(white);

            btnTabEmi.setBackgroundTintList(ColorStateList.valueOf(cardColor));
            btnTabEmi.setTextColor(textPrimary);
        } else {
            layoutStandardCalc.setVisibility(View.GONE);
            layoutEmiCalc.setVisibility(View.VISIBLE);

            btnTabEmi.setBackgroundTintList(ColorStateList.valueOf(accentColor));
            btnTabEmi.setTextColor(white);

            btnTabStandard.setBackgroundTintList(ColorStateList.valueOf(cardColor));
            btnTabStandard.setTextColor(textPrimary);
        }
    }

    private void setupStandardKeypad() {
        int[] numberIds = {
                R.id.btnCalc0, R.id.btnCalc1, R.id.btnCalc2, R.id.btnCalc3, R.id.btnCalc4,
                R.id.btnCalc5, R.id.btnCalc6, R.id.btnCalc7, R.id.btnCalc8, R.id.btnCalc9,
                R.id.btnCalcDoubleZero, R.id.btnCalcDot
        };

        for (int id : numberIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> appendExpression(btn.getText().toString()));
            }
        }

        int[] opIds = {R.id.btnCalcPlus, R.id.btnCalcMinus, R.id.btnCalcMultiply, R.id.btnCalcDivide, R.id.btnCalcPercent};
        for (int id : opIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> appendExpression(" " + btn.getText().toString() + " "));
            }
        }

        Button btnClear = findViewById(R.id.btnCalcClear);
        btnClear.setOnClickListener(v -> {
            expressionBuilder.setLength(0);
            tvCalcExpression.setText("");
            tvCalcResult.setText("0");
        });

        Button btnDelete = findViewById(R.id.btnCalcDelete);
        btnDelete.setOnClickListener(v -> {
            if (expressionBuilder.length() > 0) {
                expressionBuilder.deleteCharAt(expressionBuilder.length() - 1);
                tvCalcExpression.setText(expressionBuilder.toString());
            }
        });

        Button btnEquals = findViewById(R.id.btnCalcEquals);
        btnEquals.setOnClickListener(v -> evaluateExpression());
    }

    private void appendExpression(String text) {
        expressionBuilder.append(text);
        tvCalcExpression.setText(expressionBuilder.toString());
    }

    private void evaluateExpression() {
        String expr = expressionBuilder.toString().trim();
        if (TextUtils.isEmpty(expr)) return;

        try {
            String[] tokens = expr.split("\\s+");
            if (tokens.length < 3) return;

            double result = Double.parseDouble(tokens[0]);
            for (int i = 1; i < tokens.length - 1; i += 2) {
                String op = tokens[i];
                double nextNum = Double.parseDouble(tokens[i + 1]);

                switch (op) {
                    case "+":
                        result += nextNum;
                        break;
                    case "-":
                        result -= nextNum;
                        break;
                    case "×":
                    case "*":
                        result *= nextNum;
                        break;
                    case "÷":
                    case "/":
                        if (nextNum != 0) result /= nextNum;
                        break;
                    case "%":
                        result = result % nextNum;
                        break;
                }
            }

            tvCalcResult.setText(String.format(Locale.getDefault(), "%.2f", result));
        } catch (Exception e) {
            tvCalcResult.setText("0");
        }
    }

    private void calculateEmi() {
        String principalStr = etEmiPrincipal.getText().toString().trim();
        String rateStr = etEmiRate.getText().toString().trim();
        String tenureStr = etEmiTenure.getText().toString().trim();

        if (TextUtils.isEmpty(principalStr)) {
            etEmiPrincipal.setError("Please enter loan amount");
            return;
        }

        if (TextUtils.isEmpty(rateStr)) {
            etEmiRate.setError("Please enter interest rate");
            return;
        }

        if (TextUtils.isEmpty(tenureStr)) {
            etEmiTenure.setError("Please enter tenure in years");
            return;
        }

        try {
            double principal = Double.parseDouble(principalStr);
            double annualRate = Double.parseDouble(rateStr);
            double tenureYears = Double.parseDouble(tenureStr);

            double monthlyRate = annualRate / (12 * 100);
            double months = tenureYears * 12;

            double emi;
            if (monthlyRate == 0) {
                emi = principal / months;
            } else {
                emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) / (Math.pow(1 + monthlyRate, months) - 1);
            }

            double totalPayment = emi * months;
            double totalInterest = totalPayment - principal;

            tvEmiMonthly.setText(String.format(Locale.getDefault(), "₹%.2f", emi));
            tvEmiTotalInterest.setText(String.format(Locale.getDefault(), "₹%.2f", totalInterest));
            tvEmiTotalPayment.setText(String.format(Locale.getDefault(), "₹%.2f", totalPayment));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }
}