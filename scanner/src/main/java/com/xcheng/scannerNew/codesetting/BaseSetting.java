package com.xcheng.scannerNew.codesetting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.text.TextUtils;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;
import com.xcheng.scannerNew.R;
import com.xcheng.scannerNew.ScanNative;

public class BaseSetting extends PreferenceActivity {

    private static final String TAG = "BaseSetting";

    private static final String MAX_LENGTH_TEXT = "MAX_LENGTH";
    private static final String MIN_LENGTH_TEXT = "MIN_LENGTH";
    private static final int SEND_COMMAND = 0x01;

    public static final int TYPE_PREFERENCE = 0;
    public static final int TYPE_SWTICH = 1;
    public static final int TYPE_LIST = 2;

    private EditText mMaxEditText;
    private EditText mMinEditText;
    private String mMaxText;
    private String mMinText;

    private SettingThreadHandler mSettingTheadHandler;
    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.base_setting);
        mHandlerThread = new HandlerThread("BaseSettingThread");
        mHandlerThread.start();
        mSettingTheadHandler = new SettingThreadHandler(mHandlerThread.getLooper());
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMaxEditText != null) {
            outState.putString(MAX_LENGTH_TEXT, mMaxEditText.getText().toString());
        }
        if (mMinEditText != null) {
            outState.putString(MIN_LENGTH_TEXT, mMinEditText.getText().toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(MAX_LENGTH_TEXT)) {
            mMaxText = savedInstanceState.getString(MAX_LENGTH_TEXT);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(MIN_LENGTH_TEXT)) {
            mMinText = savedInstanceState.getString(MIN_LENGTH_TEXT);
        }
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

    public void updateListPreferenceSummary(ListPreference preference, long current) {
        String summary;
        if (current < 0) {
            // Unsupported value
            summary = "";
        } else {
            final CharSequence[] entries = preference.getEntries();
            final CharSequence[] values = preference.getEntryValues();
            if (entries == null || entries.length == 0) {
                summary = "";
            } else {
                int best = 0;
                for (int i = 0; i < values.length; i++) {
                    long index = Long.parseLong(values[i].toString());
                    if (current >= index) {
                        best = i;
                    }
                }
                summary = entries[best].toString();
            }
        }
        preference.setSummary(summary);
    }

    public Preference createPreference(Context context, int type,
            String key, String title, String command) {
        Preference preference = null;
        String data = ScanNative.queryData(Integer.parseInt(command));
        if (TextUtils.isEmpty(data)) {
            data = "0";
        }
        Log.d(TAG, "command = " + command + ", data = " + data);
        switch (type) {
            case TYPE_PREFERENCE:
                preference = new Preference(context);
                int value = getSharedPreferences(this).getInt(key, -1);
                if (value == -1){
                    getSharedPreferences(context).edit().putInt(key,
                        Integer.parseInt(data)).commit();
                }
                //preference.setSummary(ScanNative.queryData(iCommand));
                break;
            case TYPE_SWTICH:
                preference = new SwitchPreference(context);
                //((SwitchPreference) preference).setChecked("1".equals
                //        (ScanNative.queryData(iCommand)));
                int valueBoolean = getSharedPreferences(this).getInt(key, -1);
                if (valueBoolean == -1){
                    getSharedPreferences(context).edit().putInt(key,
                        Integer.parseInt(data)).commit();
                }
                break;
            case TYPE_LIST:
                preference = new ListPreference(context);
                int valueList = getSharedPreferences(this).getInt(key, -1);
                if (valueList == -1){
                    getSharedPreferences(context).edit().putInt(key,
                            Integer.parseInt(data)).commit();
                }
                //int index = ((ListPreference) preference).findIndexOfValue(
                //        ScanNative.queryData(iCommand));
                //((ListPreference) preference).setValueIndex(index);
                //updateListPreferenceSummary(((ListPreference) preference), index);
                break;
            default:
                break;
        }

        preference.setTitle(title);
        getPreferenceScreen().addPreference(preference);
        return preference;
    }

    public void sendCommand(String command) {
        final Message msg = Message.obtain();
        msg.what = SEND_COMMAND;
        msg.obj = command;
        mSettingTheadHandler.sendMessage(msg);
    }

    public void createMaxLengthDialog(Context context, final Preference preference,
            String title, final int max, final int min, final String command) {
        mMaxEditText = new EditText(context);
        mMaxEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mMaxEditText.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        mMaxText = Integer.toString(getSharedPreferences(this).getInt(preference.getKey(), 0));
        if (mMaxText != null) {
            mMaxEditText.setText(mMaxText);
            mMaxEditText.setSelection(mMaxText.length());
        }
        final String oldText = mMaxText;
        mMaxText = null;
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (!TextUtils.isEmpty(mMaxEditText.getText())) {
                            mMaxText = mMaxEditText.getText().toString();
                            int length = Integer.parseInt(Integer.toString(new Integer(mMaxText)));
                            if (length >= min && length <= max) {
                                preference.setSummary(Integer.toString(new Integer(mMaxText)));
                                String newCommand = command + new Integer(mMaxText);
                                Log.d(TAG, "newCommand = " + newCommand);
                                Editor editor = getSharedPreferences(BaseSetting.this).edit();
                                editor.putInt(preference.getKey(), new Integer(mMaxText));
                                editor.commit();
                                sendCommand(newCommand);
                            } else {
                                mMaxText = oldText;
                                Toast.makeText(BaseSetting.this,
                                        R.string.text_out_of_range,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.cancel, null)
                .create();
        alertDialog.setView(mMaxEditText);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mMaxEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alertDialog.show();
    }

    public void createMinLengthDialog(Context context, final Preference preference,
            String title, final int max, final int min, final String command) {
        mMinEditText = new EditText(context);
        mMinEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mMinEditText.setTextDirection(View.TEXT_DIRECTION_LOCALE);
        mMinText = Integer.toString(getSharedPreferences(this).getInt(preference.getKey(), 0));
        if (mMinText != null) {
            mMinEditText.setText(mMinText);
            mMinEditText.setSelection(mMinText.length());
        }
        final String oldText = mMinText;
        mMinText = null;
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (!TextUtils.isEmpty(mMinEditText.getText())) {
                            mMinText = mMinEditText.getText().toString();
                            int length = Integer.parseInt(mMinText);
                            if (length >= min && length <= max) {
                                preference.setSummary(Integer.toString(new Integer(mMinText)));
                                String newCommand = command + new Integer(mMinText);
                                Log.d(TAG, "newCommand = " + newCommand);
                                Editor editor = getSharedPreferences(BaseSetting.this).edit();
                                editor.putInt(preference.getKey(), new Integer(mMinText));
                                editor.commit();
                                sendCommand(newCommand);
                            } else {
                                mMinText = oldText;
                                Toast.makeText(BaseSetting.this,
                                        R.string.text_out_of_range,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.cancel, null)
                .create();
        alertDialog.setView(mMinEditText);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mMinEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alertDialog.show();
    }

    class SettingThreadHandler extends Handler {
        public SettingThreadHandler (Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case SEND_COMMAND:
                    ScanNative.sendCommand((String)msg.obj);
                    break;
            }
        }
    }

    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("com.xcheng.scanner_preferences", Context.MODE_PRIVATE);
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
