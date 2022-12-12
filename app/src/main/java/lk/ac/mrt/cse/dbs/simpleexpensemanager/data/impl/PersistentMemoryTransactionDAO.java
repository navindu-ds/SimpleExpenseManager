package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlhandler.SQLhandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlhandler.SQLkeywords;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private final SQLhandler dbhandler;

    public PersistentMemoryTransactionDAO(Context context) {
        dbhandler = SQLhandler.getInstance(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbhandler.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put(SQLkeywords.DATE, new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date));
        content.put(SQLkeywords.ACC_NO, accountNo);
        content.put(SQLkeywords.EXP_TYPE, String.valueOf(expenseType));
        content.put(SQLkeywords.AMOUNT, amount);

        db.insert(SQLkeywords.TABLE_TRANSC, null, content);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = dbhandler.getReadableDatabase();

         Cursor cursor = db.query(SQLkeywords.TABLE_TRANSC,
                null,null,null, null, null, null);

        while(cursor.moveToNext()) {
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.DATE));
            Date date = null;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.ACC_NO));
            ExpenseType expType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.EXP_TYPE)));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLkeywords.AMOUNT));

            transactionList.add(new Transaction(date, accountNo,expType, amount));
        }
        cursor.close();
        return transactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = dbhandler.getReadableDatabase();
        List<Transaction> limitedTransactionList = new ArrayList<>();

        Cursor cursor = db.query(SQLkeywords.TABLE_TRANSC,
                null, null, null, null, null, null, limit + "");

        while(cursor.moveToNext()) {
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.DATE));
            Date date = null;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.ACC_NO));
            ExpenseType expType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.EXP_TYPE)));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLkeywords.AMOUNT));

            limitedTransactionList.add(new Transaction(date, accountNo,expType, amount));
        }
        cursor.close();
        return limitedTransactionList;
    }
}
