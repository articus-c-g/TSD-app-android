package com.xcheng.scannerNew.codesetting;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;

import com.xcheng.scannerNew.R;
public class EanJan13Setting extends BaseSetting {

    private static final String TAG = "EanJan13Setting";

    private static final String COMMAND_CHECK_DIGIT = "915002";
    private static final String COMMAND_DIGIT_2 = "915003";
    private static final String COMMAND_DIGIT_5 = "915004";
    private static final String COMMAND_REQUIRED = "915005";
    private static final String COMMAND_ADD_SEPARATOR = "915006";
    private static final String COMMAND_ISBN = "915007";

    private static final String KEY_CHECKBOX_DIGIT = "EanJan13_digit";
    private static final String KEY_CHECKBOX_DIGIT2 = "EanJan13_digit2";
    private static final String KEY_CHECKBOX_DIGIT5 = "EanJan13_digit5";
    private static final String KEY_CHECKBOX_REQUIRED = "EanJan13_Required";
    private static final String KEY_CHECKBOX_SEPARATOR = "EanJan13_Separator";
    private static final String KEY_CHECKBOX_ISBN = "EanJan13_ISBN";

    private SwitchPreference mCheckDigit;
    private SwitchPreference mDigit2;
    private SwitchPreference mDigit5;
    private SwitchPreference mRequired;
    private SwitchPreference mAddSeparator;
    private SwitchPreference mISBN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCheckDigit = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX_DIGIT, getString(R.string.check_digit), COMMAND_CHECK_DIGIT);
        mCheckDigit.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX_DIGIT, -1) == 1);
        mCheckDigit.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(EanJan13Setting.this).edit();
                editor.putInt(KEY_CHECKBOX_DIGIT, checked ? 1 : 0);
                editor.commit();
                String newCommand = COMMAND_CHECK_DIGIT + (checked ? "1" : "0");
                sendCommand(newCommand);
                return true;
            }
        });

        mDigit2 = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX_DIGIT2, getString(R.string.digit_addenda_2), COMMAND_DIGIT_2);
        mDigit2.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX_DIGIT2, -1) == 0);
        mDigit2.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(EanJan13Setting.this).edit();
                editor.putInt(KEY_CHECKBOX_DIGIT2, checked ? 0 : 1);
                editor.commit();
                String newCommand = COMMAND_DIGIT_2 + (checked ? "0" : "1");
                sendCommand(newCommand);
                return true;
            }
        });

        mDigit5 = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX_DIGIT5, getString(R.string.digit_addenda_5), COMMAND_DIGIT_5);
        mDigit5.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX_DIGIT5, -1) == 0);
        mDigit5.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(EanJan13Setting.this).edit();
                editor.putInt(KEY_CHECKBOX_DIGIT5, checked ? 0 : 1);
                editor.commit();
                String newCommand = COMMAND_DIGIT_5 + (checked ? "0" : "1");
                sendCommand(newCommand);
                return true;
            }
        });

        mRequired = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX_REQUIRED, getString(R.string.addenda_required), COMMAND_REQUIRED);
        mRequired.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX_REQUIRED, -1) == 1);
        mRequired.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(EanJan13Setting.this).edit();
                editor.putInt(KEY_CHECKBOX_REQUIRED, checked ? 1 : 0);
                editor.commit();
                String newCommand = COMMAND_REQUIRED + (checked ? "1" : "0");
                sendCommand(newCommand);
                return true;
            }
        });

        mAddSeparator = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX_SEPARATOR, getString(R.string.addenda_add_separator), COMMAND_ADD_SEPARATOR);
        mAddSeparator.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX_SEPARATOR, -1) == 0);
        mAddSeparator.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(EanJan13Setting.this).edit();
                editor.putInt(KEY_CHECKBOX_SEPARATOR, checked ? 0 : 1);
                editor.commit();
                String newCommand = COMMAND_ADD_SEPARATOR + (checked ? "0" : "1");
                sendCommand(newCommand);
                return true;
            }
        });

        mISBN = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX_ISBN, getString(R.string.isbn_translate), COMMAND_ISBN);
        mISBN.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX_ISBN, -1) == 1);
        mISBN.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(EanJan13Setting.this).edit();
                editor.putInt(KEY_CHECKBOX_ISBN, checked ? 1 : 0);
                editor.commit();
                String newCommand = COMMAND_ISBN + (checked ? "1" : "0");
                sendCommand(newCommand);
                return true;
            }
        });
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
}