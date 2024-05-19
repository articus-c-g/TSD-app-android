package com.xcheng.scannerNew;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;

public class AllMoudlesActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "MoudleSettingFragment";

    public static final int MSG_CHECK_TYPE = 0x01;
    public static final int MSG_UPDATE_STATE = 0x02;
    public static final int MSG_ENABLE_TYPE = 0x03;
    public static final int MSG_DISABLE_TYPE = 0x04;

    private ScanThreadHandler mScanTheadHandler;
    private HandlerThread mHandlerThread;

    private static final int MAXNUM = ScanUtil.MAX_TYPE;

    private final ArrayList<SwitchPreference> mSPList = new ArrayList<>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what) {
                case MSG_UPDATE_STATE:
                    updateState(msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.base_setting);

        mHandlerThread = new HandlerThread("ScanSettingThread");
        mHandlerThread.start();
        mScanTheadHandler = new ScanThreadHandler(mHandlerThread.getLooper());

        createPreference();
    }

    private void createPreference() {
        if (mSPList.size() == 0) {
            for (int i=1; i<MAXNUM; i++) {
                SwitchPreference preference = new SwitchPreference(this);
                preference.setOnPreferenceChangeListener(this);
                preference.setKey(ScanUtil.SP_KEYS[i]);
                preference.setTitle(ScanUtil.SP_TITLE_RES[i]);
                mSPList.add(preference);
                getPreferenceScreen().addPreference(preference);
            }
        }
        Log.d(TAG, "size = " + mSPList.size());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        boolean checked = (Boolean) objValue;
        Log.d(TAG, "onPreferenceChange: checked = " + checked);
        final Message msg = Message.obtain();
        msg.obj = mSPList.indexOf((SwitchPreference)preference) + 1;
        Log.d(TAG, "onPreferenceChange:  msg.obj = " + msg.obj);
        if (checked) {
            msg.what = MSG_ENABLE_TYPE;
        } else {
            msg.what = MSG_DISABLE_TYPE;
        }
        mScanTheadHandler.sendMessage(msg);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateState();
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
        if (mSPList.size() > 0) {
            mSPList.clear();
        }
    }

    private void updateState() {
        for (SwitchPreference sp : mSPList) {
            final Message msg = Message.obtain();
            msg.what = MSG_CHECK_TYPE;
            msg.obj = mSPList.indexOf(sp) + 1;
            Log.d(TAG, "msg.obj = " + msg.obj);
            mScanTheadHandler.sendMessage(msg);
        }
    }

    private void updateState(int type, int enable) {
        mSPList.get(type - 1).setChecked(enable == 1);
    }

    class ScanThreadHandler extends Handler {
        public ScanThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case MSG_CHECK_TYPE:
                    int checktype = (int)msg.obj;
                    boolean enabled = ScanNative.checkType(checktype);
                    Log.d(TAG, "enabled: " + enabled);
                    final Message checkMsg = Message.obtain();
                    checkMsg.what = MSG_UPDATE_STATE;
                    checkMsg.arg1 = checktype;
                    checkMsg.arg2 = enabled ? 1 : 0;
                    mHandler.sendMessage(checkMsg);
                    break;
                case MSG_ENABLE_TYPE:
                    int entype = (int)msg.obj;
                    if (!ScanNative.enableType(entype)) {
                        final Message enMsg = Message.obtain();
                        enMsg.what = MSG_ENABLE_TYPE;
                        enMsg.obj = entype;
                        mScanTheadHandler.sendMessage(enMsg);
                    }
                    break;
                case MSG_DISABLE_TYPE:
                    int distype = (int)msg.obj;
                    if (!ScanNative.disableType(distype)) {
                        final Message disMsg = Message.obtain();
                        disMsg.what = MSG_DISABLE_TYPE;
                        disMsg.obj = distype;
                        mScanTheadHandler.sendMessage(disMsg);
                    }
                    break;
            }
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


