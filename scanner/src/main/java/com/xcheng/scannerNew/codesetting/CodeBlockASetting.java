package com.xcheng.scannerNew.codesetting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.xcheng.scannerNew.R;

public class CodeBlockASetting extends BaseSetting {

    private static final String TAG = "CodeBlockASetting";

    private static final String COMMAND_MAX_LENGTH = "922002";
    private static final String COMMAND_MIN_LENGTH = "922003";

    private static final String KEY_MIN = "codeblocka_min";
    private static final String KEY_MAX = "codeblocka_max";

    private static final int MAX_LENGTH = 600;
    private static final int MIN_LENGTH = 1;

    private Preference mMaxPref;
    private Preference mMinPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                createMaxLengthDialog(CodeBlockASetting.this, preference, maxTitle,
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
                createMinLengthDialog(CodeBlockASetting.this, preference, minTitle,
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
