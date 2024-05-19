package com.articus.tsd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class ActivityTable extends AppCompatActivity {

    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;
    private CreateXMLFile crXML;
    int permissionStatusRead;
    int permissionStatusWrite;
    public int _item;
    private String [] searchParams = new String[3];

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        permissionStatusRead = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionStatusWrite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        mDBHelper = new DBHelper(this);
        crXML = new CreateXMLFile();

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
        ShowTableSQL ();
    }

    public void ShowTableSQL ()
    {
        try {
            ArrayList<HashMap<String, Object>> listDB = new ArrayList<HashMap<String, Object>>();

            HashMap<String, Object> products;

            boolean empty = true;
            for (int i=0; i<searchParams.length; i++) {
                if (searchParams[i] != null) {
                    empty = false;
                    break;
                }
            }

            Cursor cursor;

            if(empty) {
                cursor = mDb.rawQuery("SELECT * FROM products", null);
            }
            else if(searchParams[2] != null){
                cursor = mDb.rawQuery("SELECT * FROM products WHERE name LIKE " + "'" + "%"+ searchParams[1] +"%" + "'" + " AND code LIKE " + "'" + "%"+ searchParams[0] +"%" + "'" + " AND count > 0", null);
            }
            else{
                cursor = mDb.rawQuery("SELECT * FROM products WHERE name LIKE " + "'" + "%"+ searchParams[1] +"%" + "'" + " AND code LIKE " + "'" + "%"+ searchParams[0] +"%" + "'", null);
            }
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                products = new HashMap<String, Object>();

                products.put("id", cursor.getString(0));
                products.put("code", cursor.getString(1));
                products.put("name", cursor.getString(2));
                products.put("count", cursor.getInt(3));

                listDB.add(products);
                cursor.moveToNext();
            }
            cursor.close();
            String[] from = {"id","code", "name", "count"};
            int[] to = {R.id.textView0, R.id.textView, R.id.textView1, R.id.textView2};
            SimpleAdapter adapter = new SimpleAdapter(this, listDB, R.layout.adapter_item, from, to);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    HashMap<String, Object> obj = (HashMap<String, Object>) adapter.getItem(position);

                    Integer value = (Integer) obj.get("count");
                    String nameProduct = (String) obj.get("name");
                    String idProduct = (String) obj.get("id");
                    String _value = value.toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTable.this);

                    builder.setTitle("Введите количество");
                    builder.setMessage(nameProduct);

                    final EditText input = new EditText(ActivityTable.this);
                    input.setInputType(2);
                    input.setText(_value);
                    builder.setView(input);

                    input.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            /*Integer _id = (int) id;
                            _id++;*/
                            String _value = input.getText().toString();
                            if(_value.equals(""))
                            {
                                _value = "0";
                            }
                            String SQL_CHANGE_COUNT = "UPDATE products SET count = " + _value + " WHERE id = " + idProduct;
                            mDb.execSQL(SQL_CHANGE_COUNT);
                            ShowTableSQL();
                            input.clearFocus();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            input.clearFocus();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            dialog.dismiss();
                        }

                    });
                    builder.show();
                }
            });
        }catch (Throwable t)
        {
            Toast.makeText(this, "Ошибка при инициализации базы данных: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void DeleteTableSQL()
    {
        final char kv = (char) 34;
        String SQL_CLEAR_TABLE = "DELETE FROM products";
        String SQL_DROP_AUTOINC = "UPDATE sqlite_sequence SET seq=0 WHERE Name=" + kv+"products"+kv;
        mDb.execSQL(SQL_DROP_AUTOINC);
        mDb.execSQL(SQL_CLEAR_TABLE);
        ShowTableSQL();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_table, menu);;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_item_createXML:

                LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_dialog_save_xml, null);
                TextView txtName = (TextView) view.findViewById(R.id.txtNameXML);
                CheckBox chkBox = (CheckBox) view.findViewById(R.id.chBoxXML);

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Введите имя документа");
                builder1.setView(view);
                builder1.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        parseToXML(txtName.getText().toString(), chkBox.isChecked());
                    }
                });
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                return true;

            case R.id.clear_table:
                AlertDialog.Builder builder  =  new AlertDialog.Builder(this);
                builder.setMessage("Удалить данные из таблицы?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DeleteTableSQL();
                    }
                }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            case R.id.update_table:
                searchParams[0] = null;
                searchParams[1] = null;
                searchParams[2] = null;
                ShowTableSQL();
                return true;

            case R.id.search:
                LinearLayout searchView = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_dialog_search, null);
                TextView field_Name = (TextView) searchView.findViewById(R.id.search_field_code);
                TextView field_Code = (TextView) searchView.findViewById(R.id.search_field_name);
                CheckBox chkBox_nonNull = (CheckBox) searchView.findViewById(R.id.search_NonNull);

                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle("Поиск");
                builder3.setView(searchView);
                builder3.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        searchParams[0] = field_Name.getText().toString();
                        searchParams[1] = field_Code.getText().toString();
                        if(chkBox_nonNull.isChecked())
                            searchParams[2] = "true";
                        ShowTableSQL();
                    }
                });
                AlertDialog dialog3 = builder3.create();
                dialog3.show();
                return true;

            case R.id.get_xml:
                AlertDialog.Builder builder2  =  new AlertDialog.Builder(this);
                builder2.setMessage("Добавить новые данные или заменить данные?");
                builder2.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (permissionStatusRead == PackageManager.PERMISSION_GRANTED )
                        {
                            //parseFromXML ("storage/emulated/0/Download/data.xml");
                            ChooseFile();
                        }else {
                            ActivityCompat.requestPermissions(ActivityTable.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                        }
                    }
                }).setNegativeButton("Заменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (permissionStatusRead == PackageManager.PERMISSION_GRANTED )
                        {
                            DeleteTableSQL();
                            //parseFromXML ("storage/emulated/0/Download/data.xml");
                            ChooseFile();
                        }else
                            {
                                ActivityCompat.requestPermissions(ActivityTable.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                            }
                    }
                }).setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                    permissionStatusWrite = PackageManager.PERMISSION_GRANTED;
                    permissionStatusRead = PackageManager.PERMISSION_GRANTED;
                    Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    int counterD = 0;
    public void parseToXML(String fileName, Boolean noNull)
    {
        try {
            File file = new File("storage/emulated/0/Download/" + fileName + ".xml");
            boolean deleted = file.delete();
            Cursor cursor;
            String products[] = new String[4];
            if(noNull) {
                cursor = mDb.rawQuery("SELECT * FROM products WHERE count > 0", null);
            }else {
                    cursor = mDb.rawQuery("SELECT * FROM products", null);
                }
            cursor.moveToFirst();
            counterD = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int tmp;
                    TextView statusBar = (TextView) findViewById(R.id.statusBar);
                    //SQLiteDatabase db = mDBHelper.getReadableDatabase();
                    long countRows = 0;
                    if (noNull) {
                        countRows = DatabaseUtils.queryNumEntries(mDb, "products", "count > 0");
                    }else {
                        countRows = DatabaseUtils.queryNumEntries(mDb, "products");
                        }
                    Log.d("Counter", "noNull " + noNull.toString() + " " + countRows);
                    File file = new File("storage/emulated/0/Download/", fileName + ".xml");
                    boolean save = false;
                    while (!cursor.isAfterLast()) {
                        tmp = cursor.getInt(0);
                        products[0] = String.valueOf(tmp);
                        products[1] = cursor.getString(1);
                        products[2] = cursor.getString(2);
                        tmp = cursor.getInt(3);
                        products[3] = String.valueOf(tmp);
                        cursor.moveToNext();

                        if(counterD >= countRows -1)
                            save = true;
                        try {
                            crXML.setAttr(products[0], products[1], products[2], products[3], fileName + ".xml",file,save);
                        } catch (SAXException | ParserConfigurationException | IOException | TransformerException e) {
                            e.printStackTrace();
                        }

                        counterD++;
                        long finalCountRows = countRows;
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                statusBar.setText("Загрузка: " + counterD + "/" + finalCountRows);
                            }
                        });
                    }
                    cursor.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusBar.setText("");
                        }
                    });
                    cursor.close();
                }
            }).start();


        }catch (Throwable t)
        {
            Toast.makeText(this, "Ошибка при создании XML-документа: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void ChooseFile()
    {
        Intent intent = new Intent();
        intent.setType("text/xml");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select XML-file"), 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case 1:
            {
                if (resultCode == RESULT_OK)
                {
                    Uri uri = data.getData();
                    String src = GetRealPath(uri).toString();
                    LoadDialog(src);
                }
                break;
            }
        }
    }

    public File GetRealPath(Uri uri)
    {
        final File file = new File(this.getCacheDir(), ActivityTable.this + Objects.requireNonNull(this.getContentResolver().getType(uri)).split("/")[1]);
        try (final InputStream inputStream = this.getContentResolver().openInputStream(uri); OutputStream output = new FileOutputStream(file)) {
            final byte[] buffer = new byte[4 * 1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
            return file;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

   /* public void ChooseFile(String dirDirectory)
    {
        ArrayList<String> files = new ArrayList<>();
        try {
            File f = new File(dirDirectory);
            File[] list = f.listFiles();

                for (int i = 0; i < list.length; i++) {
                    files.add(list[i].toString());
                }

                if (files.size() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Выберите XML-документ: ")
                            .setSingleChoiceItems(files.toArray(new String[files.size()]), -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    _item = item;
                                }
                            })
                            .setPositiveButton("Далее", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    ChooseFile(files.get(_item));
                                }
                            })
                            .setNegativeButton("Выбрать", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    parseFromXML(files.get(_item));
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(this, "Файлы не найдены", Toast.LENGTH_LONG).show();
                }
            }
            catch(Throwable t)
            {
                Toast.makeText(this, "Произошла ошибка при выборе дкумента: " + t.toString(), Toast.LENGTH_LONG).show();
            }
    }

    public void ChooseDirectory(String dirDirectory)
    {
        ArrayList<String> files = new ArrayList<>();
        try {
            File f = new File(dirDirectory);
            File[] list = f.listFiles();

            for (int i = 0; i < list.length; i++)
            {
                files.add(list[i].toString());
            }

            if (files.size() != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Выберите XML-документ: ")
                        .setSingleChoiceItems(files.toArray(new String[files.size()]), -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                _item = item;
                            }
                        })
                        .setPositiveButton("Далее", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ChooseDirectory(files.get(_item));
                            }
                        })
                        .setNegativeButton("Выбрать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                parseToXML(files.get(_item));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(this, "Файлы не найдены", Toast.LENGTH_LONG).show();
            }
        }
        catch(Throwable t)
        {
            Toast.makeText(this, "Ошибка: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }*/

    int counter = 0;
    public void parseFromXML (String path, Integer count)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Log.d("PARSER: ", "Прасим");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    File file = new File(path);
                    FileInputStream fis = new FileInputStream(file);
                    parser.setInput(new InputStreamReader(fis));
                    TextView statusBar = (TextView) findViewById(R.id.statusBar);
                    ContentValues contentValues = new ContentValues();
                    while (parser.getEventType() != XmlPullParser.END_DOCUMENT)
                    {
                        if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("id"))
                        {
                            parser.next();
                            contentValues.put("id", parser.getText());
                        }
                        if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("code"))
                        {
                            parser.next();
                            contentValues.put("code", getHTMLlegacyText(parser.getText()));
                            Log.d("PARSER:", "code: " + parser.getText());
                        }
                        if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("name"))
                        {
                            parser.next();
                            contentValues.put("name", getHTMLlegacyText(parser.getText()));
                            Log.d("PARSER:", "name: " + parser.getText());
                        }
                        if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("count"))
                        {
                            parser.next();
                            contentValues.put("count", parser.getText());
                            counter++;
                            mDb.insert("products",null,contentValues);
                        }
                        parser.next();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                statusBar.setText("Загрузка: " + counter + "/" + count );

                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            statusBar.setText("");
                            ShowTableSQL();

                        }
                    });
                    Log.d("parseFromXML", "Вот и фсё");
                    statusBar.setText("");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } catch (Throwable t)
                {
                   // Toast.makeText(ActivityTable.this,"Ошибка при загрузке XML-документа: " + t.toString(), Toast.LENGTH_LONG).show();
                    Log.d("BUG!!", "BUG - " + t);
                }
            }
        }).start();
    }

    public String getHTMLlegacyText(String text){
        return Html.fromHtml(text).toString();
    }

    Integer countGoods = 0;
    public void LoadDialog(String src) {

        try {
            countGoods = 0;
            Log.d("COUNTER: ", "Считаем");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            File file = new File(src);

            FileInputStream fis = new FileInputStream(file);
            parser.setInput(new InputStreamReader(fis));

            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("goods")) {
                    countGoods++;
                }
                parser.next();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Загрузить " + countGoods + " записей?")
            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    parseFromXML(src, countGoods);
                }
            })
            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Throwable t) {
            Toast.makeText(this, "Ошибка при подгрузке XML-документа: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

}