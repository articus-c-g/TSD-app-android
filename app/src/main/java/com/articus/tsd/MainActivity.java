package com.articus.tsd;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scanBtn;
    Button btnTable;
    Button btnAddGoods;
    private SQLiteDatabase mDb;
    private DBHelper mDBHelper;
    String name;
    public int codeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBHelper = new DBHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);
        btnTable = findViewById(R.id.btnTable);
        btnTable.setOnClickListener(this);
        btnAddGoods = findViewById(R.id.addBtn);
        btnAddGoods.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.scanBtn:
                scanCode();
                break;
            case R.id.btnTable:
                Intent intent = new Intent(this, ActivityTable.class);
                startActivity(intent);
                break;
            case R.id.addBtn:
                HandInput ();
                break;
        }
    }
    public void HandInput ()
    {
        codeEvent = 0;
        AlertDialog.Builder builder  =  new AlertDialog.Builder(this);
        builder.setTitle("Введите штрих-код:");

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(2);
        input.setText("");
        builder.setView(input);

        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String code = input.getText().toString();
                codeEvent = 1;
                DialogResults(code);
            }
        }).setNegativeButton("Отмена", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(2,1,3,"Отключить автоповорот ").setCheckable(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case 1:
                item.setChecked(!item.isChecked());
                if (item.isChecked())
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                }else
                    {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void scanCode()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt("Сканирование...");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        IntentResult result  = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result !=  null)
        {
             if(result.getContents() != null)
             {
                 codeEvent = 0;
                 DialogResults(result.getContents());
             }
                 else
                 {
                     Toast.makeText(this, "Не найдено", Toast.LENGTH_LONG).show();
                 }
        } else
            {
                super.onActivityResult(requestCode, resultCode, data);
            }
    }

    public void DialogResults(String code)
    {
        try {
            Log.d("CODE:", code);
            Integer nameCount = 1;
            name = "неизвестно_" + nameCount.toString();
            boolean isNew = true;

            LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_dialog_main_results, null);
            TextView txtName = (TextView) view.findViewById(R.id.txtNameRes);
            TextView txtBarcode = (TextView) view.findViewById(R.id.BarcodeRes);
            TextView EditCountEdTxt = (EditText) view.findViewById(R.id.EditCountTxtRes);

            Cursor cursor = mDb.rawQuery("SELECT * FROM products", null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String getCode = cursor.getString(cursor.getColumnIndex("code"));
                if (code.equals(getCode)) {
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    isNew = false;
                }
                cursor.moveToNext();
            }
            cursor.moveToFirst();
            if (isNew) {
                while (!cursor.isAfterLast()) {
                    String getName = cursor.getString(cursor.getColumnIndex("name"));
                    if (name.equals(getName)) {
                        nameCount++;
                        name = "неизвестно_" + nameCount.toString();
                        cursor.moveToFirst();
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Результат:");
            txtBarcode.setText(code);
            txtName.setText(name);
            builder.setView(view);

            builder.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    String value = EditCountEdTxt.getText().toString();
                    name = txtName.getText().toString();
                    Comparator(code, value, name);
                    switch (codeEvent)
                    {
                        case 0:
                            scanCode();
                            break;
                        case 1:
                            HandInput();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                }
            }).setNegativeButton("Повторить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (codeEvent)
                    {
                        case 0:
                            scanCode();
                            break;
                        case 1:
                            HandInput();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }catch (Exception e)
        {
            Toast.makeText(this, "Ошибка: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public void Comparator (String RecCode, String value, String name)
    {
        try {
            Log.d("Code: ", RecCode);
            final char kv = (char) 34;
            String SQL_INCREMENT_COUNT = "UPDATE products SET count = count + " + value + " WHERE code = " + kv + RecCode + kv;
            String SQL_CHANGE_NAME = "UPDATE products SET name = " + kv + name + kv + " WHERE code = " + kv + RecCode + kv;
            Cursor cursor = mDb.rawQuery("SELECT * FROM products", null);
            boolean createNew = true;
            while (cursor.moveToNext()) {
                String getCode = cursor.getString(cursor.getColumnIndex("code"));
                if (getCode != null) {
                    if (RecCode != null) {
                        if (getCode.equals(RecCode)) {
                            mDb.execSQL(SQL_INCREMENT_COUNT);
                            mDb.execSQL(SQL_CHANGE_NAME);
                            createNew = false;
                            cursor.close();
                            break;
                        }
                    }
                    else
                    {
                        continue;
                    }
                }else
                    {
                        continue;
                    }
            }
            if (createNew) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("code", RecCode);
                contentValues.put("name", name);
                contentValues.put("count", value);
                mDb.insert("products", null, contentValues);
                //Toast.makeText(this, "Новые данные: да. Код: " + RecCode + " Имя: " + name + " Кол-во: " + value , Toast.LENGTH_LONG).show();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Ошибка: " + e, Toast.LENGTH_LONG).show();
        }
    }
    String codeFromScan = "";
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if ((char)event.getUnicodeChar() != 0 &&  event.getKeyCode() != 66 && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            codeFromScan = codeFromScan +  (char) event.getUnicodeChar();
            codeFromScan.substring(0, codeFromScan.length() - 1);
        }
        if (event.getKeyCode() == 66 && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            DialogResults(codeFromScan);
            codeEvent = 2;
            codeFromScan = "";
        }
        return true;
    }
}