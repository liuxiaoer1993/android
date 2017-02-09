package com.ls.Util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;

/**
 * Created by qwe on 2016/4/23.
 */
public class AnimationUtil {

    public static void data_pane_animation_one(View target_one, final View target_two,final MapView mapView,
                                               int length,int to_length){
        int length_down = target_one.getHeight();
        ObjectAnimator one_down = ObjectAnimator.ofFloat(target_one, "translationY", length_down);
        one_down.setDuration(500);

        ObjectAnimator two_up = ObjectAnimator.ofFloat(target_two,"scaleY",0f,1f);
        two_up.setDuration(250);

        two_up.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                target_two.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final ValueAnimator mapView_strength = ValueAnimator.ofFloat(length,to_length);
        mapView_strength.setTarget(mapView);
        mapView_strength.setDuration(150);
        mapView_strength.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            ViewGroup.LayoutParams mapView_layoutParams;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) mapView_strength.getAnimatedValue();
                mapView_layoutParams = mapView.getLayoutParams();
                mapView_layoutParams.height = (int)value;
                mapView.setLayoutParams(mapView_layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(one_down).with(mapView_strength).before(two_up);
        animatorSet.start();
    }

    public static void data_pane_animation_two(View target_one, final View target_two,final MapView mapView,
                                               int length,int to_length){
        int length_down = target_one.getHeight();
        ObjectAnimator one_down = ObjectAnimator.ofFloat(target_one,"translationY",length_down,0);
        one_down.setDuration(500);

        ObjectAnimator two_up = ObjectAnimator.ofFloat(target_two,"scaleY",1f,0f);
        two_up.setDuration(250);
        two_up.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                target_two.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final ValueAnimator mapView_shorten = ValueAnimator.ofFloat(length,to_length);
        mapView_shorten.setTarget(mapView);
        mapView_shorten.setDuration(500);
        mapView_shorten.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            ViewGroup.LayoutParams mapView_layoutParams;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) mapView_shorten.getAnimatedValue();
                mapView_layoutParams = mapView.getLayoutParams();
                mapView_layoutParams.height = (int) value;
                mapView.setLayoutParams(mapView_layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(one_down).with(mapView_shorten).after(two_up);
        animatorSet.start();
    }

    public static void sport_button_animation_separate(View start_stop_button, final View finish_button){
        ObjectAnimator translation_left = ObjectAnimator.ofFloat(start_stop_button,"translationX",
                -start_stop_button.getWidth());
        translation_left.setDuration(250);

        ObjectAnimator translation_right = ObjectAnimator.ofFloat(finish_button,"translationX",
                start_stop_button.getWidth());
        translation_right.setDuration(250);

        translation_right.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                finish_button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translation_left, translation_right);
        animatorSet.start();
    }

    public static void sport_button_animation_compose(View start_stop_button, final View finish_button){
        ObjectAnimator translation_left = ObjectAnimator.ofFloat(start_stop_button,"translationX",
                -start_stop_button.getWidth(),0);
        translation_left.setDuration(250);

        ObjectAnimator translation_right = ObjectAnimator.ofFloat(finish_button,"translationX",
                finish_button.getWidth(),0);
        translation_right.setDuration(250);

        translation_right.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish_button.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translation_left, translation_right);
        animatorSet.start();
    }

    public static void line_view_data_animation(final View v,final TextView text,float real_length,final String s){
        ValueAnimator show_line_animator = ValueAnimator.ofFloat(0,real_length);
        show_line_animator.setTarget(v);
        show_line_animator.setDuration(1500);
        show_line_animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (v instanceof LineView) {
                    //Log.d("AnimationUtil","v is LineView and value is "+value);
                    ((LineView) v).setLength(value);

                }
            }
        });
        show_line_animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                text.setVisibility(View.VISIBLE);
                text.setText(s);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator text_animator = ObjectAnimator.ofFloat(text,"alpha",0,1);
        text_animator.setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.play(show_line_animator).before(text_animator);
        set.start();
    }

    public static int dp2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
