package com.ls.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by qwe on 2016/5/11.
 */
public class LineView extends View {
    private Paint mPaint;
    //private RectF mLineRectF;
    private float right = 100;
    private final float LEFT = 0;
    private final float TOP = 0;
    private final float BOTTOM = 20;
    private SpeedHandler mHandler;

    private int mMeasureHeigth;
    private int mMeasureWidth;


    public LineView(Context context){
        super(context);
        init();
    }

    public LineView(Context context,AttributeSet attr){
        super(context,attr);
        init();
    }

    public LineView(Context context,AttributeSet attr,int defStyleAttr){
        super(context, attr, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FFA500"));
        mPaint.setAntiAlias(true);
        right = 10;
        mHandler = new SpeedHandler(this);
        //mLineRectF = new RectF(0,0,length,10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("LineView","onDraw()");
        canvas.drawRect(LEFT,TOP,right,BOTTOM,mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
//        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
//        mMeasureHeigth = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    public void setLength(float length){
        if (length != 0){
            this.right = length;
        }else if(length > AnimationUtil.dp2px(getContext(),220)){
            this.right = AnimationUtil.dp2px(getContext(),220);
        }else{
            this.right = 10;
        }
        invalidate();
       //mHandler.sendEmptyMessage(0);
    }

    private int measureWidth(int measureSpec){
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = 10;
            if(specMode == MeasureSpec.AT_MOST){
                result = 10;
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec){
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = 10;
            if(specMode == MeasureSpec.AT_MOST){
                result = 10;
            }
        }

        return result;
    }

    private static class SpeedHandler extends Handler {

        private LineView act;

        public SpeedHandler(LineView act) {
            super();
            this.act = act;
        }

        @Override
        public void handleMessage(Message msg) {
            act.postInvalidateDelayed(0);
            Log.d("LineView", "invalidate()");
            super.handleMessage(msg);
        }

    }
}
