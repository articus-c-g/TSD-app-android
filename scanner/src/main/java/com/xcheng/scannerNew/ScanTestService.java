package com.xcheng.scannerNew;

import android.app.Instrumentation;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.xcheng.scannerNew.codesetting.BaseSetting;

public class ScanTestService extends Service {

    private static final String TAG = "ScanTestService";
    private boolean mIsDeviceOpen = false;
    private boolean mIsScanTest = false;
    private ScanThreadHandler mScanTheadHandler;
    private HandlerThread mHandlerThread;

    private ToneGenerator mToneGenerator;
    private Object mToneGeneratorLock = new Object();
    private static final int TONE_LENGTH_MS = 150;
    private AudioManager mAudioManager;

    public static final int DELAYED_TIME = 50;
    public static final int MSG_OPEN = 0x01;
    public static final int MSG_CLOSE = 0x02;
    public static final int MSG_READ_DATA = 0x03;

    class ScanThreadHandler extends Handler {
        public ScanThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message arg) {
            Bundle bundle;
            //Log.d(TAG, "handleMessage: " + arg.what);
            switch (arg.what) {
                case MSG_OPEN:
                    int i = 0;
                    while (!openDevice()) {
                        i++;
                        Log.d(TAG, "test***MSG_OPEN: i = " + i);
                    }
                    mScanTheadHandler.sendEmptyMessage(MSG_READ_DATA);
                    break;
                case MSG_CLOSE:
                    int j = 0;
                    while (!closeDevice()) {
                        j++;
                        Log.d(TAG, "test***MSG_CLOSE: j = " + j);
                    }
                    break;
                case MSG_READ_DATA:
                    String result = ScanNative.readData();
                    Log.d(TAG, "result: " + result);
                    if (TextUtils.isEmpty(result)) {
                        if (isDeviceOpen()) {
                            mScanTheadHandler.sendEmptyMessageDelayed(MSG_READ_DATA, DELAYED_TIME);
                        }
                    } else {
                        if (isDeviceOpen()) {
                            mIsDeviceOpen = false;
                            while(true) {
                                if (ScanNative.closeDev()) {
                                    break;
                                }
                            }
                            ScanUtil.setScanLight(ScanTestService.this);
                            scanNotification(true);
                            Log.d(TAG, "mIsScanTest: " + mIsScanTest);
                            if (!mIsScanTest) {
                                result = result.substring(1);
                                inputScanResult(result);
                            } else {
                                sendDataBroadcast(ScanTestService.this, result);
                            }
                        } else {
                            Log.d(TAG, "Scanner closed before read data");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initThread() {
        mHandlerThread = new HandlerThread("ScanTestService");
        mHandlerThread.start();
        mScanTheadHandler = new ScanThreadHandler(mHandlerThread.getLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initThread();
        initToneGenerator();
        /*if (!SystemProperties.getBoolean("sys.scantype.enable", false)) {
            ScanNative.profileScanType();
            SystemProperties.set("sys.scantype.enable", "on");
        }*/
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (mScanTheadHandler != null) {
            mScanTheadHandler.removeCallbacksAndMessages(null);
            //boolean isTyping = SystemProperties.getBoolean("sys.scan.typing", false);
           // Log.d(TAG, "isTyping: " + isTyping);
           // if (!isDeviceOpen() && !isTyping) {
              //  mScanTheadHandler.sendEmptyMessage(MSG_OPEN);
            //}
        }
        mIsScanTest = intent.getBooleanExtra("isScanTest", false);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsScanTest = false;
        quitThread();
    }

    private void quitThread() {
        try {
            Thread.currentThread().sleep(50);
        } catch (InterruptedException e) {
             e.printStackTrace();
        }
        if (isDeviceOpen()) {
            mIsDeviceOpen = false;
            ScanUtil.setScanLight(ScanTestService.this);
            scanNotification(false);
            while(true) {
                if (ScanNative.closeDev()) {
                    break;
                }
            }
        }
        if (mHandlerThread != null) {
            boolean mIsQuit = mHandlerThread.quit();
            Log.d(TAG, "quitThread: mIsQuit = " + mIsQuit);
        }
    }

    private boolean openDevice() {
        //Log.d(TAG, "openDevice: mIsDeviceOpen = " + mIsDeviceOpen);
        if (!mIsDeviceOpen) {
            mIsDeviceOpen = ScanNative.openDev();
        }
        return mIsDeviceOpen;
    }

    private boolean closeDevice() {
        //Log.d(TAG, "closeDevice: mIsDeviceOpen = " + mIsDeviceOpen);
        boolean isDeviceClose = false;
        if (mIsDeviceOpen) {
            isDeviceClose = ScanNative.closeDev();
            mIsDeviceOpen = !isDeviceClose;
        }
        return isDeviceClose;
    }

    public boolean isDeviceOpen() {
        return mIsDeviceOpen;
    }

    private void inputScanResult(String result) {
        //result = result.substring(0,result.length()-2);
        result = getCustomLetterCaseResult(result);
        Log.i(TAG, "inputScanResult: result = " + result);
        Instrumentation inst = new Instrumentation();
        sendPrefix(inst);
        selectDataReceiveMethod(inst, result);
        sendSuffix(inst);
    }

    private void sendIntentData(String result) {
        sendDataBroadcast(ScanTestService.this, result);
    }

    private void sendKeyboardData(Instrumentation inst, String result) {
        char[] chars = result.toCharArray();
        String delayTimeValue = getDefaultValue(ScanSettingActivity.TYPING_DELAY_KEY, "0");
        String userDefValue = getDefaultValue(ScanSettingActivity.USER_DEFINITION_KEY, "0");
        int delayTime = Integer.parseInt(delayTimeValue);
        if (delayTime == 1) {
            delayTime = Integer.parseInt(userDefValue);
        }
        Log.i(TAG, "sendKeyboardData: delayTime = " + delayTime);
        //SystemProperties.set("sys.scan.typing", "true");
        for(int i = 0; i < chars.length; i++) {
            Log.i(TAG, "sendKeyboardData: result = " + String.valueOf(chars[i]));
            inst.sendStringSync(String.valueOf(chars[i]));
            try {
                Thread.currentThread().sleep(delayTime);
            } catch (InterruptedException e) {
                 e.printStackTrace();
            }
        }
        //SystemProperties.set("sys.scan.typing", "false");
    }

    private void sendClipboardData(Instrumentation inst, String result) {
        ClipboardManager copy = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        copy.setText(result);
        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_PASTE);
    }

    private void selectDataReceiveMethod(Instrumentation inst, String result) {
        String value = getDefaultValue(ScanSettingActivity.DATA_RECEIVE_KEY, "KEYBOARD_EVENT");
        if ("KEYBOARD_EVENT".equals(value)) {
            sendKeyboardData(inst, result);
        } else if ("CLIPBOARD_EVENT".equals(value)) {
            sendClipboardData(inst, result);
        } else if ("INTENT_EVENT".equals(value)) {
            sendIntentData(result);
        }
    }

    private void sendPrefix(Instrumentation inst){
        String mPrefixChar1  = BaseSetting.getSharedPreferences(ScanTestService.this).getString(ScanSettingActivity.PREFIX_CHAR1_KEY,ScanSettingActivity.EMPTY);
        String mPrefixChar2  = BaseSetting.getSharedPreferences(ScanTestService.this).getString(ScanSettingActivity.PREFIX_CHAR2_KEY,ScanSettingActivity.EMPTY);
        sendSpecialCharacters(inst,mPrefixChar1,false);
        sendSpecialCharacters(inst,mPrefixChar2,false);
    }

    private void sendSuffix(Instrumentation inst){
        String mSuffixChar1  = BaseSetting.getSharedPreferences(ScanTestService.this).getString(ScanSettingActivity.SUFFIX_CHAR1_KEY,"ENTER");
        String mSuffixChar2  = BaseSetting.getSharedPreferences(ScanTestService.this).getString(ScanSettingActivity.SUFFIX_CHAR2_KEY,ScanSettingActivity.EMPTY);
        sendSpecialCharacters(inst,mSuffixChar1,true);
        sendSpecialCharacters(inst,mSuffixChar2,true);
    }

    private void sendSpecialCharacters(Instrumentation inst,String specialStr,boolean sendTabEnter){
        if(("Empty").equals(specialStr)){
        }else if(("TAB").equals(specialStr)){
            if(sendTabEnter) inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        }else if(("ENTER").equals(specialStr)){
            if(sendTabEnter) inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
        }else if(("SPACE").equals(specialStr)){
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_SPACE);
        }else{
            inst.sendStringSync(specialStr);
        }
    }
    private String getCustomLetterCaseResult(String result){
        String mLetterCase  = BaseSetting.getSharedPreferences(ScanTestService.this).getString(ScanSettingActivity.LETTER_CASE_KEY,"NONE_CASE");
        switch(mLetterCase){
            case "NONE_CASE":
                break;
            case "UPPER_CASE":
                result = result.toUpperCase();
                break;
            case "LOWER_CASE":
                result = result.toLowerCase();
                break;
            default:
                break;
        }
        return result;
    }

    private void sendDataBroadcast(Context context, String barcode) {
        String actionName = ScanUtil.getScanActionName(context);
        String barcodeData = ScanUtil.getScanBarcodeData(context);
        String symbologyType = ScanUtil.getScanSymbologyType(context);
        String value = barcode.substring(0,1);
        String symbology = getScanSymbology(value);
        barcode = barcode.substring(1);
        Intent intent = new Intent();
        intent.setAction(actionName);
        intent.putExtra(barcodeData, barcode);
        intent.putExtra(symbologyType, symbology);
        sendBroadcast(intent);
    }

    private void initToneGenerator() {
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                    mToneGenerator = null;
                }
            }
        }
    }

