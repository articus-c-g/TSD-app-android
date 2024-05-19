package com.xcheng.scannerNew.codesetting;

import android.os.Bundle;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import com.xcheng.scannerNew.R;

public class Code11Setting extends BaseSetting {

    private static final String TAG = "Code11Setting";

    private static final String COMMAND_CHECK_CHAR = "908001";
    private static final String COMMAND_MAX_LENGTH = "908003";
    private static final String COMMAND_MIN_LENGTH = "908004";

    private static final String KEY_CHAR = "code11_char";
    private static final String KEY_MIN = "code11_min";
    private static final String KEY_MAX = "code11_max";

    private static final int MAX_LENGTH = 80;
    private static final int MIN_LENGTH = 1;
    private static final int DFAULT_MIN_LENGTH = 4;

    private ListPreference mDigitChekPref;
    private Preference mMaxPref;
    private Preference mMinPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDigitChekPref = (ListPreference) createPreference(this, TYPE_LIST,
                KEY_CHAR, getString(R.string.check_digit), COMMAND_CHECK_CHAR);
        mDigitChekPref.setEntries(R.array.check_digit_entries);
        mDigitChekPref.setEntryValues(R.array.check_digit_values);
        mDigitChekPref.setDialogTitle(R.string.check_digit);
        int char_value = BaseSetting.getSharedPreferences(this).getInt(KEY_CHAR, -1);
        if (char_value != -1){
            mDigitChekPref.setValueIndex(char_value);
            updateListPreferenceSummary(mDigitChekPref, char_value);
        }
        mDigitChekPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                String command = COMMAND_CHECK_CHAR + (String) objValue;
                Editor editor = BaseSetting.getSharedPreferences(Code11Setting.this).edit();
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
        }
        mMaxPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preference.setKey(KEY_MAX);
                createMaxLengthDialog(Code11Setting.this, preference, maxTitle,
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
            mMinPref.setSummary(String.valueOf(DFAULT_MIN_LENGTH));
        }
        mMinPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preference.setKey(KEY_MIN);
                createMinLengthDialog(Code11Setting.this, preference, minTitle,
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
