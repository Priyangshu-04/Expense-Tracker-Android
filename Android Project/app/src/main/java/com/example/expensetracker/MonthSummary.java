package com.example.expensetracker;

public class MonthSummary {

    private final String monthName;
    private final int transactionCount;
    private final double totalIncome;
    private final double totalExpense;

    public MonthSummary(String monthName, int transactionCount, double totalIncome, double totalExpense) {
        this.monthName = monthName;
        this.transactionCount = transactionCount;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public String getMonthName() {
        return monthName;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getNetBalance() {
        return totalIncome - totalExpense;
    }
}