    private void playTone() {
        int ringerMode = mAudioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
                || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF, 15,
                AudioManager.FLAG_PLAY_SOUND);
        if (mToneGenerator != null) {
            mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, TONE_LENGTH_MS);
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        int ringerMode = mAudioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)) {
            return;
        }
        if (vibrator != null) {
            vibrator.vibrate(100);
        }
    }

    private void scanNotification(boolean isSuccess) {
        String value;
        if (isSuccess) {
            value = BaseSetting.getSharedPreferences(ScanTestService.this)
                    .getString(ScanSettingActivity.SUCCESS_NOTIFICATION_KEY,
                            "Sound");
        } else {
            value = BaseSetting.getSharedPreferences(ScanTestService.this)
                    .getString(ScanSettingActivity.FAIL_NOTIFICATION_KEY,
                            "Mute");
        }
        Log.d(TAG, "test***scanNotification: value = " + value);
        if ("Sound".equals(value) || "Sound/Vib".equals(value)) {
            playTone();
        }
        if ("Vib".equals(value) || "Sound/Vib".equals(value)) {
            vibrate();
        }
    }

    public String getScanSymbology(String value) {
        String[] typeValue = getResources().getStringArray(R.array.scan_type_values);
        String[] typeEntries = getResources().getStringArray(R.array.scan_type_entries);
        String symbology = "unknow";
        for (int i = 0; i < typeValue.length; i++) {
            if (typeValue[i].equals(value)) {
                symbology = typeEntries[i];
                Log.d(TAG, "getScanSymbology: i = " + i);
                break;
            }
        }
        Log.d(TAG, "getScanSymbology: symbology = " + symbology);
        return symbology;
    }

    public String getDefaultValue(String key, String defaultValue) {
        String value = BaseSetting.getSharedPreferences(this).getString(key,
                defaultValue);
        return value;
    }
}
