package com.ls.sportactivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ls.sportapp.R;


/**
 * Created by qwe on 2016/5/13.
 */
public class SportDialog extends Dialog{

    private Button finish_sport;
    private Button continue_sport;

    public SportDialog(Context context){
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_dialog_layout);
        Log.d("SportDialog", "onCreate()");

        initView();
    }

    private void initView(){
        continue_sport = (Button)findViewById(R.id.back_sport_button);
        finish_sport = (Button)findViewById(R.id.finish_sport_button);
        finish_sport.setOnClickListener(dialogListener);
        continue_sport.setOnClickListener(dialogListener);
        if(finish_sport == null){
            Log.d("SportDialog","finish_button is null");
        }
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    public void setListener(View.OnClickListener listener){
        if(listener == null){
            Log.d("SportDialog","listener is null");
        }else{
            finish_sport.setOnClickListener(listener);
            continue_sport.setOnClickListener(listener);
        }
    }

    View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.finish_sport_button:
                    SportActivity.getInstance().finish();
                    break;
                case R.id.back_sport_button:
                    dismiss();
                    break;
            }
        }
    };

}
