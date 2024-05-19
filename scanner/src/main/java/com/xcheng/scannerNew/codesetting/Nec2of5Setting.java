package com.xcheng.scannerNew.codesetting;

import android.os.Bundle;
import android.content.SharedPreferences.Editor;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import com.xcheng.scannerNew.R;

public class Nec2of5Setting extends BaseSetting {

    private static final String TAG = "Nec2of5Setting";

    private static final String COMMAND_CHAR_SETTING = "903002";
    private static final String COMMAND_MAX_LENGTH = "903003";
    private static final String COMMAND_MIN_LENGTH = "903004";

    private static final String KEY_CHECKBOX = "Nec2of5_checkbox";
    private static final String KEY_MIN = "Nec2of5_min";
    private static final String KEY_MAX = "Nec2of5_max";

    private static final int MAX_LENGTH = 80;
    private static final int MIN_LENGTH = 2;
    private static final int DFAULT_MIN_LENGTH = 4;

    private SwitchPreference mCharSettingPref;
    private Preference mMaxPref;
    private Preference mMinPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCharSettingPref = (SwitchPreference) createPreference(this, TYPE_SWTICH,
                KEY_CHECKBOX, getString(R.string.char_setting), COMMAND_CHAR_SETTING);
        mCharSettingPref.setChecked(BaseSetting.getSharedPreferences(this).getInt(KEY_CHECKBOX, -1) == 1);
        mCharSettingPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object objValue) {
                boolean checked = (Boolean) objValue;
                Editor editor = BaseSetting.getSharedPreferences(Nec2of5Setting.this).edit();
                editor.putInt(KEY_CHECKBOX, checked ? 1 : 0);
                editor.commit();
                String newCommand = COMMAND_CHAR_SETTING + (checked ? "1" : "0");
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
                createMaxLengthDialog(Nec2of5Setting.this, preference, maxTitle,
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
                createMinLengthDialog(Nec2of5Setting.this, preference, minTitle,
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
