package com.xcheng.scannerNew;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;

public class PublicUtil {
    private final static String TAG = "PublicUtil";

    private static final String ACTION_OPEN_SCAN = ScanUtil.ACTION_OPEN_SCAN;
    private static final String ACTION_CLOSE_SCAN = ScanUtil.ACTION_CLOSE_SCAN;
    private static final String SCANKEY = ScanUtil.SCANKEY;

    private static final String ACTION_ENABLE_TYPE = ScanUtil.ACTION_ENABLE_TYPE;
    private static final String ACTION_DISABLE_TYPE = ScanUtil.ACTION_DISABLE_TYPE;
    private static final String SCANTYPE = ScanUtil.SCANTYPE;

    public static String getSN(){
        return Build.SERIAL;
    }

    public static void startImportExport(Context context){
        Intent intent = new Intent();
        intent.setClassName("com.xcheng.scanner", "com.xcheng.scanner.ImportExportPublicActivity");
        context.startActivity(intent);
    }

    public static void openCloseDevice(Context context, boolean isOpen){
        Intent intent = new Intent();
        if (isOpen) {
            intent.setAction(ACTION_OPEN_SCAN);
        } else {
            intent.setAction(ACTION_CLOSE_SCAN);
        }
        intent.putExtra(SCANKEY, KeyEvent.KEYCODE_F3);
        context.sendBroadcast(intent);
    }

    public static void enableDisableSymbology(Context context, boolean isEnable, int type){
        Intent intent = new Intent();
        if (isEnable) {
            intent.setAction(ACTION_ENABLE_TYPE);
        } else {
            intent.setAction(ACTION_DISABLE_TYPE);
        }
        intent.putExtra(SCANTYPE, type);
        context.sendBroadcast(intent);
    }

    public static void setScankeyStatus(Context context, int keyCode, boolean isEnable){
        Intent intent = new Intent();
        intent.setAction(ScanUtil.ACTION_CONTROL_SCANKEY);
        intent.putExtra(ScanUtil.EXTRA_SCANKEY_CODE, keyCode);
        intent.putExtra(ScanUtil.EXTRA_SCANKEY_STATUS, isEnable);
        context.sendBroadcast(intent);
    }

    public static String getSdCardPath() {
        String path = ScanUtil.getSdCardPath() + ScanUtil.XML_FILE_NAME;
        ScanUtil.writeFileDataTo(path,
                "/data/data/com.xcheng.scanner/shared_prefs/" + ScanUtil.XML_FILE_NAME);
        return path;
    }

}
