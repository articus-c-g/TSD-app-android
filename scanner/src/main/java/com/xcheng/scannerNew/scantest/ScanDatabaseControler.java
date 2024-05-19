package com.xcheng.scannerNew.scantest;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ScanDatabaseControler {

    private ScanDatabaseHelper helper;

    public ScanDatabaseControler(Context context) {
        helper = new ScanDatabaseHelper(context);
    }

    public void insert(ScanData scandata) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("symbology", scandata.getSymbology());
        values.put("barcode", scandata.getBarcode());

        int id = (int)db.insert("scandata", null, values);

        scandata.setId(id);
        db.close();
    }

    public int delete(int id) {

        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete("scandata", "_id=?", new String[] { id + "" });

        db.close();
        return count;

    }

    public int deleteAll() {

        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete("scandata", null, null);

        db.close();
        return count;

    }

    public int update(ScanData scandata) {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("symbology", scandata.getSymbology());
        values.put("barcode", scandata.getBarcode());

        int count = db.update("scandata", values, "_id=?",
                new String[] { scandata.getId() + "" });

        db.close();

        return count;

    }

    public List<ScanData> queryAll() {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("scandata", null, null, null, null, null,
                "_id ASC");
        List<ScanData> list = new ArrayList<ScanData>();

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex("_id"));
            String symbology = c.getString(1);
            String barcode = c.getString(2);
            list.add(new ScanData(id, symbology, barcode));
        }

        c.close();
        db.close();
        return list;

    }
}
