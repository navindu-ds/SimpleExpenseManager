package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlhandler.SQLhandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlhandler.SQLkeywords;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PersistentMemoryAccountDAO implements AccountDAO {
    private final SQLhandler dbhandler;

    public PersistentMemoryAccountDAO() {
        dbhandler = SQLhandler.getInstance();
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNoList = new ArrayList<>();
        SQLiteDatabase db = dbhandler.getReadableDatabase();

        String[] columns = {SQLkeywords.ACC_NO};
        Cursor cursor = db.query(SQLkeywords.TABLE_ACC, columns,
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            accountNoList.add(cursor.getString(0));
        }
        cursor.close();
        return accountNoList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<>();
        SQLiteDatabase db = dbhandler.getReadableDatabase();
        Cursor cursor = db.query(SQLkeywords.TABLE_ACC, null,
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.ACC_NO));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.BANK));
            String accountHolderName = cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.ACC_NAME));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLkeywords.BALANCE));
            accountList.add(new Account(accountNo, bankName, accountHolderName, balance));
        }
        cursor.close();

        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbhandler.getReadableDatabase();
        String selection = SQLkeywords.ACC_NO + " = ?";
        String[] acc_no = {accountNo};

        Cursor cursor = db.query(SQLkeywords.TABLE_ACC, null,
                selection, acc_no, null, null, null);
        if (cursor == null) {
            throw new InvalidAccountException("Invalid Account Number");
        } else {
            cursor.moveToFirst();
            Account account = new Account(accountNo,
                    cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.BANK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(SQLkeywords.ACC_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(SQLkeywords.BALANCE)));
            cursor.close();
            return account;
        }
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbhandler.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(SQLkeywords.ACC_NO, account.getAccountNo());
        content.put(SQLkeywords.BANK, account.getBankName());
        content.put(SQLkeywords.ACC_NAME, account.getAccountHolderName());
        content.put(SQLkeywords.BALANCE, account.getBalance());

        db.insert(SQLkeywords.TABLE_ACC, null, content);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbhandler.getWritableDatabase();
        String[] acc_no = {accountNo};

        String selection = SQLkeywords.ACC_NO + " = ?";
        Cursor cursor = db.query(SQLkeywords.TABLE_ACC, null, selection, acc_no, null, null, null);
        if (cursor == null) {
            throw new InvalidAccountException("Invalid Account Number");
        }
        cursor.close();

        db.delete(SQLkeywords.TABLE_ACC, SQLkeywords.ACC_NO + " = ?", acc_no);

        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbhandler.getWritableDatabase();
        Account account = this.getAccount(accountNo);

        ContentValues content = new ContentValues();
        switch(expenseType) {
            case EXPENSE:
                content.put(SQLkeywords.BALANCE, account.getBalance() - amount);
                break;
            case INCOME:
                content.put(SQLkeywords.BALANCE, account.getBalance() + amount);
                break;
        }
        String[] acc_no = {accountNo};
        db.update(SQLkeywords.TABLE_ACC, content, SQLkeywords.ACC_NO + " = ? ",acc_no );
        db.close();
    }
}
