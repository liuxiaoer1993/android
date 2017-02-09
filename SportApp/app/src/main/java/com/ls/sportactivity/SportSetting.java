package com.ls.sportactivity;

import android.content.SharedPreferences;

/**
 * Created by qwe on 2016/5/10.
 */
public class SportSetting {

    SharedPreferences mSetting;

    public SportSetting(SharedPreferences setting){
        mSetting = setting;
    }

    public boolean getIsSpeak(){
        return mSetting.getBoolean("speak",false);
    }

    public double getBodyWeight(){
        return Double.parseDouble(mSetting.getString("body_weight","50"));
    }

    public double getSpeakRace(){
        return Double.parseDouble(mSetting.getString("speak_race","1"));
    }

    public boolean shouldTellDistance(){
        return mSetting.getBoolean("tell_distance",false);
    }

    public boolean shouldTellTime(){
        return mSetting.getBoolean("tell_time",false);
    }

    public boolean shouldTellSpeed(){
        return mSetting.getBoolean("tell_speed",false);
    }

    public boolean shouldTellStep(){
        return mSetting.getBoolean("tell_step",false);
    }

    public boolean shouldTellCalories(){
        return mSetting.getBoolean("tell_calories",false);
    }

    public boolean shouldTellHeight(){
        return mSetting.getBoolean("tell_height",false);
    }
}
