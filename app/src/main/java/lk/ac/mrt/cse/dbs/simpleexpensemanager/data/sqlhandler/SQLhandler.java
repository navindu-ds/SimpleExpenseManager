package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlhandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlhandler.SQLkeywords;

public class SQLhandler extends SQLiteOpenHelper {
    private static SQLhandler instance;

    private SQLhandler(Context context) {
        super(context, "200110P", null, 1);
    }

    public static SQLhandler getInstance(Context context) {
        if (instance == null) {
            instance = new SQLhandler(context);
        }
        return instance;
    }

    public static SQLhandler getInstance() {
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + SQLkeywords.TABLE_ACC + "(" +
                SQLkeywords.ACC_NO + " VARCHAR(50) PRIMARY KEY," +
                SQLkeywords.BANK + " VARCHAR(50) NOT NULL," +
                SQLkeywords.ACC_NAME + " VARCHAR(50) NOT NULL, " +
                SQLkeywords.BALANCE + " DOUBLE(50) NOT NULL " +
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + SQLkeywords.TABLE_TRANSC + "(" +
                SQLkeywords.T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SQLkeywords.DATE + " DATE NOT NULL," +
                SQLkeywords.ACC_NO + " VARCHAR(50) NOT NULL, " +
                SQLkeywords.EXP_TYPE + " VARCHAR(20) NOT NULL," +
                SQLkeywords.AMOUNT + " DOUBLE(50) NOT NULL " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SQLkeywords.TABLE_ACC);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SQLkeywords.TABLE_TRANSC);

        onCreate(sqLiteDatabase);
    }
}
