package com.ls.sportactivity;

import com.ls.Util.SpeakUtil;

/**
 * Created by qwe on 2016/5/6.
 */
public class StepNotifier implements UpdateListener,SpeakTimer.Listener {

    interface Listener{
        void stepChanged(int step);
    }

    Listener mListener;

    private int step;
    private SpeakUtil mUtil;
    private SportSetting mSetting;

    public StepNotifier(SpeakUtil util,Listener listener,SportSetting setting){
        mUtil = util;
        mListener = listener;
        mSetting = setting;
    }

    public void setStep(int step){
        this.step = step;
    }

    private void notifyListener(){
        mListener.stepChanged(step);
    }

    @Override
    public void onUpdate() {
        notifyListener();
    }

    @Override
    public void speak() {
        mUtil.say(step + " steps");
    }
}
