package com.ls.fragmentmodel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ls.sportactivity.Setting;
import com.ls.sportactivity.SportActivity;
import com.ls.sportapp.R;

/**
 * Created by qwe on 2016/4/12.
 */
public class SportFragment extends Fragment {

    private View v;
    private Button start_sport;
    private ImageView settings;
    private ImageView sport_type;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savesInstanceState){
        if(container == null)
            return null;
        v = inflater.inflate(R.layout.sport_fragment_layout,container,false);

        initView();
        return v;
    }

    private void initView(){
        start_sport = (Button)v.findViewById(R.id.start_sport_button);
        start_sport.setOnClickListener(sportListener);

        settings = (ImageView)v.findViewById(R.id.settings);
        sport_type = (ImageView)v.findViewById(R.id.sport_type);
        settings.setOnClickListener(clickListener);
        sport_type.setOnClickListener(clickListener);

    }

    View.OnClickListener sportListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), SportActivity.class);
            //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            startActivity(intent);
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.settings:
                    Intent intent = new Intent(getActivity(),Setting.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.sport_type:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int reqCode,int resCode,Intent data){
        Intent intent = new Intent(SportActivity.CHANGED_SETTINGS);
        getActivity().sendBroadcast(intent);
    }
}
