package com.xcheng.scannerNew;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class ImportExportPublicActivity extends Activity implements View.OnClickListener{

    private final static String TAG = "ImportExportPublicActivity";

    private Button exportButton = null;
    private Button importButton = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_settings_public);
        initView();
    }

    private void initView(){
        exportButton = (Button) findViewById(R.id.button_export);
        importButton = (Button) findViewById(R.id.button_import);
        exportButton.setOnClickListener(this);
        importButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_export){
            ScanUtil.writeFileDataTo(ScanUtil.getSdCardPath() + ScanUtil.XML_FILE_NAME,
                    "/data/data/" + getPackageName() + "/shared_prefs/" + ScanUtil.XML_FILE_NAME);
        }else if (id == R.id.button_import){
            ScanUtil.xmlPullParser(this);
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
