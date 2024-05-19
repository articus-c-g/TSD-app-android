package com.xcheng.scannerNew.codesetting;

import android.os.Bundle;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import com.xcheng.scannerNew.R;

public class MsiPlesseySetting extends BaseSetting {

    private static final String TAG = "MsiPlesseySetting";

    private static final String COMMAND_CHECK_CHAR = "917002";
    private static final String COMMAND_MAX_LENGTH = "917003";
    private static final String COMMAND_MIN_LENGTH = "917004";

    private static final String KEY_CHAR = "MsiPlessey_char";
    private static final String KEY_MIN = "MsiPlessey_min";
    private static final String KEY_MAX = "MsiPlessey_max";

    private static final int MAX_LENGTH = 48;
    private static final int MIN_LENGTH = 4;

    private ListPreference mMsiChekPref;
    private Preference mMaxPref;
    private Preference mMinPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMsiChekPref = (ListPreference) createPreference(this, TYPE_LIST,
                KEY_CHAR, getString(R.string.msi_check_chars), COMMAND_CHECK_CHAR);
        mMsiChekPref.setEntries(R.array.msi_check_chars_entries);
        mMsiChekPref.setEntryValues(R.array.msi_check_chars_values);
        mMsiChekPref.setDialogTitle(R.string.msi_check_chars);
        int char_value = BaseSetting.getSharedPreferences(this).getInt(KEY_CHAR, -1);
        if (char_value != -1){
            mMsiChekPref.setValueIndex(char_value);
            updateListPreferenceSummary(mMsiChekPref, char_value);
        }
        mMsiChekPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                String command = COMMAND_CHECK_CHAR + (String) objValue;
                Editor editor = BaseSetting.getSharedPreferences(MsiPlesseySetting.this).edit();
                editor.putInt(KEY_CHAR, new Integer((String) objValue));
                editor.commit();
                sendCommand(command);
                updateListPreferenceSummary((ListPreference)preference,
                        Long.parseLong((String) objValue));
                return true;
            }
        });

        final String maxTitle = getString(R.string.setting_max_length) + " (" + MIN_LENGTH + "-" + MAX_LENGTH + ")";
        mMaxPref = createPreference(this, TYPE_PREFERENCE, KEY_MAX, maxTitle, COMMAND_MAX_LENGTH);
        int maxValue = BaseSetting.getSharedPreferences(this).getInt(KEY_MAX, -1);
        if (maxValue != -1){
            mMaxPref.setSummary(Integer.toString(maxValue));
        } else {
            mMaxPref.setSummary(String.valueOf(MAX_LENGTH));
        }
        mMaxPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preference.setKey(KEY_MAX);
                createMaxLengthDialog(MsiPlesseySetting.this, preference, maxTitle,
                        MAX_LENGTH, MIN_LENGTH, COMMAND_MAX_LENGTH);
                return true;
            }
        });

        final String minTitle = getString(R.string.setting_min_length) + " (" + MIN_LENGTH + "-" + MAX_LENGTH + ")";
        mMinPref = createPreference(this, TYPE_PREFERENCE, KEY_MIN, minTitle, COMMAND_MIN_LENGTH);
        int minValue = BaseSetting.getSharedPreferences(this).getInt(KEY_MIN, -1);
        if (minValue != -1){
            mMinPref.setSummary(Integer.toString(minValue));
        } else {
            mMinPref.setSummary(String.valueOf(MIN_LENGTH));
        }
        mMinPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preference.setKey(KEY_MIN);
                createMinLengthDialog(MsiPlesseySetting.this, preference, minTitle,
                        MAX_LENGTH, MIN_LENGTH, COMMAND_MIN_LENGTH);
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
