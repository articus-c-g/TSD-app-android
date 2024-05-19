package com.xcheng.scannerNew;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.xcheng.scannerNew.codesetting.BaseSetting;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * This class define Scanner decode type
 */
public class ScanUtil {
    private final static String TAG = "ScanUtil";

    //all Code: 999001
    public static final int Type_Code_All = 0;

    //Aztec Code: 931001
    public static final int Type_Aztec_Code = 1;

    //China Post: 936001
    public static final int Type_China_Post = 2;

    //Codabar: 900003
    public static final int Type_Codabar = 3;

    //Codablock A: 922001
    public static final int Type_Codablock_A = 4;

    //Codablock F: 923001
    public static final int Type_Codablock_F = 5;

    //Code 11: 908002
    public static final int Type_Code_11 = 6;

    //Code 39: 901001
    public static final int Type_Code_39 = 7;

    //Code 93: 904002
    public static final int Type_Code_93 = 8;

    //Code 128: 909001
    public static final int Type_Code_128 = 9;

    //Data Matrix: 929001
    public static final int Type_Data_Matrix = 10;

    //EAN/JAN-8: 916001
    public static final int Type_EAN_JAN_8 = 11;

    //EAN/JAN-13: 915001
    public static final int Type_EAN_JAN_13 = 12;

    //GS1-128: 910001
    public static final int Type_GS1_128 = 13;

    //GS1 Composite Codes: 926001
    public static final int Type_GS1_Composite = 14;

    //GS1 DataBar Expanded: 920001
    public static final int Type_GS1_DataBar_Expanded = 15;

    //GS1 DataBar Limited: 919001
    public static final int Type_GS1_DataBar_Limited = 16;

    //GS1 DataBar Omnidiretional: 918001
    public static final int Type_GS1_DataBar_Omnidiretional = 17;

    //Han Xin: 932001
    public static final int Type_Han_Xin = 18;

    //Interleaved 2 of 5: 902002
    public static final int Type_Interleaved_2_5 = 19;

    //Korea Post: 937001
    public static final int Type_Korea_Post = 20;

    //Matrix 2 of 5: 907001
    public static final int Type_Matrix_2_5 = 21;

    //MaxiCode: 930001
    public static final int Type_MaxiCode = 22;

    //MSI-Plessey: 917001
    public static final int Type_MSI_Plessey = 23;

    //Micro PDF417: 925001
    public static final int Type_Micro_PDF417 = 24;

    //NEC 2 of 5: 903001
    public static final int Type_NEC_2_5 = 25;

    //PDF417: 924001
    public static final int Type_PDF417 = 26;

    //QR Code: 928001
    public static final int Type_QR_Code = 27;

    //Straight 2 of 5: 905001
    public static final int Type_Straight_2_5 = 28;

    //Straight 2 of 5 IATA: 906001
    public static final int Type_Straight_2_5_IATA = 29;

    //Telepen: 911001
    public static final int Type_Telepen = 30;

    //TCIF Linked Code 39: 927001
    public static final int Type_TCIF_Linked_Code_39 = 31;

    //Trioptic Code: 921001
    public static final int Type_Trioptic_Code = 32;

    //UPC-A: 912003
    public static final int Type_UPC_A = 33;

    //UPC-E0: 914001
    public static final int Type_UPC_E0 = 34;

    public static final int MAX_TYPE = 35;

