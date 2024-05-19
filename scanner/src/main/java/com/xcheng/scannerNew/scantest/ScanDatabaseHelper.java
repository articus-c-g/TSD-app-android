package com.xcheng.scannerNew.scantest;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScanDatabaseHelper extends SQLiteOpenHelper{

    private String TAG = "ScanDatabaseHelper";

    public ScanDatabaseHelper(Context context) {
        super(context, "scantest.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        try {
            db.execSQL("CREATE TABLE scandata(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "symbology TEXT,"
                    + "barcode TEXT)");
        } catch (SQLException ex) {
            Log.e(TAG, "couldn't create table in database");
            throw ex;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
