package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.myapplication.adapters.AlbumListAdapter;
import com.example.myapplication.adapters.IndicatorAdapter;
import com.example.myapplication.adapters.MainContextAdapter;
import com.example.myapplication.data.XimalayaDBHelper;
import com.example.myapplication.interfaces.IPlayerCallback;
import com.example.myapplication.presenters.PlayerPresenter;
import com.example.myapplication.presenters.RecommendPresenter;
import com.example.myapplication.utils.LogUtil;
import com.example.myapplication.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;


public class MainActivity extends FragmentActivity implements IPlayerCallback {

    private static final String TAG = "MainActivity";
    private MagicIndicator magicIndicator;
    private ViewPager mviewPager;
    private IndicatorAdapter mindicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private TextView mHeaderAuthor;
    private ImageView mPlatControl;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlContainer;
    private ImageView mSerchBtn;
    private AlbumListAdapter mAlbumListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       intView();
       intEvent();
       initPresenter();
       /* XimalayaDBHelper ximalayaDBHelper=new XimalayaDBHelper(this);
        ximalayaDBHelper.getWritableDatabase();*/

    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

    }

    private void intEvent() {
        mindicatorAdapter.setOnIndicatorTabClickListener(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void Ontabclick(int index) {
                if (mviewPager!=null) {
                    LogUtil.d(TAG,"click is ====>"+index);
                    mviewPager.setCurrentItem(index);
                }
                if(index==1){

                }
            }


        });
        mPlatControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (hasPlayList) {
                        if (!mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.play();

                        }else {
                            mPlayerPresenter.pause();
                        }

                    }else {
                        playFristRecommend();
                    }
                }


            }
        });
        mPlayControlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFristRecommend();
                }else {
                    startActivity(new Intent(MainActivity.this,PlayerActivity.class));
                }
            }
        });
        mSerchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 播放第一个专辑内容
     */
    private void playFristRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null&&currentRecommend.size()>0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAblumID(albumId);
        }
    }

    private void intView() {
        //绑定指示器布局
        magicIndicator = this.findViewById(R.id.main_indicator);
        //指示器颜色
        magicIndicator.setBackground(this.getResources().getDrawable(R.color.main_color));
        //设置icIndicator适配器
        mindicatorAdapter = new IndicatorAdapter (this);
        //通用指示器
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mindicatorAdapter);
        //viewpager
          mviewPager = this.findViewById(R.id.content_pager);
          FragmentManager supporFragmentManager=getSupportFragmentManager();
          ///适配器
          MainContextAdapter mainContextAdapter=new MainContextAdapter(supporFragmentManager);
          mviewPager.setAdapter(mainContextAdapter);
         //viewpager和magicIndicator绑定
         magicIndicator.setNavigator(commonNavigator);
         ViewPagerHelper.bind(magicIndicator, mviewPager);

         //播放控制相关
        mRoundRectImageView =this.findViewById(R.id.main_track_cover);
        mHeaderTitle = this.findViewById(R.id.main_track_title);
        mHeaderTitle.setSelected(true);
        mHeaderAuthor = this.findViewById(R.id.main_track_author);
        mPlatControl = this.findViewById(R.id.main_track_control);
        mPlayControlContainer = this.findViewById(R.id.main_play_control_container);
        //搜索
        mSerchBtn = this.findViewById(R.id.search_btn);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStrat() {
        updatePlayControl(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);

    }
    public void updatePlayControl(boolean isPlaying){
        if (mPlatControl != null) {
            mPlatControl.setImageResource(isPlaying?R.mipmap.track_pause:R.mipmap.track_play);
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

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgess, int totall) {

    }

    @Override
    public void onALoading() {

    }

    @Override
    public void onAFinished() {

    }

    @Override
    public void onTrackUpdata(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mHeaderTitle != null) {

                mHeaderTitle.setText(trackTitle);
            }
            if (mHeaderAuthor != null) {

                mHeaderAuthor.setText(nickname);
            }
            if (mRoundRectImageView != null) {

                Picasso.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
            }



        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAgainDetail(Track track, int playIndex) {

    }


}
