package com.ls.sportactivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.ls.sportapp.R;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        MapView map_view = (MapView)findViewById(R.id.map_view);
        MyApplication myApp = (MyApplication)getApplication();
        /*((ViewGroup)(myApp.getBaiduMapView().getParent())).removeView(myApp.getBaiduMapView());
        if(myApp.getBaiduMapView()!=null)
            map_view.addView(myApp.getBaiduMapView());*/
        map_view = myApp.getBaiduMapView();
        Toast.makeText(this,"Tag:"+map_view.getTag(),Toast.LENGTH_SHORT).show();
    }
}
