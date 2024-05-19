package com.xcheng.scannerNew;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.xcheng.scannerNew.codesetting.BaseSetting;

public class ScanSettingActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "ScanSettingActivity";
    public static final String EMPTY = "Empty";

    public static final String SCANKEY_ENABLE_KEY = "pref_scankey_enable";
    public static final String LEFT_SCAN_KEY = "pref_left_scankey";
    public static final String RIGHT_SCAN_KEY = "pref_right_scankey";
    public static final String FRONT_SCAN_KEY = "pref_front_scankey";
    public static final String SUCCESS_NOTIFICATION_KEY = "pref_success_notification";
    public static final String FAIL_NOTIFICATION_KEY = "pref_fail_notification";
    public static final String APP_NOTIFICATION_KEY = "pref_notification_enable";
    public static final String TYPING_DELAY_KEY = "pref_keyboard_typing_delay";
    public static final String USER_DEFINITION_KEY = "pref_user_definition";
    public static final String PREFIX_CHAR1_KEY = "pref_prefix_char_1";
    public static final String PREFIX_CHAR2_KEY = "pref_prefix_char_2";
    public static final String SUFFIX_CHAR1_KEY = "pref_suffix_char_1";
    public static final String SUFFIX_CHAR2_KEY = "pref_suffix_char_2";
    public static final String LETTER_CASE_KEY = "pref_letter";
    public static final String SHOW_SCAN_BUTTON_KEY = "pref_show_scan_button";
    public static final String ENABLE_CHARACTER_MODIFY = "pref_character_modify_enable";
    public static final String CHARACTER_GS = "pref_character_gs";
    public static final String KEY_TIMEOUT = "pref_max_scan_time";
    public static final String DATA_RECEIVE_KEY = "pref_data_receive_method";
    public static final String ACTION_NAME_KEY = "pref_action_name";
    public static final String BARCODE_DATA_KEY = "pref_barcode_data";
    public static final String SYMBOLOGY_TYPE_KEY = "pref_symbology_type";
    public static final String ENABLE_SETTING_LOCK = "pref_lock";
    public static final String CHANGE_PASSWORD = "pref_change_lock_password";

    private SwitchPreference mEnaleLeftScankey;
    private SwitchPreference mEnaleRightScankey;
    private SwitchPreference mEnaleFrontScankey;
    private ListPreference mSuccessNotification;
    private ListPreference mFailNotification;
    private SwitchPreference mAppNotification;
    private ListPreference mKbdTypingDeleyPreference;
    private EditTextPreference mUserDefinitionPreference;
    private SwitchPreference mShowScreenScanButton;
    private SwitchPreference mEnableCharacterModifyPreference;
    private SwitchPreference mEnableSettingLockPreference;
    private ListPreference mPrefixChar1Preference;
    private ListPreference mPrefixChar2Preference;
    private ListPreference mSuffixChar1Preference;
    private ListPreference mSuffixChar2Preference;
    private ListPreference mLetterCasePreference;
    private ListPreference mCharacterGSPreference;
    private ListPreference mTimeOutPreference;
    private ListPreference mDataReceivePreference;
    private EditTextPreference mActionNamePreference;
    private EditTextPreference mBarcodeDataPreference;
    private EditTextPreference mSymbologyTypePreference;
    private Preference changePwdPreference;

    private SharedPreferences mShare;
    private Editor editor;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPreferences();
        mContext = ScanSettingActivity.this;
    }

    private void initPreferences() {

        mShare = BaseSetting.getSharedPreferences(ScanSettingActivity.this);
        editor = mShare.edit();

        boolean mEnableLeftScan  = mShare.getBoolean(LEFT_SCAN_KEY,true);
        boolean mEnableRightScan  = mShare.getBoolean(RIGHT_SCAN_KEY,true);
        boolean mEnableFrontScan  = mShare.getBoolean(FRONT_SCAN_KEY,true);
        boolean mShowSacnButton  = mShare.getBoolean(SHOW_SCAN_BUTTON_KEY,true);
        boolean mEnableNotication  = mShare.getBoolean(APP_NOTIFICATION_KEY,true);
        boolean mEnableCharacterModify  = mShare.getBoolean(ENABLE_CHARACTER_MODIFY,false);
        boolean mEnableSettingLock = mShare.getBoolean(ENABLE_SETTING_LOCK,false);
        String mPrefixChar1  = mShare.getString(PREFIX_CHAR1_KEY,EMPTY);
        String mPrefixChar2  = mShare.getString(PREFIX_CHAR2_KEY,EMPTY);
        String mSuffixChar1  = mShare.getString(SUFFIX_CHAR1_KEY,"ENTER");
        String mSuffixChar2  = mShare.getString(SUFFIX_CHAR2_KEY,EMPTY);
        String mLetterCase  = mShare.getString(LETTER_CASE_KEY,"NONE_CASE");
        String mCharacterGS  = mShare.getString(CHARACTER_GS,EMPTY);
        String mActionName  = mShare.getString(ACTION_NAME_KEY, ScanUtil.SCAN_DECODING_BROADCAST);
        String mBarcodeData  = mShare.getString(BARCODE_DATA_KEY, ScanUtil.SCAN_DECODING_DATA);
        String mSymbologyType  = mShare.getString(SYMBOLOGY_TYPE_KEY, ScanUtil.SCAN_SYMBOLOGY_TYPE);
        String mPassword  = mShare.getString(CHANGE_PASSWORD, "0000");
        String mSuccessValue  = mShare.getString(SUCCESS_NOTIFICATION_KEY,"Sound");
        String mFailValue  = mShare.getString(FAIL_NOTIFICATION_KEY,"Mute");
        String mDataReceiveMethod = mShare.getString(DATA_RECEIVE_KEY, "KEYBOARD_EVENT");
        String mTypingDelay  = mShare.getString(TYPING_DELAY_KEY,"0");
        String mUserDefinition = mShare.getString(USER_DEFINITION_KEY, "0");

        addPreferencesFromResource(R.xml.scan_setting);
        //Max scan time
        String maxScanTime = mShare.getString(KEY_TIMEOUT,"3000");
        ScanNative.setTimeout(Integer.parseInt(maxScanTime));

        mTimeOutPreference = (ListPreference)findPreference(KEY_TIMEOUT);
        mTimeOutPreference.setValue(maxScanTime);
        mTimeOutPreference.setOnPreferenceChangeListener(this);
        updateTimeoutPreferenceSummary(Integer.parseInt(maxScanTime));
        //DataReceiveMethod
        mDataReceivePreference = (ListPreference)findPreference(DATA_RECEIVE_KEY);
        mDataReceivePreference.setValue(mDataReceiveMethod);
        mDataReceivePreference.setSummary(mDataReceiveMethod);
        mDataReceivePreference.setOnPreferenceChangeListener(this);
        //LeftScankey
        mEnaleLeftScankey = (SwitchPreference)findPreference(LEFT_SCAN_KEY);
        mEnaleLeftScankey.setChecked(mEnableLeftScan);
        mEnaleLeftScankey.setOnPreferenceChangeListener(this);
        //RightScankey
        mEnaleRightScankey = (SwitchPreference)findPreference(RIGHT_SCAN_KEY);
        mEnaleRightScankey.setChecked(mEnableRightScan);
        mEnaleRightScankey.setOnPreferenceChangeListener(this);
        //FrontScankey
        mEnaleFrontScankey = (SwitchPreference)findPreference(FRONT_SCAN_KEY);
        mEnaleFrontScankey.setChecked(mEnableFrontScan);
        mEnaleFrontScankey.setOnPreferenceChangeListener(this);
        //Scan Notification
        mSuccessNotification = (ListPreference)findPreference(SUCCESS_NOTIFICATION_KEY);
        mSuccessNotification.setValue(mSuccessValue);
        mSuccessNotification.setSummary(mSuccessValue);
        mSuccessNotification.setOnPreferenceChangeListener(this);
        mFailNotification = (ListPreference)findPreference(FAIL_NOTIFICATION_KEY);
        mFailNotification.setValue(mFailValue);
        mFailNotification.setSummary(mFailValue);
        mFailNotification.setOnPreferenceChangeListener(this);
        //Notification
        mAppNotification = (SwitchPreference)findPreference(APP_NOTIFICATION_KEY);
        mAppNotification.setChecked(mEnableNotication);
        mAppNotification.setOnPreferenceChangeListener(this);
        //Scan Delay Setting
        mKbdTypingDeleyPreference = (ListPreference)findPreference(TYPING_DELAY_KEY);
        mKbdTypingDeleyPreference.setValue(mTypingDelay);
        mKbdTypingDeleyPreference.setSummary(mKbdTypingDeleyPreference.getEntry());
        mKbdTypingDeleyPreference.setEnabled("KEYBOARD_EVENT".equals(mDataReceiveMethod));
        mKbdTypingDeleyPreference.setOnPreferenceChangeListener(this);

        mUserDefinitionPreference = (EditTextPreference)findPreference(USER_DEFINITION_KEY);
        mUserDefinitionPreference.setText(mUserDefinition);
        mUserDefinitionPreference.setSummary(mUserDefinition + " MilliSecond");
        mUserDefinitionPreference.setEnabled("1".equals(mTypingDelay)
                && "KEYBOARD_EVENT".equals(mDataReceiveMethod));
        mUserDefinitionPreference.setOnPreferenceChangeListener(this);
        //ShowScanButton
        mShowScreenScanButton = (SwitchPreference)findPreference(SHOW_SCAN_BUTTON_KEY);
        mShowScreenScanButton.setChecked(mShowSacnButton);
        mShowScreenScanButton.setOnPreferenceChangeListener(this);

        //PrefixChar1
        mPrefixChar1Preference = (ListPreference)findPreference(PREFIX_CHAR1_KEY);
        mPrefixChar1Preference.setValue(mPrefixChar1);
        mPrefixChar1Preference.setSummary("%".equals(mPrefixChar1)?"%%":mPrefixChar1);
        mPrefixChar1Preference.setOnPreferenceChangeListener(this);

        //PrefixChar2
        mPrefixChar2Preference = (ListPreference)findPreference(PREFIX_CHAR2_KEY);
        mPrefixChar2Preference.setValue(mPrefixChar2);
        mPrefixChar2Preference.setSummary("%".equals(mPrefixChar2)?"%%":mPrefixChar2);
        mPrefixChar2Preference.setOnPreferenceChangeListener(this);

        //SuffixChar1
        mSuffixChar1Preference = (ListPreference)findPreference(SUFFIX_CHAR1_KEY);
        mSuffixChar1Preference.setValue(mSuffixChar1);
        mSuffixChar1Preference.setSummary("%".equals(mSuffixChar1)?"%%":mSuffixChar1);
        mSuffixChar1Preference.setOnPreferenceChangeListener(this);

        //SuffixChar2
        mSuffixChar2Preference = (ListPreference)findPreference(SUFFIX_CHAR2_KEY);
        mSuffixChar2Preference.setValue(mSuffixChar2);
        mSuffixChar2Preference.setSummary("%".equals(mSuffixChar2)?"%%":mSuffixChar2);
        mSuffixChar2Preference.setOnPreferenceChangeListener(this);

        //LetterCase
        mLetterCasePreference = (ListPreference)findPreference(LETTER_CASE_KEY);
        mLetterCasePreference.setValue(mLetterCase);
        mLetterCasePreference.setSummary(mLetterCase);
        mLetterCasePreference.setOnPreferenceChangeListener(this);

        //EnableCharacterModify
        mEnableCharacterModifyPreference = (SwitchPreference)findPreference(ENABLE_CHARACTER_MODIFY);
        mEnableCharacterModifyPreference.setChecked(mEnableCharacterModify);
        mEnableCharacterModifyPreference.setOnPreferenceChangeListener(this);

        //CharacterGs
        mCharacterGSPreference = (ListPreference)findPreference(CHARACTER_GS);
        mCharacterGSPreference.setValue(mCharacterGS);
        mCharacterGSPreference.setSummary("%".equals(mCharacterGS)?"%%":mCharacterGS);
        mCharacterGSPreference.setEnabled(mEnableCharacterModify);
        mCharacterGSPreference.setOnPreferenceChangeListener(this);

        //Broadcast Settings
        mActionNamePreference = (EditTextPreference)findPreference(ACTION_NAME_KEY);
        mActionNamePreference.setText(mActionName);
        mActionNamePreference.setSummary(mActionName);
        mActionNamePreference.setOnPreferenceChangeListener(this);

        mBarcodeDataPreference = (EditTextPreference)findPreference(BARCODE_DATA_KEY);
        mBarcodeDataPreference.setText(mBarcodeData);
        mBarcodeDataPreference.setSummary(mBarcodeData);
        mBarcodeDataPreference.setOnPreferenceChangeListener(this);

        mSymbologyTypePreference = (EditTextPreference)findPreference(SYMBOLOGY_TYPE_KEY);
        mSymbologyTypePreference.setText(mSymbologyType);
        mSymbologyTypePreference.setSummary(mSymbologyType);
        mSymbologyTypePreference.setOnPreferenceChangeListener(this);

        //EnableSettingLock
        mEnableSettingLockPreference = (SwitchPreference)findPreference(ENABLE_SETTING_LOCK);
        mEnableSettingLockPreference.setChecked(mEnableSettingLock);
        mEnableSettingLockPreference.setOnPreferenceChangeListener(this);

        //ChangePassword
        changePwdPreference = (Preference)findPreference(CHANGE_PASSWORD);
        changePwdPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO Auto-generated method stub
                String password  = mShare.getString(CHANGE_PASSWORD, "0000");
                changePassword(password);
                return false;
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onPreferenceChange: preference = " + preference);
        Log.d(TAG, "onPreferenceChange: newValue = " + newValue);
        if (preference == mEnaleLeftScankey) {
            if (mEnaleLeftScankey.isChecked() != (Boolean) newValue) {
                boolean value = (Boolean) (newValue);
                mEnaleLeftScankey.setChecked(value);
                editor.putBoolean(LEFT_SCAN_KEY,value);
            }
        } else if (preference == mEnaleRightScankey) {
            if (mEnaleRightScankey.isChecked() != (Boolean) newValue) {
                boolean value = (Boolean) (newValue);
                mEnaleRightScankey.setChecked(value);
                editor.putBoolean(RIGHT_SCAN_KEY,value);
            }
        } else if (preference == mEnaleFrontScankey) {
            if (mEnaleFrontScankey.isChecked() != (Boolean) newValue) {
                boolean value = (Boolean) (newValue);
                mEnaleFrontScankey.setChecked(value);
                editor.putBoolean(FRONT_SCAN_KEY,value);
            }
        } else if (preference == mSuccessNotification) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
            String value = (String) (newValue);
            mSuccessNotification.setValue(value);
            mSuccessNotification.setSummary(value);
            editor.putString(SUCCESS_NOTIFICATION_KEY,value);
        } else if (preference == mFailNotification) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
            String value = (String) (newValue);
            mFailNotification.setValue(value);
            mFailNotification.setSummary(value);
            editor.putString(FAIL_NOTIFICATION_KEY,value);
        } else if (preference == mAppNotification) {
            if (mAppNotification.isChecked() != (Boolean) newValue) {
                boolean value = (Boolean) (newValue);
                mAppNotification.setChecked(value);
                editor.putBoolean(APP_NOTIFICATION_KEY,value);
            }
        } else if (preference == mKbdTypingDeleyPreference) {
            Log.d(TAG, "mKbdTypingDeleyPreference: ***** newValue"+newValue);
            String value = (String) (newValue);
            mKbdTypingDeleyPreference.setValue(value);
            mKbdTypingDeleyPreference.setSummary(mKbdTypingDeleyPreference.getEntry());
            mUserDefinitionPreference.setEnabled("1".equals(value));
            editor.putString(TYPING_DELAY_KEY,value);
        } else if (preference == mUserDefinitionPreference) {
            Log.d(TAG, "mUserDefinitionPreference: ***** newValue" + newValue);
            String value = (String) (newValue);
            mUserDefinitionPreference.setText(value);
            mUserDefinitionPreference.setSummary(value + " MilliSecond");
            editor.putString(USER_DEFINITION_KEY, value);
        } else if (preference == mShowScreenScanButton) {
            if (mShowScreenScanButton.isChecked() != (Boolean) newValue) {
                Log.d(TAG, "onPreferenceChange: ***** cmf2");
                boolean value = (Boolean) (newValue);
                mShowScreenScanButton.setChecked(value);
                editor.putBoolean(SHOW_SCAN_BUTTON_KEY,value);
                Settings.System.putInt(getContentResolver(),
                        "sys.showscanbutton.switch", value ? 1 : 0);
            }
        } else if (preference == mPrefixChar1Preference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
	     String value = (String) (newValue);
            mPrefixChar1Preference.setValue(value);
            mPrefixChar1Preference.setSummary("%".equals(value)?"%%":value);
            editor.putString(PREFIX_CHAR1_KEY,value);
        } else if (preference == mPrefixChar2Preference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
	     String value = (String) (newValue);
            mPrefixChar2Preference.setValue(value);
            mPrefixChar2Preference.setSummary("%".equals(value)?"%%":value);
            editor.putString(PREFIX_CHAR2_KEY,value);
        } else if (preference == mSuffixChar1Preference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
	     String value = (String) (newValue);
            mSuffixChar1Preference.setValue(value);
            mSuffixChar1Preference.setSummary("%".equals(value)?"%%":value);
            editor.putString(SUFFIX_CHAR1_KEY,value);
        } else if (preference == mSuffixChar2Preference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
	     String value = (String) (newValue);
            mSuffixChar2Preference.setValue(value);
            mSuffixChar2Preference.setSummary("%".equals(value)?"%%":value);
            editor.putString(SUFFIX_CHAR2_KEY,value);
        } else if (preference == mLetterCasePreference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
	     String value = (String) (newValue);
            mLetterCasePreference.setValue(value);
            mLetterCasePreference.setSummary(value);
            editor.putString(LETTER_CASE_KEY,value);
        } else if (preference == mEnableCharacterModifyPreference) {
            Log.d(TAG, "onPreferenceChange: ***** cmf1");
            if (mEnableCharacterModifyPreference.isChecked() != (Boolean) newValue) {
                boolean value = (Boolean) (newValue);
                mEnableCharacterModifyPreference.setChecked(value);
                editor.putBoolean(ENABLE_CHARACTER_MODIFY,value);
                mCharacterGSPreference.setEnabled(value);
            }
        } else if (preference == mCharacterGSPreference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
            String value = (String) (newValue);
            mCharacterGSPreference.setValue(value);
            mCharacterGSPreference.setSummary("%".equals(value)?"%%":value);
            editor.putString(CHARACTER_GS,value);
        } else if (preference == mTimeOutPreference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue"+newValue);
            String value = (String) (newValue);
            int scanTime = Integer.parseInt(value);
            mTimeOutPreference.setValue(value);
            updateTimeoutPreferenceSummary(scanTime);
            editor.putString(KEY_TIMEOUT,value);
            ScanNative.setTimeout(scanTime);
        } else if (preference == mDataReceivePreference) {
            Log.d(TAG, "mDataReceivePreference: ***** newValue"+newValue);
            String value = (String) (newValue);
            mDataReceivePreference.setValue(value);
            mDataReceivePreference.setSummary(value);
            mKbdTypingDeleyPreference.setEnabled("KEYBOARD_EVENT".equals(value));
            mUserDefinitionPreference.setEnabled("KEYBOARD_EVENT".equals(value)
                    && "1".equals(mKbdTypingDeleyPreference.getValue()));
            editor.putString(DATA_RECEIVE_KEY,value);
        } else if (preference == mActionNamePreference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue" + newValue);
            String value = (String) (newValue);
            mActionNamePreference.setText(value);
            mActionNamePreference.setSummary(value);
            editor.putString(ACTION_NAME_KEY, value);
        } else if (preference == mBarcodeDataPreference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue" + newValue);
            String value = (String) (newValue);
            mBarcodeDataPreference.setText(value);
            mBarcodeDataPreference.setSummary(value);
            editor.putString(BARCODE_DATA_KEY, value);
        } else if (preference == mSymbologyTypePreference) {
            Log.d(TAG, "onPreferenceChange: ***** newValue" + newValue);
            String value = (String) (newValue);
            mSymbologyTypePreference.setText(value);
            mSymbologyTypePreference.setSummary(value);
            editor.putString(SYMBOLOGY_TYPE_KEY, value);
        }else if (preference == mEnableSettingLockPreference) {
            Log.d(TAG, "onPreferenceChange: ***** cmf1");
            boolean value = (Boolean) (newValue);
            String password  = mShare.getString(CHANGE_PASSWORD, "0000");
            checkPassword(password,value);
        }
        editor.commit();
        return false;
    }

    private void updateTimeoutPreferenceSummary(long currentTimeout) {
        String summary;
        if (currentTimeout < 0) {
            // Unsupported value
            summary = "";
        } else {
            final CharSequence[] entries = mTimeOutPreference.getEntries();
            final CharSequence[] values = mTimeOutPreference.getEntryValues();
            if (entries == null || entries.length == 0) {
                summary = "";
            } else {
                int best = 0;
                for (int i = 0; i < values.length; i++) {
                    long timeout = Long.parseLong(values[i].toString());
                    if (currentTimeout >= timeout) {
                        best = i;
                    }
                }
                summary = entries[best].toString();
            }
        }
        mTimeOutPreference.setSummary(summary);
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

    private void checkPassword(final String password, final boolean newValue){
        final EditText et = new EditText(mContext);
        new AlertDialog.Builder(mContext).setTitle(R.string.check_password)
        .setView(et)
        .setPositiveButton(R.string.change_password_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String input = et.getText().toString();
                    if (input.equals(password)) {
                        mEnableSettingLockPreference.setChecked(newValue);
                        editor.putBoolean(ENABLE_SETTING_LOCK,newValue);
                        editor.commit();
                    }else {
                        Toast.makeText(mContext, R.string.wrong_password_tips, Toast.LENGTH_SHORT).show();
                    }
                }
        })
        .setNegativeButton(R.string.change_password_cancel, null)
        .show();
    }

    private void changePassword(final String password){
        final EditText et = new EditText(mContext);
        new AlertDialog.Builder(mContext).setTitle(R.string.current_password)
        .setView(et)
        .setPositiveButton(R.string.change_password_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String input = et.getText().toString();
                    if (input.equals(password)) {
                        newPassword();
                    }else {
                        Toast.makeText(mContext, R.string.wrong_password_tips, Toast.LENGTH_SHORT).show();
                    }
                }
        })
        .setNegativeButton(R.string.change_password_cancel, null)
        .show();
    }

    private void newPassword(){
        final EditText et = new EditText(mContext);
        et.setKeyListener(new NumberKeyListener(){

            protected char[] getAcceptedChars(){
                char[] numberChars ={'1','2','3','4','5','6','7','8','9','0','a','b','c',
                            'd','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s',
                            't','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I',
                            'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
                return numberChars;
            }

            @Override
            public int getInputType() {
                // TODO Auto-generated method stub
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
    	});
        et.setHint(R.string.new_password_tips);
        new AlertDialog.Builder(mContext).setTitle(R.string.new_password)
        .setView(et)
        .setPositiveButton(R.string.change_password_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String input = et.getText().toString();
                    editor.putString(CHANGE_PASSWORD, input);
                    editor.commit();
                }
        })
        .setNegativeButton(R.string.change_password_cancel, null)
        .show();
    }
}
