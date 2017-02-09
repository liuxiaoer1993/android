package com.ls.sportactivity;

import com.ls.Util.SpeakUtil;

import java.text.DecimalFormat;

/**
 * Created by qwe on 2016/5/6.
 */
public class SpeedNotifier implements UpdateListener,SpeakTimer.Listener {

    interface Listener{
        void speedChanged(double speed);
    }

    Listener mListener;

    private double speed = 0;
    private SpeakUtil mUtil;
    private SportSetting mSetting;
    private DecimalFormat dcmFmt = new DecimalFormat("0.00");

    public SpeedNotifier(SpeakUtil util,Listener listener,SportSetting setting){
        mUtil = util;
        mListener = listener;
        mSetting = setting;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    @Override
    public void onUpdate(){
        notifyListener();
    }

    @Override
    public void speak() {
        if(mSetting.shouldTellSpeed()){
            mUtil.say(dcmFmt.format(speed) + " kilometers per hour");
        }
    }

    private void notifyListener(){
        mListener.speedChanged(speed);
    }
}