    private static final String KEY_SP_0 = "all_type"; // 0--always hide and disable
    private static final String KEY_SP_1 = "aztec_code"; // 1
    private static final String KEY_SP_2 = "china_post"; // 2
    private static final String KEY_SP_3 = "codabar"; // 3
    private static final String KEY_SP_4 = "codablock_A"; // 4
    private static final String KEY_SP_5 = "codablock_F"; // 5
    private static final String KEY_SP_6 = "code11"; // 6
    private static final String KEY_SP_7 = "code39"; // 7
    private static final String KEY_SP_8 = "code93"; // 8
    private static final String KEY_SP_9 = "code128"; // 9
    private static final String KEY_SP_10 = "data_matrix"; // 10
    private static final String KEY_SP_11 = "ean_jan8"; // 11
    private static final String KEY_SP_12 = "ean_jan13"; // 12
    private static final String KEY_SP_13 = "gs1_128"; // 13
    private static final String KEY_SP_14 = "gs1_composite"; // 14
    private static final String KEY_SP_15 = "gs1_expanded"; // 15
    private static final String KEY_SP_16 = "gs1_limited"; // 16
    private static final String KEY_SP_17 = "gs1_omnidiretional"; // 17
    private static final String KEY_SP_18 = "han_xin"; // 18
    private static final String KEY_SP_19 = "interleaved_25"; // 19
    private static final String KEY_SP_20 = "korea_post"; // 20
    private static final String KEY_SP_21 = "matrix_25"; // 21
    private static final String KEY_SP_22 = "maxicode"; // 22
    private static final String KEY_SP_23 = "msi_plessey"; // 23
    private static final String KEY_SP_24 = "micro_pdf417"; // 24
    private static final String KEY_SP_25 = "nec_25"; // 25
    private static final String KEY_SP_26 = "pdf417"; // 26
    private static final String KEY_SP_27 = "qr_code"; // 27
    private static final String KEY_SP_28 = "straight_25"; // 28
    private static final String KEY_SP_29 = "straight_25_iata"; // 29
    private static final String KEY_SP_30 = "telepen"; // 30
    private static final String KEY_SP_31 = "tcif_linked_code39"; // 31
    private static final String KEY_SP_32 = "trioptic"; // 32
    private static final String KEY_SP_33 = "upc_A"; // 33
    private static final String KEY_SP_34 = "upc_E0"; // 34

    public static final int[] SP_TITLE_RES = {
        R.string.text_all_type, R.string.text_aztec_code, R.string.text_china_post,
        R.string.text_codabar, R.string.text_codablock_A, R.string.text_codablock_F,
        R.string.text_code11, R.string.text_code39, R.string.text_code93,
        R.string.text_code128, R.string.text_data_matrix, R.string.text_ean_jan8,
        R.string.text_ean_jan13, R.string.text_gs1_128, R.string.text_gs1_composite,
        R.string.text_gs1_expanded, R.string.text_gs1_limited, R.string.text_Omnidiretional,
        R.string.text_han_xin, R.string.text_interleaved_25, R.string.text_korea_post,
        R.string.text_matrix_25, R.string.text_maxicode, R.string.text_msi_plessey,
        R.string.text_micro_pdf417, R.string.text_nec_25, R.string.text_pdf417,
        R.string.text_qr_code, R.string.text_straight_25, R.string.text_straight_25_iata,
        R.string.text_telepen, R.string.text_tcif_linked_code39, R.string.text_trioptic,
        R.string.text_upc_A, R.string.text_upc_E0
    };

    public static final String[] SP_KEYS = {
        KEY_SP_0, KEY_SP_1, KEY_SP_2, KEY_SP_3, KEY_SP_4, KEY_SP_5,
        KEY_SP_6, KEY_SP_7, KEY_SP_8, KEY_SP_9, KEY_SP_10, KEY_SP_11,
        KEY_SP_12, KEY_SP_13, KEY_SP_14, KEY_SP_15, KEY_SP_16, KEY_SP_17,
        KEY_SP_18, KEY_SP_19, KEY_SP_20, KEY_SP_21, KEY_SP_22, KEY_SP_23,
        KEY_SP_24, KEY_SP_25, KEY_SP_26, KEY_SP_27, KEY_SP_28, KEY_SP_29,
        KEY_SP_30, KEY_SP_31, KEY_SP_32, KEY_SP_33, KEY_SP_34
    };

    public static final String SCAN_DECODING_BROADCAST = "com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST";
    public static final String SCAN_DECODING_DATA = "EXTRA_BARCODE_DECODING_DATA";
    public static final String SCAN_SYMBOLOGY_TYPE = "EXTRA_BARCODE_DECODING_SYMBOLE";
    public static final String ACTION_NAME_KEY = "pref_action_name";
    public static final String BARCODE_DATA_KEY = "pref_barcode_data";
    public static final String SYMBOLOGY_TYPE_KEY = "pref_symbology_type";

    public static final String LEFT_SCAN_KEY = "pref_left_scankey";
    public static final String RIGHT_SCAN_KEY = "pref_right_scankey";
    public static final String FRONT_SCAN_KEY = "pref_front_scankey";

    public static final String ACTION_OPEN_SCAN = "com.xcheng.scanner.action.OPEN_SCAN_BROADCAST";
    public static final String ACTION_CLOSE_SCAN = "com.xcheng.scanner.action.CLOSE_SCAN_BROADCAST";
    public static final String SCANKEY = "scankey";

