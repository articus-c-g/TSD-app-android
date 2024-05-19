package com.xcheng.scannerNew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class ImportExportActivity extends Activity implements View.OnClickListener{

    private final static String TAG = "ImportExportActivity";

    private Button exportButton = null;
    private Button importButton = null;
    private Button defaultButton = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_settings);
        initView();
    }

    private void initView(){
        exportButton = (Button) findViewById(R.id.button_export);
        importButton = (Button) findViewById(R.id.button_import);
        defaultButton = (Button) findViewById(R.id.button_default);
        exportButton.setOnClickListener(this);
        importButton.setOnClickListener(this);
        defaultButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(ImportExportActivity.this, MainActivity.class);
        int id = view.getId();
        if (id == R.id.button_export) {
            ScanUtil.writeFileDataTo(ScanUtil.getSdCardPath() + ScanUtil.XML_FILE_NAME,
                    "/data/data/" + getPackageName() + "/shared_prefs/" + ScanUtil.XML_FILE_NAME);
        } else if (id == R.id.button_import){
            ScanUtil.xmlPullParser(this);
            startActivity(intent);
            finish();
        } else if (id == R.id.button_default){
            ScanUtil.clear(this);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
