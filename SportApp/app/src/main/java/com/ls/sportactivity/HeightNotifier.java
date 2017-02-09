package com.ls.sportactivity;

import com.ls.Util.SpeakUtil;

/**
 * Created by qwe on 2016/5/6.
 */
public class HeightNotifier implements UpdateListener,SpeakTimer.Listener {

    interface Listener{
        void heightChanged(double height);
    }

    Listener mListener;

    private double height;
    private SpeakUtil mUtil;
    private SportSetting mSetting;

    public HeightNotifier(SpeakUtil util,Listener listener,SportSetting setting){
        mUtil = util;
        mListener = listener;
        mSetting = setting;
    }

    public void setHeight(double height){
        this.height = height;
    }

    private void notifyListener(){
        mListener.heightChanged(height);
    }

    @Override
    public void onUpdate() {
        notifyListener();
    }

    @Override
    public void speak() {
        if(mSetting.shouldTellHeight()){
            mUtil.say("height is " + height + " meters");
        }
    }
}
