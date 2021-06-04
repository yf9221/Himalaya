package com.example.myapplication;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.adapters.PlayTrackPagerAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.interfaces.IPlayerCallback;
import com.example.myapplication.presenters.PlayerPresenter;
import com.example.myapplication.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG ="PlayerActivity" ;
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat=new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat=new SimpleDateFormat("hh:mm:ss");
    private TextView mTotallDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int  mCurrentProgress=0;
    private boolean mIsUserTouchProgessBar=false;
    private ImageView mPlayBtnNext;
    private ImageView mPlayBtnPre;
    private TextView mTrackTitle;
    private ViewPager mTrackPageView;
    private PlayTrackPagerAdapter mPlayTrackPagerAdapter;
    private boolean mIsUserSlidePager=false;
    private ImageView mPlayModeSwitchBtn;
    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModeRule=new HashMap<>();
    private XmPlayListControl.PlayMode mCurrentMode= XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    //处理播放模式的切换
    //1.默认的是列表播放PLAY_MODEL_LIST
    //2.列表循环PLAY_MODEL_LIST_LOOP
    //3.随机播放PLAY_MODEL_RANDOM
    //4.单曲循环播放PLAY_MODEL_SINGLE_LOOP
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private View mPlayListTableBtn;
    private SobPopWindow mPopWindow;
    private ValueAnimator mEnterAnimation;
    private ValueAnimator mOutBgAnimator;
    public final int BG_ANIMATION_DURATION=500;
    private boolean mFristInto =true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        //测试
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        //mPlayerPresenter.play();
        mPlayerPresenter.getPlayList();
        initEven();
        initBgAnimation();




    }

    private void initBgAnimation() {
        mEnterAnimation = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterAnimation.setDuration(BG_ANIMATION_DURATION);
        mEnterAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                updataBgAlpha(animatedValue);
            }
        });
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                updataBgAlpha(animatedValue);
            }
        });

    }


    @SuppressLint("ClickableViewAccessibility")
    private void initEven() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果现在下状态正在播放的，那么就暂停
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                }
                //如果现在下状态是暂停的，那么就播放
               else {
                    mPlayerPresenter.play();
                }


            }
        });
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                      if(fromUser){
                         mCurrentProgress=progress;
                      }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgessBar=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                   //手离开拖动进度条更新进度
                mPlayerPresenter.seekTo(mCurrentProgress);
                mIsUserTouchProgessBar=false;
            }
        });
        mPlayBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一首
                //TODO
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();

                }

            }
        });
        mPlayBtnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放上一首
                //TODO
                if (mPlayerPresenter != null) {

                    mPlayerPresenter.playPre();
                }

            }
        });
        mTrackPageView.addOnPageChangeListener(this);
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager=true;
                        break;

                }
                return false;
            }
        });
        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据当前mode播放下一个mode
                swichPlayMode();
            }
        });
        mPlayListTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //pop弹出改变背景
                //updataBgAlpha(0.7f);
                mEnterAnimation.start();

            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop消失恢复背景
                mOutBgAnimator.start();
            }
        });
        mPopWindow.setPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPlayerPresenter.playByIndex(position);
            }
        });
        mPopWindow.setPlayListActionListener(new SobPopWindow.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                swichPlayMode();
            }

            @Override
            public void onOrederClick() {
               //点击了切换顺序和逆序

                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();

                }
            }
        });


    }
    private boolean mtestorder=false;

    private void swichPlayMode() {
        XmPlayListControl.PlayMode playMode=sPlayModeRule.get(mCurrentMode);
        mCurrentMode=playMode;
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.swichPlayMode(mCurrentMode);
         }
    }

    public void updataBgAlpha(float alpha){
        Window window=getWindow();
        WindowManager.LayoutParams attributes=window.getAttributes();
        attributes.alpha=alpha;
        window.setAttributes(attributes);

    }

    /**
     * 根据当前的状态，改变模式图标
     * 1.默认的是列表播放PLAY_MODEL_LIST
     * 2.列表循环PLAY_MODEL_LIST_LOOP
     * 3.随机播放PLAY_MODEL_RANDOM
     * 4.单曲循环播放PLAY_MODEL_SINGLE_LOOP
     */
    private void updataPlayModeBtnImg() {
        int resId = R.drawable.selector_player_list;
         switch (mCurrentMode)
         {
             case PLAY_MODEL_LIST:
                 resId = R.drawable.selector_player_list;
                 break;
             case PLAY_MODEL_LIST_LOOP:
                 resId = R.drawable.selector_player_list_loop;
                 break;
             case PLAY_MODEL_RANDOM:
                 resId = R.drawable.selector_player_random;
                 break;
             case PLAY_MODEL_SINGLE_LOOP:
                 resId = R.drawable.selector_player_single_loop;
                 break;
         }
         mPlayModeSwitchBtn.setImageResource(resId);

    }

    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotallDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar=this.findViewById(R.id.duration_bar);
        mPlayBtnNext = this.findViewById(R.id.play_next);
        mPlayBtnPre = this.findViewById(R.id.play_pre);
        mTrackTitle = this.findViewById(R.id.track_titile);
        mTrackPageView = this.findViewById(R.id.track_page_view);
        mPlayModeSwitchBtn = this.findViewById(R.id.play_mode_switch_btn);

        //创建设配器
        mPlayTrackPagerAdapter = new PlayTrackPagerAdapter();
        //设置设配器
         mTrackPageView.setAdapter(mPlayTrackPagerAdapter);
         //设置播放列表
        mPlayListTableBtn = this.findViewById(R.id.player_list_table);
        mPopWindow = new SobPopWindow();





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayerPresenter!=null){
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter=null;
        }
    }

    @Override
    public void onPlayStrat() {
     //开始播放,修改UI曾暂停的按钮
        if (mPlayerPresenter != null) {
            mControlBtn.setImageResource(R.mipmap.track_pause);
        }
    }

    @Override
    public void onPlayPause() {
        //播放暂停,修改UI成播放的按钮
        if (mPlayerPresenter != null) {
            mControlBtn.setImageResource(R.mipmap.track_play);
        }

    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onnextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list,int playIndex) {
        //数据设置给适配器
        if (mPlayTrackPagerAdapter != null) {
            mPlayTrackPagerAdapter.setData(list);
            if(playIndex!=mTrackPageView.getCurrentItem()){
                  mTrackPageView.setCurrentItem(playIndex);

            }
        }
        //数据给节目列表一份
        if (mPopWindow != null) {
            mPopWindow.setListData(list);
        }

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式，并修改UI
        mCurrentMode=playMode;
        updataPlayModeBtnImg();
        mPopWindow.updatePlayMode(mCurrentMode);
    }

    @Override
    public void onProgressChange(int currentDuration, int totall) {
        mDurationBar.setMax(totall);
        String totallDuration;
        String currentPosition;
             //更新播放进度,更新进度条
        if (totall>1000*60*60) {
             totallDuration = mHourFormat.format(totall);
             currentPosition = mHourFormat.format(currentDuration);
        }else {
             totallDuration = mMinFormat.format(totall);
             currentPosition= mMinFormat.format(currentDuration);
        }
        if(mTotallDuration!=null){
            mTotallDuration.setText(totallDuration);
        }
        if(mCurrentPosition!=null){
            mCurrentPosition.setText(currentPosition);
        }
        //更新当前时间
        //更新进度
       if (!mIsUserTouchProgessBar) {
           mDurationBar.setProgress(currentDuration);
        }

    }

    @Override
    public void onALoading() {

    }

    @Override
    public void onAFinished() {

    }

    @Override
    public void onTrackUpdata(Track track,int playIndex) {

        if (mTrackTitle != null) {
            mTrackTitle.setText(track.getTrackTitle());
        }
        if (mTrackPageView != null&&mFristInto==false) {
            mTrackPageView.setCurrentItem(playIndex,true);

        }
        if (mPopWindow != null) {
            mPopWindow.setCurrentPlayPosition(playIndex);
        }
        mFristInto = false;
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mPopWindow.updateOrderIcon(!isReverse);

    }

    @Override
    public void onAgainDetail(Track track,int playIndex) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {


      //当页面选择时，就去切换播放内容
        if (mPlayerPresenter != null&& mIsUserSlidePager==true) {

            mPlayerPresenter.playByIndex(position);
           }
        mIsUserSlidePager=false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
