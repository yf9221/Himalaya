package com.example.myapplication.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {
    //旋转角度
    private int rotateDegree=0;

    private  boolean mNeetRotate=false;
    public LoadingView(Context context) {

        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {

        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeetRotate=true;
         //绑定到window时候
        post(new Runnable() {
            @Override
            public void run() {
                rotateDegree+=30;
                rotateDegree=rotateDegree<=360?rotateDegree:0;
                invalidate();
                //是否继续旋转
                if(mNeetRotate){
                    postDelayed(this,100);
                }


            }
        });
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mNeetRotate=false;
        //windows解绑的时候
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 第一个参数是旋转角度
         * 第二个参数是旋转的x坐标
         * 第三个参数是旋转的y坐标
         */
        canvas.rotate(rotateDegree,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
