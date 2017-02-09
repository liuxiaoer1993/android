package com.ls.sportactivity;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.ls.Util.SpeakUtil;
import com.ls.Util.TimerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SportService extends Service {

    private final String TAG = "SportService";

    private static BaiduMap baidumap;
    private LocationClient locationClient;
    private MyLocationListener locationListener;

    private boolean isFirstLoc=true;  //是否为第一次定位
    /*
     *轨迹绘制相关
     */
    private ArrayList<Double> latitude_points = new ArrayList<>(2); //百度地图纬度点坐标
    private ArrayList<Double> longitude_points = new ArrayList<>(2); //百度地图经度点坐标
    private ArrayList<LatLng> trace_points = new ArrayList<>(2); //记录轨迹的点
    //private LatLng point_one,point_two; //两点决定一条线
    //private OverlayOptions overlayOptions;  //绘制图层

    private double distance = 0;
    private double speed = 0;
    private double height = 0;
    private int calories = 0;
    private double body_weight = 0;

    private Thread time_thread;

    private SpeakUtil mUtil;
    private SpeakTimer speakTimer;

    private SharedPreferences mSetting;
    private SportSetting mSportSetting;

    /*
     * 通知相关的运动数据
     */
    private CountStepDemo countStep;  //计步的功能类
    private StepNotifier stepNotifier;
    private TimerUtil timerUtil;  //统计时间的功能类
    private TimeNotifier timeNotifier;
    private SpeedNotifier speedNotifier;  //通知速度
    private DistanceNotifier distanceNotifier; //通知距离
    private SensorManager sensorManager;

    private CaloriesNotifier caloriesNotifier;  //通知卡路里
    private HeightNotifier heightNotifier;  //通知海拔高度

    private Sensor sensor;
    private NotificationManager nm;

    private ArrayList<UpdateListener>mListeners;

    public SportService(){}

    @Override
    public void onCreate(){
        mListeners = new ArrayList<>();

        mUtil = SpeakUtil.getInstance();
        mUtil.setService(this);
        mUtil.initTTS();

        mSetting = PreferenceManager.getDefaultSharedPreferences(this);
        mSportSetting = new SportSetting(mSetting);

        speakTimer = new SpeakTimer(mUtil,mSportSetting);
        mListeners.add(speakTimer);

        countStep = new CountStepDemo(mUtil,mSportSetting);
        countStep.setStepNotifier();
        countStep.registerCallback(stepCallback);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(countStep, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        speakTimer.addListener(countStep);

        timerUtil = new TimerUtil(mUtil,mSportSetting);
        timerUtil.setTimeNotifier();
        timerUtil.registerCallback(timeCallback);
        time_thread = new Thread(timerUtil);
        speakTimer.addListener(timerUtil);

        distanceNotifier = new DistanceNotifier(mUtil,mDistanceListener,mSportSetting);
        distanceNotifier.setDistance(0);
        mListeners.add(distanceNotifier);
        speakTimer.addListener(distanceNotifier);

        speedNotifier = new SpeedNotifier(mUtil,mSpeedListener,mSportSetting);
        speedNotifier.setSpeed(0);
        mListeners.add(speedNotifier);
        speakTimer.addListener(speedNotifier);

        heightNotifier = new HeightNotifier(mUtil,mHeightListener,mSportSetting);
        heightNotifier.setHeight(0);
        mListeners.add(heightNotifier);
        speakTimer.addListener(heightNotifier);

        caloriesNotifier = new CaloriesNotifier(mUtil,mCaloriesListener,mSportSetting);
        caloriesNotifier.setCalories(0);
        mListeners.add(caloriesNotifier);
        speakTimer.addListener(caloriesNotifier);

//        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        showNotification();

        //Log.d(TAG, TimerUtil.getTotal_time()+" "+TimerUtil.getTotal_second());

        reloadSetting();

        registerReceiver(mReceiver,makeIntentFilter());
        Log.d(TAG,"onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.d(TAG, "onStartCommand()");
        initialize();

        time_thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        //退出时销毁定位
        locationClient.stop();
        //关闭定位图层
        baidumap.setMyLocationEnabled(false);
        TimerUtil.setTotal_second();
        mUtil.shutdownTTS();
        sensorManager.unregisterListener(countStep);
        unregisterReceiver(mReceiver);
        //nm.cancel(1);
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    public void reloadSetting(){
        speakTimer.reloadSetting();
        body_weight = Double.parseDouble(mSetting.getString("body_weight", "50"));
    }

    public interface ICallback{
        void stepsChanged(int step_number);
        void distanceChanged(double distance);
        void speedChanged(double speed);
        void timeChanged(String time);
        void caloriesChanged(int calories);
        void heightChanged(double height);
    }
    ICallback iCallback;

    public void registerCallback(ICallback callback){
        iCallback = callback;
    }

    DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        @Override
        public void distanceChanged(double distance) {
            iCallback.distanceChanged(distance);
        }
    };

    SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        @Override
        public void speedChanged(double speed) {
            iCallback.speedChanged(speed);
        }
    };

    CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        @Override
        public void caloriesChanged(double calories) {
            iCallback.caloriesChanged((int) calories);
        }
    };

    HeightNotifier.Listener mHeightListener = new HeightNotifier.Listener() {
        @Override
        public void heightChanged(double height) {
            iCallback.heightChanged(height);
        }
    };

    CountStepDemo.StepCallback stepCallback = new CountStepDemo.StepCallback() {
        @Override
        public void onStepChanged(int step) {
            iCallback.stepsChanged(step);
        }
    };

    TimerUtil.TimeCallback timeCallback = new TimerUtil.TimeCallback() {
        @Override
        public void onTimeChanged(String time) {
            iCallback.timeChanged(time);
        }
    };

    /*private void showNotification(){
        int icon = R.drawable.ic_directions_run_black;
        CharSequence text = "正在运动";
        Notification notification = new Notification(icon,text,System.currentTimeMillis());

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setComponent(new ComponentName(this, SportActivity.class));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
//        notification.setLatestEventInfo(this, text,
//                "SportAPP", pendingIntent);
        nm.notify(1, notification);
    }*/

    private void initialize(){
        baidumap.setMyLocationEnabled(true);
        locationClient = new LocationClient(getApplicationContext());
        locationListener = new MyLocationListener();
        locationClient.registerLocationListener(locationListener); //注册监听器
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);  //地图扫描频率，更新定位数据
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.requestLocation();
    }


    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            if(location == null)
                return;
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(0) //定位精度
                    .direction(100)  //定位的方向角度
                    .latitude(location.getLatitude())  //百度纬度坐标
                    .longitude(location.getLongitude()).build(); //百度经度坐标
            baidumap.setMyLocationData(locationData); //设置定位数据

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            if(!SportActivity.getIsStopSport()){
                latitude_points.add(location.getLatitude());
                longitude_points.add(location.getLongitude());
                if(latitude_points.size() == 2 && longitude_points.size() == 2){
                    /*LatLng point_one = new LatLng(latitude_points.get(0),longitude_points.get(0));
                    LatLng point_two = new LatLng(latitude_points.get(1),longitude_points.get(1));*/

                    WeakReference<LatLng> point1 = new WeakReference<>(
                            new LatLng(latitude_points.get(0),longitude_points.get(0)));
                    WeakReference<LatLng> point2 = new WeakReference<>(
                            new LatLng(latitude_points.get(1),longitude_points.get(1)));

                    trace_points.add(point1.get());
                    trace_points.add(point2.get());

                    /*OverlayOptions overlayOptions = new PolylineOptions().width(8)
                            .color(0xAAFF0000).points(trace_points);*/

                    baidumap.addOverlay(new PolylineOptions().width(8)
                            .color(0xAAFF0000).points(trace_points));

                    distance += DistanceUtil.getDistance(point1.get(),point2.get()) / 1000;
                    speed = distance / TimerUtil.getTotal_second() * 60;
                    height = location.getAltitude();
                    calories = (int)(body_weight * distance);

                    for(UpdateListener listener : mListeners){
                        distanceNotifier.setDistance(distance);
                        speedNotifier.setSpeed(speed);
                        heightNotifier.setHeight(height);
                        caloriesNotifier.setCalories(calories);

                        listener.onUpdate();
                    }
                    latitude_points.remove(0);
                    longitude_points.remove(0);
                    trace_points.remove(0);
                }
            }
        }
    }

    public static void shareMySport(){
        baidumap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap snapshot) {
                File file = new File("/mnt/sdcard/sport.png");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (snapshot.compress(
                            Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private IntentFilter makeIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(SportActivity.CHANGED_SETTINGS);
        return filter;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadSetting();
        }
    };


    public static void setBaidumap(BaiduMap map){
        baidumap = map;
    }


    class SportBinder extends Binder {
        SportService getSportService(){
            return SportService.this;
        }
    }

    private IBinder iBinder = new SportBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }
}