    public static final String ACTION_ENABLE_TYPE = "com.xcheng.scanner.action.ENABLE_SCANTYPE_BROADCAST";
    public static final String ACTION_DISABLE_TYPE = "com.xcheng.scanner.action.DISABLE_SCANTYPE_BROADCAST";
    public static final String SCANTYPE = "scantype";

    public static final String ACTION_CONTROL_SCANKEY = "com.xcheng.scanner.action.ACTION_CONTROL_SCANKEY";
    public static final String EXTRA_SCANKEY_CODE = "extra_scankey_code";
    public static final String EXTRA_SCANKEY_STATUS = "extra_scankey_STATUS";

    public final static String XML_FILE_NAME = "com.xcheng.scanner_preferences.xml";

    public static String readFileData(String fileName) {
        String result = "";
        try {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            Log.d(TAG, "readFileData->e: " + e);
            e.printStackTrace();
        }
        return result;
    }

    public static void writeFileDataTo(String toFileName, String fromFileName) {
        try {
            File file = new File(toFileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(readFileData(fromFileName).getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.d(TAG, "writeFileData->e: " + e);
            e.printStackTrace();
        }
    }

    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        Log.d(TAG, "getSdCardPath: " + exist);
        String sdPath = "/storage/emulated/0/";
        if (exist) {
            sdPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdPath = "/storage/emulated/0/";
        }
        return sdPath + "/";
    }

    private static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static void xmlPullParser(Context context) {
        SharedPreferences sharedPreferences = BaseSetting.getSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        try {
            XmlPullParser pullParser = Xml.newPullParser();
            InputStream is = new ByteArrayInputStream(readFileData(getSdCardPath() + XML_FILE_NAME).getBytes("UTF-8"));
            pullParser.setInput(is, "utf-8");
            int eventType = pullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = pullParser.getName();
                if (!TextUtils.isEmpty(tagName) && "string".equals(tagName)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String nameString = pullParser.getAttributeValue(null, "name");
                        String valueString = pullParser.nextText();
                        //Log.d(TAG, "nameString = " + nameString);
                        //Log.d(TAG, "valuString = " + valueString);
                        editor.putString(nameString, valueString);
                        editor.commit();
                    }
                }
                if (!TextUtils.isEmpty(tagName) && "boolean".equals(tagName)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String nameBoolean = pullParser.getAttributeValue(null, "name");
                        //Log.d(TAG, "nameBoolean = " + nameBoolean);
                        String valueBoolean = pullParser.getAttributeValue(null, "value");
                        //Log.d(TAG, "valueBoolean = " + valueBoolean);
                        editor.putBoolean(nameBoolean, Boolean.parseBoolean(valueBoolean));
                        editor.commit();
                    }
                }
                if (!TextUtils.isEmpty(tagName) && "int".equals(tagName)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String nameInt = pullParser.getAttributeValue(null, "name");
                        //Log.d(TAG, "nameInt = " + nameInt);
                        String valueInt = pullParser.getAttributeValue(null, "value");
                        //Log.d(TAG, "valueInt = " + valueInt);
                        if (!TextUtils.isEmpty(valueInt)) {
                            editor.putInt(nameInt, new Integer(valueInt));
                            editor.commit();
                        }
                    }
                }
                eventType = pullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(Context context) {
        ScanNative.sendCommand("800006");
        SharedPreferences preferences = BaseSetting.getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static boolean isValidType(int type) {
        return type >= 0 && type < MAX_TYPE;
    }

    public static String getScanActionName(Context context) {
        String actionName = BaseSetting.getSharedPreferences(context)
                .getString(ACTION_NAME_KEY, SCAN_DECODING_BROADCAST);
        Log.d(TAG, "actionName: " + actionName);
        return actionName;
    }

    public static String getScanBarcodeData(Context context) {
        String barcodeData = BaseSetting.getSharedPreferences(context)
                .getString(BARCODE_DATA_KEY, ScanUtil.SCAN_DECODING_DATA);
        Log.d(TAG, "barcodeData: " + barcodeData);
        return barcodeData;
    }

    public static String getScanSymbologyType(Context context) {
        String symbologyType = BaseSetting.getSharedPreferences(context)
                .getString(SYMBOLOGY_TYPE_KEY, SCAN_SYMBOLOGY_TYPE);
        Log.d(TAG, "symbologyType: " + symbologyType);
        return symbologyType;
    }

    public static void setScanLight(Context context) {
        try {
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            //pm.setScanlight(true);
            Thread.sleep(100);
            //pm.setScanlight(false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
