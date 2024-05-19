package com.xcheng.scannerNew;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import java.lang.reflect.Field;
import com.xcheng.scannerNew.codesetting.BaseSetting;

public class MainActivity extends PreferenceActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_SERIAL_NUMBER = "serial_number";

    private SharedPreferences mShare;

    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_activity);
        setSummaryText(KEY_SERIAL_NUMBER, Build.SERIAL);
        mShare = BaseSetting.getSharedPreferences(MainActivity.this);
        editor = mShare.edit();
        boolean mEnableSettingLock = mShare.getBoolean(ScanSettingActivity.ENABLE_SETTING_LOCK,false);
        String mPassword  = mShare.getString(ScanSettingActivity.CHANGE_PASSWORD, "0000");
        if(mEnableSettingLock){
            checkPassword(mPassword);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setSummaryText(String key, String text) {
        if (TextUtils.isEmpty(text)) {
           text = getString(R.string.text_unknown);
         }
         // some preferences may be missing
         if (findPreference(key) != null) {
             findPreference(key).setSummary(text);
         }
    }

    private void removePreferenceFromScreen(String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            getPreferenceScreen().removePreference(pref);
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

    private void checkPassword(final String password){
        final EditText et = new EditText(this);
        AlertDialog alertdialog = new AlertDialog.Builder(this)
        .setTitle(R.string.check_password)
        .setView(et)
        .setPositiveButton(R.string.change_password_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String input = et.getText().toString();
                    try{
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        if (input.equals(password)) {
                            field.set(dialog, true);
                            dialog.dismiss();
                        }else{
                            field.set(dialog, false);
                            Toast.makeText(MainActivity.this, R.string.wrong_password_tips, Toast.LENGTH_SHORT).show();
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
        }).create();
        alertdialog.setCancelable(false);
        alertdialog.setCanceledOnTouchOutside(false);
        alertdialog.show();
    }

}
