package com.ls.sportactivity;


import android.util.Log;

import com.ls.Util.SpeakUtil;

import java.text.DecimalFormat;

/**
 * Created by qwe on 2016/5/6.
 */
public class DistanceNotifier implements UpdateListener,SpeakTimer.Listener{

    interface Listener{
        void distanceChanged(double distance);
    }

    Listener mListener;

    private SpeakUtil mUtil;
    private double mDistance = 0;
    private SportSetting mSetting;
    private DecimalFormat dcmFmt = new DecimalFormat("0.00");

    public DistanceNotifier(SpeakUtil util,Listener listener,SportSetting setting){
        mUtil = util;
        mListener = listener;
        mSetting = setting;
    }

    public void setDistance(double distance){
        mDistance = distance;
    }

    @Override
    public void onUpdate(){
        notifyListener();
    }

    @Override
    public void speak() {
        if(mSetting.shouldTellDistance()){
            mUtil.say(dcmFmt.format(mDistance) + "kilometers");
        }
    }

    private void notifyListener(){
        mListener.distanceChanged(mDistance);
    }
}
