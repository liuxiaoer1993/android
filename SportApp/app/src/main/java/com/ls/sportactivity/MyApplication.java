package com.ls.sportactivity;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.map.MapView;

/**
 * Created by qwe on 2016/6/15.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private MapView baiduMapView;

    public void setBaiduMapView(MapView view){
        baiduMapView = view;
    }

    public MapView getBaiduMapView(){
        if(baiduMapView != null){
            return baiduMapView;
        }
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication onCreate()");
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        Log.d(TAG, "MyApplication onLowMemory()");
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        Log.d(TAG, "MyApplication onTerminate()");
    }

    public void onTrimMemory(int level){
        super.onTrimMemory(level);
        Log.d(TAG, "MyApplication onTrimMemory() "+level);
    }
}
