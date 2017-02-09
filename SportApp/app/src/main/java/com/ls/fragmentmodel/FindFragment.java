package com.ls.fragmentmodel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ls.sportapp.R;

/**
 * Created by qwe on 2016/4/12.
 */
public class FindFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        if(container == null)
            return null;
        View v = inflater.inflate(R.layout.find_fragment_layout,container,false);
        return v;
    }
}
