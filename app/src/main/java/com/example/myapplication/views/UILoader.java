package com.example.myapplication.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View mloadingview;
    private View mSuccessView;
    private View mNetworkError;
    private View mEmptyview;
    private OnRetryClickListener mOnRetryClickListener=null;

    public  enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,ENPTY,NONE
    }

    public  UIStatus mCurrenStatus=UIStatus.NONE;
    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void updataStatus(UIStatus status){
          mCurrenStatus=status;
          //更新UI一定要在主线程上
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                swichUIByCurrentStatus();
            }
        });
    }
    /**
     *
     * 初始化UI
     */
    private void init() {
        swichUIByCurrentStatus();

    }

    private void swichUIByCurrentStatus() {
        //加载中
        if (mloadingview == null) {
            mloadingview = getLodingView();
            addView(mloadingview);
        }
        //根据状态设置是否可见
        mloadingview.setVisibility(mCurrenStatus==UIStatus.LOADING?VISIBLE:GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrenStatus==UIStatus.SUCCESS?VISIBLE:GONE);
        //网络错误
        if (mNetworkError == null) {
            mNetworkError = getNetworkError();
            addView(mNetworkError);
        }
        //根据状态设置是否可见
        mNetworkError.setVisibility(mCurrenStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);
        //数据为空
        if (mEmptyview == null) {
            mEmptyview = getEmptyView();
            addView(mEmptyview);
        }
        //根据状态设置是否可见
        mEmptyview.setVisibility(mCurrenStatus==UIStatus.ENPTY?VISIBLE:GONE);
    }

    protected View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragement_empty_view,this,false);
    }

    protected View getNetworkError() {
        View NetworkError=LayoutInflater.from(getContext()).inflate(R.layout.fragement_error_view,this,false);
        NetworkError.findViewById(R.id.error).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(mOnRetryClickListener!=null){
                    mOnRetryClickListener.OnRetryclick();
                }
            }
        });

        return NetworkError;
    }
   public void setOnRetryClickListener(OnRetryClickListener listener){
        this.mOnRetryClickListener=listener;

   }
   public  interface  OnRetryClickListener{
         void OnRetryclick();
   }
    protected abstract View getSuccessView(ViewGroup container);

    protected View getLodingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragement_loading_view,this,false);
    }
}
