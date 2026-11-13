package com.example.expensetracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class PieChartView extends View {

    private Paint incomePaint;
    private Paint expensePaint;
    private Paint centerPaint;
    private Paint textPaint;

    private RectF rectF;
    private double income = 0;
    private double expense = 0;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        incomePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        incomePaint.setStyle(Paint.Style.FILL);

        expensePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        expensePaint.setStyle(Paint.Style.FILL);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();
    }

    public void setData(double income, double expense) {
        this.income = Math.max(0, income);
        this.expense = Math.max(0, expense);
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        double total = income + expense;
        if (total <= 0) {
            return;
        }

        incomePaint.setColor(ContextCompat.getColor(getContext(), R.color.income_green));
        expensePaint.setColor(ContextCompat.getColor(getContext(), R.color.expense_red));
        centerPaint.setColor(ContextCompat.getColor(getContext(), R.color.bg_card));
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));

        int width = getWidth();
        int height = getHeight();
        float diameter = Math.min(width, height) * 0.75f;

        float left = (width - diameter) / 2f;
        float top = (height - diameter) / 2f;
        rectF.set(left, top, left + diameter, top + diameter);

        float incomeAngle = (float) ((income / total) * 360f);
        float expenseAngle = (float) ((expense / total) * 360f);

        // Draw Income Arc
        canvas.drawArc(rectF, -90f, incomeAngle, true, incomePaint);

        // Draw Expense Arc
        canvas.drawArc(rectF, -90f + incomeAngle, expenseAngle, true, expensePaint);

        // Draw Donut Hole
        float holeRadius = diameter * 0.32f;
        canvas.drawCircle(width / 2f, height / 2f, holeRadius, centerPaint);

        // Draw Center Text
        textPaint.setTextSize(diameter * 0.12f);
        int expensePercent = (int) Math.round((expense / total) * 100);
        String centerText = expensePercent + "% Spent";
        canvas.drawText(centerText, width / 2f, (height / 2f) + (textPaint.getTextSize() / 3f), textPaint);
    }
}