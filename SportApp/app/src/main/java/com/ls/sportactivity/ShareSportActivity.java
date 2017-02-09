package com.ls.sportactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ls.Util.AnimationUtil;
import com.ls.Util.FormatUtil;
import com.ls.Util.LineView;
import com.ls.Util.TimerUtil;
import com.ls.sportapp.R;

import java.io.File;

public class ShareSportActivity extends Activity {

    private double mileage;
    private int steps;
    private int calories;
    private double height;
    private long time;
    private double speed;

    private ImageView share_image;
    private ImageView sport_image;

    private TextView mile_text;
    private TextView step_text;
    private TextView speed_text;
    private TextView calories_text;
    private TextView height_text;
    private TextView time_text;

    private LineView mile_line;
    private LineView step_line;
    private LineView speed_line;
    private LineView calories_line;
    private LineView height_line;
    private LineView time_line;

    private static final double PREFECT_DISTANCE = 5;
    private static final int PREFECT_STEP = 5000;
    private static final double PREFECT_SPEED = 5;
    private static final int PREFECT_CALORIES = 500;
    private static final double PREFECT_HEIGHT = 10;
    private static final long PREFECT_time = 1800;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_sport);

        initView();
    }

    private void initView(){
        share_image = (ImageView)findViewById(R.id.sport_share);
        sport_image = (ImageView)findViewById(R.id.map_image);

        share_image.setOnClickListener(shareListener);
        sport_image.setImageDrawable(Drawable.createFromPath("/mnt/sdcard/sport.png"));

        mile_text = (TextView)findViewById(R.id.mileage_statistic);
        step_text = (TextView)findViewById(R.id.step_statistic);
        speed_text = (TextView)findViewById(R.id.speed_statistic);
        calories_text = (TextView)findViewById(R.id.calories_statistic);
        height_text = (TextView)findViewById(R.id.height_statistic);
        time_text = (TextView)findViewById(R.id.time_statistic);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int length = metrics.widthPixels / 2;

        mile_line = (LineView)findViewById(R.id.mile_line);
        step_line = (LineView)findViewById(R.id.step_line);
        speed_line = (LineView)findViewById(R.id.speed_line);
        calories_line = (LineView)findViewById(R.id.calories_line);
        height_line = (LineView)findViewById(R.id.height_line);
        time_line = (LineView)findViewById(R.id.time_line);

        setLength(mile_line,length);
        setLength(step_line,length);
        setLength(speed_line,length);
        setLength(calories_line,length);
        setLength(height_line,length);
        setLength(time_line,length);

        Intent intent = getIntent();
        mileage = intent.getDoubleExtra("distance", 10);
        steps = intent.getIntExtra("steps", 40);
        speed = intent.getDoubleExtra("speed", 2) / 60 * 1000;
        calories = intent.getIntExtra("calories", 30);
        height = intent.getDoubleExtra("height", 2);
        time = TimerUtil.getTotal_second();
        AnimationUtil.line_view_data_animation(mile_line,mile_text,(float)(mileage / PREFECT_DISTANCE * length), FormatUtil.doubleFormat(mileage)+"千米");
        AnimationUtil.line_view_data_animation(step_line,step_text,(float)(steps / PREFECT_STEP * length),intent.getIntExtra("steps", 40)+"步");
        AnimationUtil.line_view_data_animation(speed_line,speed_text,(float)(speed / PREFECT_SPEED * length),FormatUtil.doubleFormat(speed)+"m/s");
        AnimationUtil.line_view_data_animation(calories_line,calories_text,(float)(calories / PREFECT_CALORIES * length),intent.getIntExtra("calories", 30)+"千焦");
        AnimationUtil.line_view_data_animation(height_line,height_text,(float)(height / PREFECT_HEIGHT * length),FormatUtil.doubleFormat(height)+"米");
        AnimationUtil.line_view_data_animation(time_line,time_text,(float)(time / PREFECT_time *length), time/60+"分钟"+time%60+"秒");
    }

    private void setLength(LineView v,int length){
        ViewGroup.LayoutParams v_params = v.getLayoutParams();
        v_params.width = length;
        v.setLayoutParams(v_params);
    }

    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*,image/*");
            File file=new File("/mnt/sdcard/sport.png");
            Uri u=Uri.fromFile(file);
            intent.putExtra(Intent.EXTRA_TEXT, "分享自刘自在编写的App——");
            intent.putExtra(Intent.EXTRA_STREAM, u);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };
}
