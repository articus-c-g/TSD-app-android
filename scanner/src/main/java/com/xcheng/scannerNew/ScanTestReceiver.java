package com.xcheng.scannerNew;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;

import com.xcheng.scannerNew.codesetting.BaseSetting;

public class ScanTestReceiver extends BroadcastReceiver{

    private static final String TAG = "ScanTestReceiver";
    private static final int SCAN_NOTIFICATION_ID = 10010;
    private NotificationManager mNm = null;

    private static final String ACTION_OPEN_SCAN = ScanUtil.ACTION_OPEN_SCAN;
    private static final String ACTION_CLOSE_SCAN = ScanUtil.ACTION_CLOSE_SCAN;
    private static final String SCANKEY = ScanUtil.SCANKEY;

    private static final String ACTION_ENABLE_TYPE = ScanUtil.ACTION_ENABLE_TYPE;
    private static final String ACTION_DISABLE_TYPE = ScanUtil.ACTION_DISABLE_TYPE;
    private static final String SCANTYPE = ScanUtil.SCANTYPE;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        int scanKey = intent.getIntExtra(SCANKEY, -1);
        int scanType = intent.getIntExtra(SCANTYPE, -1);
        mNm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (ACTION_OPEN_SCAN.equals(action) && isScanKeyEnable(context,scanKey)) {
            Intent serviceIntent = new Intent(context, ScanTestService.class);
            context.startService(serviceIntent);
            if (isNotificationEnable(context)){
                sendScanNotification(context);
            }
        } else if (ACTION_CLOSE_SCAN.equals(action)) {
            Log.d(TAG, "onReceive: ********ACTION_CLOSE_SCAN" );
            Intent serviceIntent = new Intent(context, ScanTestService.class);
            context.stopService(serviceIntent);
            if (isNotificationEnable(context)){
                mNm.cancel(SCAN_NOTIFICATION_ID);
            }
        } else if (ACTION_ENABLE_TYPE.equals(action) && ScanUtil.isValidType(scanType)) {
            Log.d(TAG, "onReceive: ACTION_ENABLE_TYPE = " + scanType);
            ScanNative.enableType(scanType);
        } else if (ACTION_DISABLE_TYPE.equals(action) && ScanUtil.isValidType(scanType)) {
            Log.d(TAG, "onReceive: ACTION_DISABLE_TYPE = " + scanType);
            ScanNative.disableType(scanType);
        } else if (ScanUtil.ACTION_CONTROL_SCANKEY.equals(action)) {
            int scanKeyCode = intent.getIntExtra(ScanUtil.EXTRA_SCANKEY_CODE, -1);
            boolean scanKeyEnable = intent.getBooleanExtra(ScanUtil.EXTRA_SCANKEY_STATUS, true);
            SetScanKeyEnable(context, scanKeyCode, scanKeyEnable);
        }
    }

    public void SetScanKeyEnable(Context context, int keyCode, boolean enable) {
        SharedPreferences sharedPreferences = BaseSetting.getSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        String key = "";
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            key = ScanUtil.LEFT_SCAN_KEY;
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS) {
            key = ScanUtil.RIGHT_SCAN_KEY;
        } else if (keyCode == KeyEvent.KEYCODE_F3) {
            key = ScanUtil.FRONT_SCAN_KEY;
        }
        editor.putBoolean(key, enable);
        editor.commit();
    }

    public boolean isScanKeyEnable(Context context, int scankey) {
        boolean leftKeyEnable  =  BaseSetting.getSharedPreferences(context).getBoolean(ScanUtil.LEFT_SCAN_KEY,true);
        boolean rightKeyEnable  =  BaseSetting.getSharedPreferences(context).getBoolean(ScanUtil.RIGHT_SCAN_KEY,true);
        boolean frontKeyEnable  =  BaseSetting.getSharedPreferences(context).getBoolean(ScanUtil.FRONT_SCAN_KEY,true);

        Log.d(TAG, "isScanKeyEnable: scankey = " + scankey);
        Log.d(TAG, "isScanKeyEnable: leftKeyEnable = " + leftKeyEnable);
        Log.d(TAG, "isScanKeyEnable: rightKeyEnable = " + rightKeyEnable);
        Log.d(TAG, "isScanKeyEnable: frontKeyEnable = " + frontKeyEnable);
        if (scankey == KeyEvent.KEYCODE_CAMERA && leftKeyEnable) {
            return true;
        }
        if (scankey == KeyEvent.KEYCODE_FOCUS && rightKeyEnable) {
            return true;
        }
        if (scankey == KeyEvent.KEYCODE_F3 && frontKeyEnable) {
            return true;
        }
        return false;
    }

    public boolean isNotificationEnable(Context context) {
        boolean enable = BaseSetting.getSharedPreferences(context).getBoolean(
                ScanSettingActivity.APP_NOTIFICATION_KEY, true);
        return enable;
    }

    private void sendScanNotification(Context context) {
        Notification notify = new Notification.Builder(context)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build();
        mNm.notify(SCAN_NOTIFICATION_ID, notify);
    }
}
