package com.xcheng.scannerNew;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.M)
public class SettingDetailFragment extends PreferenceFragment {

    private static final String TAG = "SettingDetailFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.scan_setting_detail);
    }

}
