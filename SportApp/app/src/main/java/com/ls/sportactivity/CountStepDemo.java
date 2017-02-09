package com.ls.sportactivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.ls.Util.SpeakUtil;

import java.util.ArrayList;

/**
 * Created by qwe on 2016/5/4.
 */
public class CountStepDemo implements SensorEventListener,SpeakTimer.Listener{

    private static final int THRESHOLD = 18;  //垂直差值
    private static final int MAX_STEP_DISTANCE = 40;  //最大步长
    private static final int MIN_STEP_DISTANCE = 10;  //最短步长
    private static final int BUFFER_NUMBER = 40;   //数据的缓存长度
    private static final int FILTER_NUMBER = 8;  //滤波的缓存数据长度

    private ArrayList<Double>accelerate_array;
    private ArrayList<Double>accelerate_average;


    private int peak = 0;
    private int step_number = 0;
    private double accelerate = 0;

    private StepNotifier stepNotifier;
    private SpeakUtil mUtil;
    private SportSetting mSetting;

    interface StepCallback{
        void onStepChanged(int step);
    }

    StepCallback stepCallback;

    public void registerCallback(StepCallback callback){
        stepCallback = callback;
    }

    public CountStepDemo(SpeakUtil util,SportSetting setting){
        accelerate_array = new ArrayList<>();
        accelerate_average = new ArrayList<>();
        mUtil = util;
        mSetting = setting;
    }

    public void setStepNotifier(){
        stepNotifier = new StepNotifier(mUtil,listener,mSetting);
    }

    StepNotifier.Listener listener = new StepNotifier.Listener() {
        @Override
        public void stepChanged(int step) {
            stepCallback.onStepChanged(step);
        }
    };

    @Override
    public void speak() {
        if(mSetting.shouldTellStep()){
            mUtil.say(step_number + " steps");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        accelerate=Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]
                +event.values[2]*event.values[2]);
        double data=accelerate * 10;
        waveFilter(data);
    }

    private int statisticsPeakNumber(ArrayList<Double>step_data){
        ArrayList<Double>gradient = new ArrayList<>();
        ArrayList<Integer>peak_position = new ArrayList<>();
        ArrayList<Integer>valley_position = new ArrayList<>();
        double threshold = 0;
        int distance = 0;
        peak = 0;

        for(int i = 0;i < BUFFER_NUMBER - 1;i++)
            gradient.add(step_data.get(i+1) - step_data.get(i));

        for(int i = 0;i < gradient.size() - 1;i++){
            if(gradient.get(i) > 0 && gradient.get(i+1) < 0)
                peak_position.add(i+1);
            if(gradient.get(i) < 0 && gradient.get(i+1) > 0)
                valley_position.add(i+1);

        }

        if((peak_position.size() == 0 || valley_position.size() == 0)
                && !(peak_position.size() == 0 && valley_position.size() == 0)){
            if(peak_position.size() == 0){
                Pair p = getMax(step_data);
                threshold = p.getData() - step_data.get(valley_position.get(0));
                distance = Math.abs(p.getPosition() - valley_position.get(0));
                if(threshold >= THRESHOLD && distance < MAX_STEP_DISTANCE && distance > MIN_STEP_DISTANCE)
                    peak ++;
            }
            if(valley_position.size() == 0){
                Pair p = getMin(step_data);
                threshold = step_data.get(peak_position.get(0)) - p.getData();
                distance = Math.abs(p.getPosition() - peak_position.get(0));
                if(threshold >= THRESHOLD && distance < MAX_STEP_DISTANCE && distance > MIN_STEP_DISTANCE)
                    peak ++;
            }
        }

        if(peak_position.size() == 0 && valley_position.size() == 0){
            Pair p0 = getMax(step_data);
            Pair p1 = getMin(step_data);

            threshold = p0.getData() - p1.getData();
            distance = Math.abs(p0.getPosition() - p1.getPosition());
            if(threshold >= THRESHOLD && distance < MAX_STEP_DISTANCE && distance > MIN_STEP_DISTANCE)
                peak ++;
        }

        if(peak_position.size() != valley_position.size() && (peak_position.size() != 0 && valley_position.size() != 0)){
            int flag = 0;
            while(peak_position.size() > flag && valley_position.size() > flag){
                distance=Math.abs(peak_position.get(flag)-valley_position.get(flag));
                threshold=step_data.get(peak_position.get(flag))-step_data.get(valley_position.get(flag));
                if(distance>MIN_STEP_DISTANCE && distance<MAX_STEP_DISTANCE &&
                        threshold>THRESHOLD){
                    peak++;
                }
                flag++;
            }
            threshold=step_data.get(peak_position.get(peak_position.size() - 1))-step_data.get(
                    valley_position.get(valley_position.size() - 1));
            distance=Math.abs(peak_position.get(peak_position.size() - 1)-valley_position.get(valley_position.size() - 1));
            if(distance>MIN_STEP_DISTANCE && distance<MAX_STEP_DISTANCE &&
                    threshold>THRESHOLD){
                peak++;
            }
        }

        if(peak_position.size()==valley_position.size())
        {
            int flag = 0;
            while(flag<peak_position.size() && flag<valley_position.size()){
                distance=Math.abs(peak_position.get(flag)-valley_position.get(flag));
                threshold=step_data.get(peak_position.get(flag))-step_data.get(valley_position.get(flag));
                if(distance>MIN_STEP_DISTANCE && distance<MAX_STEP_DISTANCE &&
                        threshold>THRESHOLD){
                    peak++;
                }
                flag++;
            }
        }

        return  peak;
    }

    private void waveFilter(double accelerate){
        double sum = 0;
        double average = 0;
        accelerate_array.add(accelerate);
        if(accelerate_array.size() == FILTER_NUMBER){
            for(int i = 0;i < FILTER_NUMBER;i++){
                sum += accelerate_array.get(i);
            }
            average = sum / FILTER_NUMBER;
            accelerate_average.add(average);
            if(accelerate_average.size() == BUFFER_NUMBER){
                step_number += statisticsPeakNumber(accelerate_average);
                listener.stepChanged(step_number);
                accelerate_average.clear();
            }
            accelerate_array.remove(0);
        }
    }

    private Pair getMax(ArrayList<Double>step_data){
        Pair p = new Pair();
        int position = 0;
        double max = 0;
        for(int i = 1;i < step_data.size();i++){
            max = step_data.get(0);
            if(max < step_data.get(i)){
                max = step_data.get(i);
                position = i;
            }
        }

        p.setData(max);
        p.setPosition(position);

        return p;
    }

    private Pair getMin(ArrayList<Double>step_data){
        Pair p = new Pair();
        int position = 0;
        double min = 0;
        for(int i = 1;i < step_data.size();i++){
            min = step_data.get(0);
            if(min > step_data.get(i)){
                min = step_data.get(i);
                position = i;
            }
        }

        p.setData(min);
        p.setPosition(position);

        return p;
    }

    class Pair{
        private double data;
        private int position;

        public void setData(double data) {
            this.data = data;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public double getData(){
            return data;
        }

        public int getPosition(){
            return position;
        }
    }
}
