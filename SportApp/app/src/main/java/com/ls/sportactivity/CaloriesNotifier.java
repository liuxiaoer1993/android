package com.ls.sportactivity;

import com.ls.Util.SpeakUtil;

/**
 * Created by qwe on 2016/5/6.
 */
public class CaloriesNotifier implements UpdateListener,SpeakTimer.Listener {

    interface Listener{
        void caloriesChanged(double calories);
    }

    Listener mListener;

    private double calories = 0;
    private SpeakUtil mUtil;
    private SportSetting mSetting;

    public CaloriesNotifier(SpeakUtil util,Listener listener,SportSetting setting){
        mUtil = util;
        mListener = listener;
        mSetting = setting;
    }

    public void setCalories(double calories){
        this.calories = calories;
    }

    private void notifyListener(){
        mListener.caloriesChanged(calories);
    }

    @Override
    public void onUpdate() {
        notifyListener();
    }

    @Override
    public void speak() {
        if(mSetting.shouldTellCalories()){
            mUtil.say((int)calories + " calories burned");
        }
    }
}
