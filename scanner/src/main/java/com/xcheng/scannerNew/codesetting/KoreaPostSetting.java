package com.xcheng.scannerNew.codesetting;

import android.os.Bundle;
import android.content.SharedPreferences.Editor;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import com.xcheng.scannerNew.R;

public class KoreaPostSetting extends BaseSetting {

    private static final String TAG = "KoreaPostSetting";

    private static final String COMMAND_CHECK_DIGIT = "937004";
    private static final String COMMAND_MAX_LENGTH = "937002";
    private static final String COMMAND_MIN_LENGTH = "937003";

    private static final String KEY_CHECKBOX = "KoreaPost_checkbox";
    private static final String KEY_MIN = "KoreaPost_min";
    private static final String KEY_MAX = "KoreaPost_max";

    private static final int MAX_LENGTH = 80;
    private static final int MIN_LENGTH = 2;
    private static final int DFAULT_MIN_LENGTH = 4;

    private SwitchPreference mCheckDigitPref;
    private Preference mMaxPref;
    private Preference mMinPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCheckDigitPref = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX, getString(R.string.check_digit), COMMAND_CHECK_DIGIT);
        mCheckDigitPref.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX, -1) == 1);
        mCheckDigitPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(KoreaPostSetting.this).edit();
                editor.putInt(KEY_CHECKBOX, checked ? 1 : 0);
                editor.commit();
                String newCommand = COMMAND_CHECK_DIGIT + (checked ? "1" : "0");
                sendCommand(newCommand);
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
                createMaxLengthDialog(KoreaPostSetting.this, preference, maxTitle,
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
                createMinLengthDialog(KoreaPostSetting.this, preference, minTitle,
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
