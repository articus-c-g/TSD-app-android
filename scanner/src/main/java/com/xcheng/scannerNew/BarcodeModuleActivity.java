package com.xcheng.scannerNew;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

public class BarcodeModuleActivity extends Activity implements OnClickListener {

    private static final String TAG = "BarcodeModuleActivity";

    private LinearLayout mTabEnable;
    private LinearLayout mTabSetting;

    private PreferenceFragment mFragEnable;
    private PreferenceFragment mFragSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_setting);
        initViews();
        selectTab(0);
    }

    private void initViews() {
        mTabEnable = (LinearLayout) findViewById(R.id.tab_enable);
        mTabEnable.setOnClickListener(this);
        mTabSetting = (LinearLayout) findViewById(R.id.tab_setting);
        mTabSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tab_enable) {
            selectTab(0);
        } else if (id == R.id.tab_setting){
            selectTab(1);
        }
    }

    private void selectTab(int index) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (mFragEnable == null) {
                    mFragEnable = new ModuleSettingFragment();
                    transaction.add(R.id.id_content, mFragEnable);
                } else {
                    transaction.show(mFragEnable);
                }
                break;
            case 1:
                if (mFragSetting == null) {
                    mFragSetting = new SettingDetailFragment();
                    transaction.add(R.id.id_content, mFragSetting);
                } else {
                    transaction.show(mFragSetting);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mFragEnable != null) {
            transaction.hide(mFragEnable);
        }
        if (mFragSetting != null) {
            transaction.hide(mFragSetting);
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
