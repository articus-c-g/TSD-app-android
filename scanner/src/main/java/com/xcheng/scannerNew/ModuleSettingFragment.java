package com.xcheng.scannerNew;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;


public class ModuleSettingFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "ModuleSettingFragment";

    private static final String KEY_ALL_SWITCH = "swtich_all";
    private static final String KEY_ALL_SETTING = "all_setting";

    public static final int MSG_ENABLE_ALL_TYPE = 0x01;
    public static final int MSG_DISABLE_ALL_TYPE = 0x02;

    private SwitchPreference mAllSwitchPref;

    private ScanThreadHandler mScanTheadHandler;
    private HandlerThread mHandlerThread;

    private boolean mEnabled = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.barcode_module_setting);

        mAllSwitchPref = (SwitchPreference) findPreference(KEY_ALL_SWITCH);
        mAllSwitchPref.setOnPreferenceChangeListener(this);

        mHandlerThread = new HandlerThread("ScanSettingThread");
        mHandlerThread.start();
        mScanTheadHandler = new ScanThreadHandler(mHandlerThread.getLooper());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        mEnabled = (Boolean) objValue;
        Log.d(TAG, "onPreferenceChange: mEnabled = " + mEnabled);
        final Message msg = Message.obtain();
        msg.obj = 0;
        if (mEnabled) {
            msg.what = MSG_ENABLE_ALL_TYPE;
        } else {
            msg.what = MSG_DISABLE_ALL_TYPE;
        }
        mScanTheadHandler.sendMessage(msg);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEnabled = prefs.getBoolean(KEY_ALL_SWITCH, false);
        if (mAllSwitchPref != null) {
            mAllSwitchPref.setChecked(mEnabled);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScanTheadHandler != null) {
            mScanTheadHandler.removeCallbacksAndMessages(null);
        }
        mHandlerThread.quit();
    }

    class ScanThreadHandler extends Handler {
        public ScanThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case MSG_ENABLE_ALL_TYPE:
                    int entype = (int)msg.obj;
                    if (!ScanNative.enableType(entype)) {
                        final Message enMsg = Message.obtain();
                        enMsg.what = MSG_ENABLE_ALL_TYPE;
                        enMsg.obj = entype;
                        mScanTheadHandler.sendMessage(enMsg);
                    }
                    break;
                case MSG_DISABLE_ALL_TYPE:
                    int distype = (int)msg.obj;
                    if (!ScanNative.disableType(distype)) {
                        final Message disMsg = Message.obtain();
                        disMsg.what = MSG_DISABLE_ALL_TYPE;
                        disMsg.obj = distype;
                        mScanTheadHandler.sendMessage(disMsg);
                    }
                    break;
            }
        }
    }
}


