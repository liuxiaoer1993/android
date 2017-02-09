package com.ls.sportactivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.ls.sportapp.R;

/**
 * Created by qwe on 2016/5/10.
 */
public class Setting extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstancedState){
        super.onCreate(savedInstancedState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
