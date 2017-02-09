package com.ls.sportactivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.ls.Util.AnimationUtil;
import com.ls.sportapp.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SportActivity extends Activity {

    private final String TAG = "SportActivity";
    public static final String CHANGED_SETTINGS = "com.ls.sportActivity.changeSettings";
    private static SportActivity mThis;
    public static SportActivity getInstance(){
        return mThis;
    }

    private static final int DATA_TOP = 1;
    private static final int DATA_LEFT = 2;
    private static final int DATA_RIGHT = 3;

    private MapView sport_map;  //百度地图视图
    BaiduMap baiduMap;

    private LinearLayout data_pane; //展示运动数据的布局容器
    //展示不同位置类型数据的布局容器
    private LinearLayout data_pane_top,data_pane_left,data_pane_right;

    private TextView data_top,data_name_top; //顶部显示的数据和顶部数据名称
    private TextView data_left,data_name_left;  //左边显示的数据与左边数据的名称
    private TextView data_right,data_name_right;  //右边显示的数据和右边数据的名称

    private LinearLayout data_pane_small;
    private TextView data_right_small,data_name_right_small;
    private TextView data_left_small,data_name_left_small;

    private Button start_stop_button,finish_button; //控制运动的相关按钮

    private ImageView setting,camera;

    private static boolean isStop_sport = false;  //运动是否暂停
    private boolean isShow_data_pane_big = true; //是否显示大界面的运动数据

    private PopupWindow data_type_window;  //PopupWindow选择显示数据类型
    private LinearLayout mile_layout,time_layout,speed_layout,step_layout,calories_layout,height_layout;

    private SportDialog sport_finish_dialog;
//    private Button finish_button_;
//    private Button continue_button;

    private int length,to_length; //百度地图伸缩的长度

    private SportService mService;

    private double mileage;
    private int steps;
    private int sum_calories;
    private double sum_height;
    private long time;
    private double average_speed;

    private Map<Integer,Boolean> data_type;
    private Map<Integer,Integer> data_position,data_position_type;
    private boolean isClick_top = false;
    private boolean isClick_left = false;
    private boolean isClick_right = false;
    private DecimalFormat dcmFmt = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_sport);

        mThis = this;
        initView();
        initHashMap();
    }

    @Override
    protected void onPause() {
        sport_map.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        sport_map.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        sport_map.onDestroy();
        sport_map = null;
        stopService(new Intent(SportActivity.this, SportService.class));
        unbindService(mConnection);
        isStop_sport = false;
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    private void initView() {
        /*
         * 有关运动实时数据显示的相关控件和初始化
         */
        data_pane = (LinearLayout) findViewById(R.id.data_pane);
        data_top = (TextView) findViewById(R.id.show_data_top);
        data_name_top = (TextView) findViewById(R.id.data_name_top);
        data_left = (TextView) findViewById(R.id.show_data_left);
        data_name_left = (TextView) findViewById(R.id.data_name_left);
        data_right = (TextView) findViewById(R.id.show_data_right);
        data_name_right = (TextView) findViewById(R.id.data_name_right);

        /*
         * 设置数据面板显示的长度为屏幕的2/3
         */
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screen_height = metrics.heightPixels * 2 / 3;
        length = metrics.heightPixels / 3;
        ViewGroup.LayoutParams data_pane_layoutParams = data_pane.getLayoutParams();
        data_pane_layoutParams.height = screen_height;
        data_pane.setLayoutParams(data_pane_layoutParams);

        //-------------------------------------------------------------------------------------
        sport_map = (MapView)findViewById(R.id.baidu_map);
        ViewGroup.LayoutParams mapView_layoutParams = sport_map.getLayoutParams();
        mapView_layoutParams.height = length;
        sport_map.setLayoutParams(mapView_layoutParams);

        to_length = metrics.heightPixels - AnimationUtil.dp2px(SportActivity.this,100);

        //------------------------------------------------------------------------------------
        /*
         ** 显示小的数据表盘
         */
        data_pane_small = (LinearLayout)findViewById(R.id.data_pane_small);
        data_right_small = (TextView)findViewById(R.id.show_data_right_small);
        data_name_right_small = (TextView)findViewById(R.id.data_name_right_small);
        data_left_small = (TextView)findViewById(R.id.show_data_left_small);
        data_name_left_small = (TextView)findViewById(R.id.data_name_left_small);

        data_pane_top = (LinearLayout) findViewById(R.id.data_top_layout);
        data_pane_left = (LinearLayout) findViewById(R.id.data_left_layout);
        data_pane_right = (LinearLayout) findViewById(R.id.data_right_layout);

        data_pane_top.setOnTouchListener(change_show_data_touchListener);
        data_pane_left.setOnTouchListener(change_show_data_touchListener);
        data_pane_right.setOnTouchListener(change_show_data_touchListener);

        //-------------------------------------------------------------------------------------
        /*
         ** 暂停和结束运动按钮
         */
        start_stop_button = (Button)findViewById(R.id.sport_start_stop);
        finish_button = (Button)findViewById(R.id.sport_finish);
        start_stop_button.setOnClickListener(change_sport_state_listener);
        finish_button.setOnClickListener(change_sport_state_listener);

        setting = (ImageView)findViewById(R.id.sport_setting);
        camera = (ImageView)findViewById(R.id.sport_camera);
        setting.setOnClickListener(clickListener);
        camera.setOnClickListener(clickListener);


        /**
         * Popwindow显示数据类型
         */
        View v = LayoutInflater.from(this).inflate(R.layout.show_data_type_layout,null); //载入窗口布局
        data_type_window = new PopupWindow(v,ViewGroup.LayoutParams.MATCH_PARENT,
                screen_height);

        data_type_window.setOutsideTouchable(true);
        data_type_window.setFocusable(true);
        //设置背景图片后点击外部或者按返回键popupwindow才消失
        data_type_window.setBackgroundDrawable(new BitmapDrawable());
        data_type_window.setAnimationStyle(R.style.PopupwindowTheme);
        initPopupWindow(v);

        /*
         * 启动百度地图服务
         */

        baiduMap = sport_map.getMap();
        baiduMap.setOnMapClickListener(mapClickListener);
        SportService.setBaidumap(baiduMap);

        startService(new Intent(SportActivity.this, SportService.class));
        bindService(new Intent(SportActivity.this,SportService.class),mConnection,Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);

        /*
         * 结束运动对话框
         */
        sport_finish_dialog = new SportDialog(this);
        sport_finish_dialog.setTitle("运动距离太短将不保存数据");
        //sport_finish_dialog.setListener(dialogListener);

        //sport_finish_dialog.set
        //initDialog();

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((SportService.SportBinder)service).getSportService();
            mService.registerCallback(iCallback);
            mService.reloadSetting();
            //mService.setBaidumap(baiduMap);
            MyApplication myApp = (MyApplication)getApplication();
            if(myApp.getBaiduMapView() == null){
                myApp.setBaiduMapView(sport_map);
                myApp.getBaiduMapView().setTag("baidumap");
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initPopupWindow(View v){
        mile_layout = (LinearLayout)v.findViewById(R.id.mile_layout);
        time_layout = (LinearLayout)v.findViewById(R.id.time_layout);
        speed_layout = (LinearLayout)v.findViewById(R.id.speed_layout);
        step_layout = (LinearLayout)v.findViewById(R.id.step_layout);
        calories_layout = (LinearLayout)v.findViewById(R.id.calories_layout);
        height_layout = (LinearLayout)v.findViewById(R.id.height_layout);

        mile_layout.setOnClickListener(data_layout_listener);
        time_layout.setOnClickListener(data_layout_listener);
        speed_layout.setOnClickListener(data_layout_listener);
        step_layout.setOnClickListener(data_layout_listener);
        calories_layout.setOnClickListener(data_layout_listener);
        height_layout.setOnClickListener(data_layout_listener);
    }

    /*
     * 显示对话框
     */
    /*private void initDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.sport_dialog_layout,null);
        finish_button_ = (Button)view.findViewById(R.id.finish_sport_button);
        continue_button = (Button)view.findViewById(R.id.back_sport_button);
        finish_button_.setOnClickListener(dialogListener);
        continue_button.setOnClickListener(dialogListener);
    }*/

    //--------------------------------------------------------------------------------------
    private void initHashMap(){
        data_position = new HashMap<>();
        data_type = new HashMap<>();
        data_position_type = new HashMap<>();

        data_position.put(DISTANCE_MSG,DATA_TOP);
        data_position.put(TIME_MSG,DATA_LEFT);
        data_position.put(SPEED_MSG,DATA_RIGHT);
        data_position.put(STEP_MSG,0);
        data_position.put(CALORIES_MSG,0);
        data_position.put(HEIGHT_MSG,0);

        data_type.put(DISTANCE_MSG,true);
        data_type.put(TIME_MSG,true);
        data_type.put(SPEED_MSG,true);
        data_type.put(STEP_MSG,false);
        data_type.put(CALORIES_MSG,false);
        data_type.put(HEIGHT_MSG,false);

        data_position_type.put(DATA_TOP,DISTANCE_MSG);
        data_position_type.put(DATA_LEFT,TIME_MSG);
        data_position_type.put(DATA_RIGHT,SPEED_MSG);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sport_setting:
                    //启动首选项设置
                    Intent intent = new Intent(SportActivity.this,Setting.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.sport_camera:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int reqCode,int resCode,Intent data){
        Intent intent = new Intent(CHANGED_SETTINGS);
        sendBroadcast(intent);
    }

    /*
     * 改变数据类型显示
     */
    View.OnTouchListener change_show_data_touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    switch(v.getId()){
                        case R.id.data_top_layout:
                            data_top.setTextColor(getResources().getColor(R.color.data_text_color));
                            data_name_top.setTextColor(getResources().getColor(R.color.data_text_color));
                            break;
                        case R.id.data_left_layout:
                            data_left.setTextColor(getResources().getColor(R.color.data_text_color));
                            data_name_left.setTextColor(getResources().getColor(R.color.data_text_color));
                            break;
                        case R.id.data_right_layout:
                            data_right.setTextColor(getResources().getColor(R.color.data_text_color));
                            data_name_right.setTextColor(getResources().getColor(R.color.data_text_color));
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    switch(v.getId()){
                        case R.id.data_top_layout:
                            data_top.setTextColor(Color.parseColor("#525252"));
                            data_name_top.setTextColor(Color.parseColor("#525252"));
                            show_data_type();
                            isClick_top = true;
                            break;
                        case R.id.data_left_layout:
                            data_left.setTextColor(Color.parseColor("#525252"));
                            data_name_left.setTextColor(Color.parseColor("#525252"));
                            show_data_type();
                            isClick_left = true;
                            break;
                        case R.id.data_right_layout:
                            data_right.setTextColor(Color.parseColor("#525252"));
                            data_name_right.setTextColor(Color.parseColor("#525252"));
                            show_data_type();
                            isClick_right = true;
                            break;
                    }
                    break;
            }
            return true;
        }
    };


    /*
     * 改变运动状态，是否继续或者退出运动
     */
    View.OnClickListener change_sport_state_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.sport_start_stop:
                    if(!isStop_sport){
                        isStop_sport = true;
                        start_stop_button.setText("继续");
                        AnimationUtil.sport_button_animation_separate(start_stop_button,finish_button);
                    }else{
                        isStop_sport = false;
                        start_stop_button.setText("暂停");
                        AnimationUtil.sport_button_animation_compose(start_stop_button,finish_button);
                    }
                    break;
                case R.id.sport_finish:
                    if(mileage < 0.5){
                        sport_finish_dialog.show();
                        Intent intent = new Intent(SportActivity.this,Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        SportService.shareMySport();
                        Intent intent = new Intent(SportActivity.this,ShareSportActivity.class);
                        intent.putExtra("distance",mileage);
                        intent.putExtra("speed",average_speed);
                        intent.putExtra("steps",steps);
                        intent.putExtra("calories",sum_calories);
                        intent.putExtra("height",sum_height);
                        startActivity(intent);
                        finish();
                    }

                    break;
            }
        }
    };

    /*
     * 百度地图的点击监听事件
     */
    BaiduMap.OnMapClickListener mapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(isShow_data_pane_big){
                AnimationUtil.data_pane_animation_one(data_pane,data_pane_small,sport_map,length,to_length);
                Log.d(TAG, "data_pane_animation_one");
                isShow_data_pane_big = false;
            }else{
                AnimationUtil.data_pane_animation_two(data_pane,data_pane_small,sport_map,to_length,length);
                Log.d(TAG, "data_pane_animation_two");
                isShow_data_pane_big = true;
            }
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    };

    View.OnClickListener data_layout_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.mile_layout:
                    changed_data_show(R.id.mile_layout);
                    data_type_window.dismiss();
                    break;
                case R.id.time_layout:
                    changed_data_show(R.id.time_layout);
                    data_type_window.dismiss();
                    break;
                case R.id.speed_layout:
                    changed_data_show(R.id.speed_layout);
                    data_type_window.dismiss();
                    break;
                case R.id.calories_layout:
                    changed_data_show(R.id.calories_layout);
                    data_type_window.dismiss();
                    break;
                case R.id.step_layout:
                    changed_data_show(R.id.step_layout);
                    data_type_window.dismiss();
                    break;
                case R.id.height_layout:
                    changed_data_show(R.id.height_layout);
                    data_type_window.dismiss();
                    break;
            }
        }
    };

    private void changed_data_show(int id){
        switch(id){
            case R.id.mile_layout:
                if(isClick_top){
                    Log.d(TAG,"change_top_data_state()");
                    change_top_data_state(DISTANCE_MSG);
                    isClick_top = false;
                }else if(isClick_left){
                    change_left_data_state(DISTANCE_MSG);
                    isClick_left = false;
                }else if(isClick_right){
                    change_right_data_state(DISTANCE_MSG);
                    isClick_right = false;
                }
                break;
            case R.id.time_layout:
                if(isClick_top){
                    change_top_data_state(TIME_MSG);
                    isClick_top = false;
                }else if(isClick_left){
                    change_left_data_state(TIME_MSG);
                    isClick_left = false;
                }else if(isClick_right){
                    change_right_data_state(TIME_MSG);
                    isClick_right = false;
                }
                break;
            case R.id.speed_layout:
                if(isClick_top){
                    change_top_data_state(SPEED_MSG);
                    isClick_top = false;
                }else if(isClick_left){
                    change_left_data_state(SPEED_MSG);
                    isClick_left = false;
                }else if(isClick_right){
                    change_right_data_state(SPEED_MSG);
                    isClick_right = false;
                }
                break;
            case R.id.calories_layout:
                if(isClick_top){
                    change_top_data_state(CALORIES_MSG);
                    isClick_top = false;
                }else if(isClick_left){
                    change_left_data_state(CALORIES_MSG);
                    isClick_left = false;
                }else if(isClick_right){
                    change_right_data_state(CALORIES_MSG);
                    isClick_right = false;
                }
                break;
            case R.id.step_layout:
                if(isClick_top){
                    change_top_data_state(STEP_MSG);
                    isClick_top = false;
                }else if(isClick_left){
                    change_left_data_state(STEP_MSG);
                    isClick_left = false;
                }else if(isClick_right){
                    change_right_data_state(STEP_MSG);
                    isClick_right = false;
                }
                break;
            case R.id.height_layout:
                if(isClick_top){
                    change_top_data_state(HEIGHT_MSG);
                    isClick_top = false;
                }else if(isClick_left){
                    change_left_data_state(HEIGHT_MSG);
                    isClick_left = false;
                }else if(isClick_right){
                    change_right_data_state(HEIGHT_MSG);
                    isClick_right = false;
                }
                break;
        }
    }

    /*
     * 改变顶部数据的显示
     */
    private void change_top_data_state(int type){
        if(data_position_type.get(DATA_TOP) == type){
           // not use
        }else if(data_position_type.get(DATA_LEFT) == type){
            int temp = data_position_type.get(DATA_LEFT);
            data_position.put(type, DATA_TOP);
            data_position.put(data_position_type.get(DATA_TOP), DATA_LEFT);
            data_position_type.put(DATA_LEFT, data_position_type.get(DATA_TOP));
            data_position_type.put(DATA_TOP,temp);
        }else if(data_position_type.get(DATA_RIGHT) == type) {
            int temp = data_position_type.get(DATA_RIGHT);
            int position = data_position.get(DATA_RIGHT);
            data_position.put(type, DATA_TOP);
            data_position.put(data_position_type.get(DATA_TOP), DATA_RIGHT);
            data_position_type.put(DATA_RIGHT, data_position_type.get(DATA_TOP));
            data_position_type.put(DATA_TOP, temp);
        }else{
            data_type.put(type,true);
            data_position.put(type,DATA_TOP);
            data_type.put(data_position_type.get(DATA_TOP),false);
            data_position.put(data_position_type.get(DATA_TOP),0);
            data_position_type.put(DATA_TOP,type);
        }
    }

    private void change_left_data_state(int type){
        if(data_position_type.get(DATA_LEFT) == type){

        }else if(data_position_type.get(DATA_TOP) == type){
            int temp = data_position_type.get(DATA_TOP);
            data_position.put(type,DATA_LEFT);
            data_position.put(data_position_type.get(DATA_LEFT),DATA_TOP);
            data_position_type.put(DATA_TOP,data_position_type.get(DATA_LEFT));
            data_position_type.put(DATA_LEFT,temp);
        }else if(data_position_type.get(DATA_RIGHT) == type) {
            int temp = data_position_type.get(DATA_RIGHT);
            data_position.put(type,DATA_LEFT);
            data_position.put(data_position_type.get(DATA_LEFT),DATA_RIGHT);
            data_position_type.put(DATA_RIGHT, data_position_type.get(DATA_LEFT));
            data_position_type.put(DATA_LEFT, temp);
        }else{
            data_type.put(type,true);
            data_position.put(type,DATA_LEFT);
            data_type.put(data_position_type.get(DATA_LEFT),false);
            data_position.put(data_position_type.get(DATA_LEFT),0);
            data_position_type.put(DATA_LEFT,type);
        }
    }

    private void change_right_data_state(int type){
        if(data_position_type.get(DATA_RIGHT) == type){

        }else if(data_position_type.get(DATA_TOP) == type){
            int temp = data_position_type.get(DATA_TOP);
            data_position.put(type,DATA_RIGHT);
            data_position.put(data_position_type.get(DATA_RIGHT),DATA_TOP);
            data_position_type.put(DATA_TOP,data_position_type.get(DATA_RIGHT));
            data_position_type.put(DATA_RIGHT,temp);
        }else if(data_position_type.get(DATA_LEFT) == type) {
            int temp = data_position_type.get(DATA_RIGHT);
            data_position.put(type,DATA_RIGHT);
            data_position.put(data_position_type.get(DATA_RIGHT),DATA_LEFT);
            data_position_type.put(DATA_RIGHT, data_position_type.get(DATA_LEFT));
            data_position_type.put(DATA_LEFT, temp);
        }else{
            data_type.put(type,true);
            data_position.put(type,DATA_RIGHT);
            data_type.put(data_position_type.get(DATA_RIGHT),false);
            data_position.put(data_position_type.get(DATA_RIGHT),0);
            data_position_type.put(DATA_RIGHT,type);
        }
    }

    /*
     * 显示数据类型
     */
    private void show_data_type(){
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
        data_type_window.showAtLocation(rootView, Gravity.BOTTOM,0,0);
    }

    public static boolean getIsStopSport(){
        return isStop_sport;
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == event.KEYCODE_BACK){
            if(!isStop_sport)
                Toast.makeText(this,"请先点击暂停按钮结束运动",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,"请先结束运动",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return super.onKeyDown(keyCode,event);
        }
    }

    /*
     * 各项运动数据的回调函数
     */
    SportService.ICallback iCallback = new SportService.ICallback() {
        @Override
        public void stepsChanged(int step_number) {
            mHandler.sendMessage(mHandler.obtainMessage(STEP_MSG,step_number,0));
            steps = step_number;
        }

        @Override
        public void distanceChanged(double distance) {
            Message m = new Message();
            m.what = DISTANCE_MSG;
            Bundle data = new Bundle();
            data.putDouble("distance",distance);
            m.setData(data);
            mHandler.sendMessage(m);
            mileage = distance;
        }

        @Override
        public void speedChanged(double speed) {
            Message m = new Message();
            m.what = SPEED_MSG;
            Bundle data = new Bundle();
            data.putDouble("speed", speed);
            m.setData(data);
            mHandler.sendMessage(m);
            average_speed = speed;
        }

        @Override
        public void timeChanged(String time) {
            Message m = new Message();
            m.what = TIME_MSG;
            Bundle data = new Bundle();
            data.putString("time",time);
            m.setData(data);
            mHandler.sendMessage(m);
        }

        @Override
        public void caloriesChanged(int calories) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,calories,0));
            sum_calories = calories;
        }

        @Override
        public void heightChanged(double height) {
            Message m = new Message();
            m.what = HEIGHT_MSG;
            Bundle data = new Bundle();
            data.putDouble("height", height);
            m.setData(data);
            mHandler.sendMessage(m);
            sum_height = height;
        }
    };

    private static final int STEP_MSG = 1;
    private static final int TIME_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int HEIGHT_MSG = 5;
    private static final int CALORIES_MSG = 6;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case STEP_MSG:
                    if(data_type.get(STEP_MSG)){
                        switch (data_position.get(STEP_MSG)){
                            case DATA_TOP:
                                data_top.setText(msg.arg1+"");
                                data_name_top.setText(getResources().getString(R.string.steps));
                                data_left_small.setText(msg.arg1+"");
                                data_name_left_small.setText(getResources().getString(R.string.steps));
                                break;
                            case DATA_LEFT:
                                data_left.setText(msg.arg1+"");
                                data_name_left.setText(getResources().getString(R.string.steps));
                                data_right_small.setText(msg.arg1+"");
                                data_name_right_small.setText(getResources().getString(R.string.steps));
                                break;
                            case DATA_RIGHT:
                                data_right.setText(msg.arg1+"");
                                data_name_right.setText(getResources().getString(R.string.steps));
                                break;
                        }
                    }
                    break;
                case TIME_MSG:
                    Bundle data = msg.getData();
                    if(data_type.get(TIME_MSG)){
                        switch (data_position.get(TIME_MSG)){
                            case DATA_TOP:
                                data_top.setText(data.getString("time"));
                                data_name_top.setText(getResources().getString(R.string.time_caculate));
                                data_left_small.setText(data.getString("time"));
                                data_name_left_small.setText(getResources().getString(R.string.time_caculate));
                                break;
                            case DATA_LEFT:
                                data_left.setText(data.getString("time"));
                                data_name_left.setText(getResources().getString(R.string.time_caculate));
                                data_right_small.setText(data.getString("time"));
                                data_name_right_small.setText(getResources().getString(R.string.time_caculate));
                                break;
                            case DATA_RIGHT:
                                data_right.setText(data.getString("time"));
                                data_name_right.setText(getResources().getString(R.string.time_caculate));
                                break;
                        }
                    }
                    break;
                case DISTANCE_MSG:
                    Bundle data1 = msg.getData();
                    if(data_type.get(DISTANCE_MSG)){
                        switch (data_position.get(DISTANCE_MSG)){
                            case DATA_TOP:
                                data_top.setText("" + dcmFmt.format(data1.getDouble("distance")));
                                data_name_top.setText(getResources().getString(R.string.mileage));
                                data_left_small.setText("" + dcmFmt.format(data1.getDouble("distance")));
                                data_name_left_small.setText(getResources().getString(R.string.mileage));
                                break;
                            case DATA_LEFT:
                                data_left.setText("" + dcmFmt.format(data1.getDouble("distance")));
                                data_name_left.setText(getResources().getString(R.string.mileage));
                                data_right_small.setText("" + dcmFmt.format(data1.getDouble("distance")));
                                data_name_right_small.setText(getResources().getString(R.string.mileage));
                                break;
                            case DATA_RIGHT:
                                data_right.setText("" + dcmFmt.format(data1.getDouble("distance")));
                                data_name_right.setText(getResources().getString(R.string.mileage));
                                break;
                        }
                    }
                    break;
                case SPEED_MSG:
                    Bundle data2 = msg.getData();
                    if(data_type.get(SPEED_MSG)){
                        switch (data_position.get(SPEED_MSG)){
                            case DATA_TOP:
                                data_top.setText("" + dcmFmt.format(data2.getDouble("speed")));
                                data_name_top.setText(getResources().getString(R.string.speed));
                                data_left_small.setText("" + dcmFmt.format(data2.getDouble("speed")));
                                data_name_left_small.setText(getResources().getString(R.string.speed));
                                break;
                            case DATA_LEFT:
                                data_left.setText("" + dcmFmt.format(data2.getDouble("speed")));
                                data_name_left.setText(getResources().getString(R.string.speed));
                                data_right_small.setText("" + dcmFmt.format(data2.getDouble("speed")));
                                data_name_right_small.setText(getResources().getString(R.string.speed));
                                break;
                            case DATA_RIGHT:
                                data_right.setText("" + dcmFmt.format(data2.getDouble("speed")));
                                data_name_right.setText(getResources().getString(R.string.speed));
                                break;
                        }
                    }
                    break;
                case HEIGHT_MSG:
                    Bundle data3 = msg.getData();
                    if(data_type.get(HEIGHT_MSG)){
                        switch (data_position.get(HEIGHT_MSG)){
                            case DATA_TOP:
                                data_top.setText("" + dcmFmt.format(data3.getDouble("height")));
                                data_name_top.setText(getResources().getString(R.string.height));
                                data_left_small.setText("" + dcmFmt.format(data3.getDouble("height")));
                                data_name_left_small.setText(getResources().getString(R.string.height));
                                break;
                            case DATA_LEFT:
                                data_left.setText("" + dcmFmt.format(data3.getDouble("height")));
                                data_name_left.setText(getResources().getString(R.string.height));
                                data_right_small.setText("" + dcmFmt.format(data3.getDouble("height")));
                                data_name_right_small.setText(getResources().getString(R.string.height));
                                break;
                            case DATA_RIGHT:
                                data_right.setText("" + dcmFmt.format(data3.getDouble("height")));
                                data_name_right.setText(getResources().getString(R.string.height));
                                break;
                        }
                    }
                    break;
                case CALORIES_MSG:
                    if(data_type.get(CALORIES_MSG)){
                        switch (data_position.get(CALORIES_MSG)){
                            case DATA_TOP:
                                data_top.setText(msg.arg1+"");
                                data_name_top.setText(getResources().getString(R.string.calorie));
                                data_left_small.setText(msg.arg1+"");
                                data_name_left_small.setText(getResources().getString(R.string.calorie));
                                break;
                            case DATA_LEFT:
                                data_left.setText(msg.arg1+"");
                                data_name_left.setText(getResources().getString(R.string.calorie));
                                data_right_small.setText(msg.arg1+"");
                                data_name_right_small.setText(getResources().getString(R.string.calorie));
                                break;
                            case DATA_RIGHT:
                                data_right.setText(msg.arg1+"");
                                data_name_right.setText(getResources().getString(R.string.calorie));
                                break;
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
