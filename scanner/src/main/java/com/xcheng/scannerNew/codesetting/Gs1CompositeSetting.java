package com.xcheng.scannerNew.codesetting;

import android.os.Bundle;
import android.content.SharedPreferences.Editor;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import com.xcheng.scannerNew.R;

public class Gs1CompositeSetting extends BaseSetting {

    private static final String TAG = "Gs1CompositeSetting";

    private static final String COMMAND_VERSION = "926005";
    private static final String COMMAND_MAX_LENGTH = "926003";
    private static final String COMMAND_MIN_LENGTH = "926004";

    private static final String KEY_CHECKBOX = "Gs1Composite_checkbox";
    private static final String KEY_MIN = "Gs1Composite_min";
    private static final String KEY_MAX = "Gs1Composite_max";

    private static final int MAX_LENGTH = 2435;
    private static final int MIN_LENGTH = 1;

    private SwitchPreference mVersionPref;
    private Preference mMaxPref;
    private Preference mMinPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVersionPref = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX, getString(R.string.upc_ean_version), COMMAND_VERSION);
        mVersionPref.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX, -1) == 1);
        mVersionPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(Gs1CompositeSetting.this).edit();
                editor.putInt(KEY_CHECKBOX, checked ? 1 : 0);
                editor.commit();
                String newCommand = COMMAND_VERSION + (checked ? "1" : "0");
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
                createMaxLengthDialog(Gs1CompositeSetting.this, preference, maxTitle,
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
                createMinLengthDialog(Gs1CompositeSetting.this, preference, minTitle,
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
