package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRANSACTIONS = "transactions";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_NOTE + " TEXT, "
                + COLUMN_TIMESTAMP + " INTEGER"
                + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public boolean addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_TITLE, transaction.getTitle());
        values.put(COLUMN_NOTE, transaction.getNote());
        values.put(COLUMN_TIMESTAMP, transaction.getTimestamp());

        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return result != -1;
    }

    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactionList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                transaction.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
                transaction.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                transaction.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)));
                transaction.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));

                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactionList;
    }

    public double getTotalIncome() {
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS
                + " WHERE " + COLUMN_TYPE + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{Transaction.TYPE_INCOME});

        double total = (cursor.moveToFirst()) ? cursor.getDouble(0) : 0.0;

        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpense() {
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS
                + " WHERE " + COLUMN_TYPE + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{Transaction.TYPE_EXPENSE});

        double total = (cursor.moveToFirst()) ? cursor.getDouble(0) : 0.0;

        cursor.close();
        db.close();
        return total;
    }

    public void clearAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, null, null);
        db.close();
    }
}