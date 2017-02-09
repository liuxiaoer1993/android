package com.ls.sportactivity;

import com.ls.Util.SpeakUtil;

/**
 * Created by qwe on 2016/5/6.
 */
public class TimeNotifier implements UpdateListener,SpeakTimer.Listener {

    public interface Listener{
        void timeChanged(String time);
    }

    Listener mListener;

    private String time;
    private SpeakUtil mUtil;
    private SportSetting mSetting;

    public TimeNotifier(SpeakUtil util,Listener listener,SportSetting setting){
        mUtil = util;
        mListener = listener;
        mSetting = setting;
    }

    public void setTime(String time){
        this.time = time;
    }

    private void notifyListener(){
        mListener.timeChanged(time);
    }

    @Override
    public void onUpdate() {
        notifyListener();
    }

    @Override
    public void speak() {
        mUtil.say("it is" + time + "minutes");
    }
}
