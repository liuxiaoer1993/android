package com.ls.Util;

import com.ls.sportactivity.SpeakTimer;
import com.ls.sportactivity.SportActivity;
import com.ls.sportactivity.SportSetting;
import com.ls.sportactivity.TimeNotifier;

import java.util.concurrent.TimeUnit;

/**
 * Created by qwe on 2016/4/25.
 */
public class TimerUtil implements Runnable,SpeakTimer.Listener {

    private static long total_second = 1;
    private  String total_time = "";
    private int hour = 0;
    private int minute = 0;
    private int second = 0;

    private SpeakUtil mUtil;
    private TimeNotifier timeNotifier;
    private SportSetting mSetting;

    public interface TimeCallback{
        void onTimeChanged(String time);
    }

    TimeCallback timeCallback;

    public TimerUtil(SpeakUtil util,SportSetting setting){
        mUtil = util;
        mSetting = setting;
    }

    public void registerCallback(TimeCallback callback){
        timeCallback = callback;
    }

    public void setTimeNotifier(){
        timeNotifier = new TimeNotifier(mUtil,listener,mSetting);
    }

    TimeNotifier.Listener listener = new TimeNotifier.Listener() {
        @Override
        public void timeChanged(String time) {
            timeCallback.onTimeChanged(time);
        }
    };

    @Override
    public void speak() {
        if(mSetting.shouldTellTime()){
            int m = (int)total_second / 60;
            int s = (int)total_second % 60;
            mUtil.say(m + "minutes" + s + "seconds cost");
        }
    }

    @Override
    public void run(){
        while(true){
            while(!SportActivity.getIsStopSport()){
                try{
                    TimeUnit.SECONDS.sleep(1);
                    total_second ++;
                    second ++;
                    if(second == 60){
                        second = 0;
                        minute++;
                        if (minute == 60){
                            minute = 0;
                            hour++;
                        }
                    }
                    total_time = (hour==0?"00":String.format("%02d",hour))+":"+
                            (minute==0?"00":String.format("%02d",minute))+":"+
                            (second==0?"00":String.format("%02d",second));

                    listener.timeChanged(total_time);

                }catch (InterruptedException e){

                }
            }
        }
    }

    public static long getTotal_second(){
        return total_second;
    }

    public static void setTotal_second(){
        total_second = 0;
    }

//    public static String getTotal_time(){
//        return total_time;
//    }
//
//    public static void setTotal_time(){
//        total_time = "";
//    }
}
