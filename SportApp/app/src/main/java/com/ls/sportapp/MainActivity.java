package com.ls.sportapp;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ls.fragmentmodel.ContactFragment;
import com.ls.fragmentmodel.FindFragment;
import com.ls.fragmentmodel.MeFragment;
import com.ls.fragmentmodel.MessageFragment;
import com.ls.fragmentmodel.SportFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /*
     底部栏按键布局
     */
    private LinearLayout message_layout;
    private LinearLayout find_layout;
    private LinearLayout sport_layout;
    private LinearLayout contact_layout;
    private LinearLayout me_layout;

    private ArrayList<ImageView> fragment_images; //存储底部栏图标对象
    private ArrayList<TextView> fragment_texts;  //存储底部栏名称对象
    private ArrayList<Integer> images_focus;  //存储获得焦点的图标对象ID
    private ArrayList<Integer> images_no_focus; //存储未获得焦点的图标对象ID

    /*
      初始化碎片界面
     */
    private static MessageFragment message = new MessageFragment();
    private static FindFragment find = new FindFragment();
    private static SportFragment sport = new SportFragment();
    private static ContactFragment contact = new ContactFragment();
    private static MeFragment me = new MeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        message_layout = (LinearLayout)findViewById(R.id.message_layout);
        find_layout = (LinearLayout)findViewById(R.id.find_layout);
        sport_layout = (LinearLayout)findViewById(R.id.sport_layout);
        contact_layout = (LinearLayout)findViewById(R.id.contact_layout);
        me_layout = (LinearLayout)findViewById(R.id.me_layout);

        ImageView message_image = (ImageView)findViewById(R.id.message_image);
        ImageView find_image = (ImageView)findViewById(R.id.find_image);
        ImageView sport_image = (ImageView)findViewById(R.id.sport_image);
        ImageView contact_image = (ImageView)findViewById(R.id.contact_image);
        ImageView me_image = (ImageView)findViewById(R.id.me_image);

        TextView message_text = (TextView)findViewById(R.id.message_text);
        TextView find_text = (TextView)findViewById(R.id.find_text);
        TextView sport_text = (TextView)findViewById(R.id.sport_text);
        TextView contact_text = (TextView)findViewById(R.id.contact_text);
        TextView me_text = (TextView)findViewById(R.id.me_text);

        message_layout.setOnClickListener(optionListener);
        find_layout.setOnClickListener(optionListener);
        sport_layout.setOnClickListener(optionListener);
        contact_layout.setOnClickListener(optionListener);
        me_layout.setOnClickListener(optionListener);

        fragment_images = new ArrayList<>();
        fragment_texts = new ArrayList<>();
        images_focus = new ArrayList<>();
        images_no_focus = new ArrayList<>();

        fragment_images.add(message_image);
        fragment_images.add(find_image);
        fragment_images.add(sport_image);
        fragment_images.add(contact_image);
        fragment_images.add(me_image);

        images_focus.add(R.drawable.bottom_layout_message_focus);
        images_focus.add(R.drawable.bottom_layout_find_focus);
        images_focus.add(R.drawable.bottom_layout_sport);
        images_focus.add(R.drawable.bottom_layout_contact_focus);
        images_focus.add(R.drawable.bottom_layout_me_focus);

        images_no_focus.add(R.drawable.bottom_layout_message_no_focus);
        images_no_focus.add(R.drawable.bottom_layout_find_no_focus);
        images_no_focus.add(R.drawable.bottom_layout_sport);
        images_no_focus.add(R.drawable.bottom_layout_contact_no_focus);
        images_no_focus.add(R.drawable.bottom_layout_me_no_focus);

        fragment_texts.add(message_text);
        fragment_texts.add(find_text);
        fragment_texts.add(sport_text);
        fragment_texts.add(contact_text);
        fragment_texts.add(me_text);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,sport);
        ft.commit();
    }

    View.OnClickListener optionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.message_layout:
                    show_message_fragment(R.id.message_image);
                    break;
                case R.id.find_layout:
                    show_find_fragment(R.id.find_image);
                    break;
                case R.id.sport_layout:
                    show_sport_fragment(R.id.sport_image);
                    break;
                case R.id.contact_layout:
                    show_contact_fragment(R.id.contact_image);
                    break;
                case R.id.me_layout:
                    show_me_fragment(R.id.me_image);
                    break;
            }
        }
    };

    /*
      显示消息界面
     */
    private void show_message_fragment(int imageId){
        change_image_state(imageId);
        message = new MessageFragment();
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,message);
        ft.commit();
    }

    /*
      显示发现界面
     */
    private void show_find_fragment(int imageId){
        change_image_state(imageId);
        find = new FindFragment();
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,find);
        ft.commit();
    }

    /*
      显示运动界面
     */
    private void show_sport_fragment(int imageId){
        change_image_state(imageId);
        sport = new SportFragment();
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,sport);
        ft.commit();
    }

    /*
      显示运动圈界面
     */
    private void show_contact_fragment(int imageId){
        change_image_state(imageId);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        contact = new ContactFragment();
        ft.replace(R.id.fragment_container,contact);
        ft.commit();
    }

    /*
      显示我的界面
     */
    private void show_me_fragment(int imageId){
        change_image_state(imageId);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        me = new MeFragment();
        ft.replace(R.id.fragment_container, me);
        ft.commit();
    }

    /*
      更改点击是图标的显示状态
     */
    private void change_image_state(int imageId){
        for(int i = 0;i < fragment_images.size();i++){
            if(fragment_images.get(i).getId() == imageId){
                fragment_images.get(i).setImageResource(images_focus.get(i));
                fragment_texts.get(i).setTextColor(Color.parseColor("#008000"));
            }else{
                fragment_images.get(i).setImageResource(images_no_focus.get(i));
                fragment_texts.get(i).setTextColor(Color.parseColor("#A6A6A6"));
            }
        }
    }
}
