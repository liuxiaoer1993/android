package com.ls.sportactivity;

import android.util.Log;

import com.ls.Util.SpeakUtil;

import java.util.ArrayList;

/**
 * Created by qwe on 2016/5/6.
 */
public class SpeakTimer implements UpdateListener {

    SpeakUtil mUtil;
    private ArrayList<Listener>mListeners;
    private long mLastSpeakTime;
    private double mInterval;
    private boolean shouldSpeak = false;
    private SportSetting mSetting;

    public SpeakTimer(SpeakUtil util,SportSetting setting){
        mLastSpeakTime = System.currentTimeMillis();
        mUtil = util;
        mSetting = setting;
        mListeners = new ArrayList<>();
        mInterval = mSetting.getSpeakRace();
        shouldSpeak = mSetting.getIsSpeak();
        reloadSetting();
    }

    @Override
    public void onUpdate(){
        long now = System.currentTimeMillis();
        long delta = now - mLastSpeakTime;
        if(delta / 60000.0 >= mInterval && shouldSpeak){
            notifyListeners();
            mLastSpeakTime = now;
        }
    }

    public void reloadSetting(){
        shouldSpeak = mSetting.getIsSpeak();
        mInterval = mSetting.getSpeakRace();
    }

    public interface Listener{
        void speak();
    }

    public void addListener(Listener l){
        mListeners.add(l);
    }

    private void notifyListeners(){
        for(Listener listener : mListeners) {
            listener.speak();
        }

    }

}
