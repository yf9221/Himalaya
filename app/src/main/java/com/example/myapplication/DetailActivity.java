package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.DetailLIstAdapter;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.interfaces.IAlbumDetailViewCallback;
import com.example.myapplication.interfaces.IPlayerCallback;
import com.example.myapplication.interfaces.ISubscriptionCallback;
import com.example.myapplication.presenters.AlbumdetailPresenter;
import com.example.myapplication.presenters.PlayerPresenter;
import com.example.myapplication.presenters.SubscriptionPresenter;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.ImageBlur;
import com.example.myapplication.utils.LogUtil;
import com.example.myapplication.utils.ToastUtils;
import com.example.myapplication.views.RoundRectImageView;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;


public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailLIstAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumdetailPresenter mAlbumdetailPresenter;
    private int mCurrentPage=1;
    private RecyclerView mDetailList;
    private DetailLIstAdapter mDetailLIstAdapter;
    private FrameLayout mDetailListContatiner;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mDetailPlayControlIv;
    private TextView mDetailPlayControlTv;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTrack;
    private final static int  DEFAULT_PLAY_NDEX=0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle=null;
    private TextView mDetailSubBt;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        intView();
        initPresenter();
        //获取专辑详情内容
        initListener();
        updataSubSate();
        updatePlaySate(mPlayerPresenter.isPlaying());

    }

    private void updataSubSate() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mDetailSubBt.setText(isSub?R.string.cancel_sup_tips_text:R.string.sup_tips_text);
        }
    }

    private void initPresenter() {
        mAlbumdetailPresenter = AlbumdetailPresenter.getInstance();
        mAlbumdetailPresenter.registerViewCallback(this);
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter. getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }


    private void initListener() {
        if (mDetailPlayControlIv != null) {
            mDetailPlayControlIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mPlayerPresenter != null) {
                        boolean has=mPlayerPresenter.hasPlayList();
                        if (has) {
                            handlerPlayControl();
                        }else {
                            handleNoPlayList();
                        }
                    }


                }
            });
        }
        mDetailSubBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                 boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);

                    }else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);

                    }

                }

            }
        });

    }

    /**
     * 播放器没有播放内容进行处理
     */
    private void handleNoPlayList() {
           mPlayerPresenter.setPlayList(mCurrentTrack,DEFAULT_PLAY_NDEX);
    }

    private void handlerPlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            //正在播放就暂停
            mPlayerPresenter.pause();
        }else {
            mPlayerPresenter.play();
        }
    }

    private void intView() {
        mDetailListContatiner = this.findViewById(R.id.detai_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {

                    return createSuccessView(container);
                }
            };
            if(mUiLoader.getParent() instanceof ViewGroup){
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            mDetailListContatiner.removeAllViews();
            mDetailListContatiner.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover=this.findViewById(R.id.iv_small_cover);
        mAlbumTitle =this.findViewById(R.id.tv_ablum_title);
        mAlbumAuthor =this.findViewById(R.id.tv_ablum_author);
          //控制图标播放的按钮
        mDetailPlayControlIv = this.findViewById(R.id.detail_play_control_iv);
        mDetailPlayControlTv = this.findViewById(R.id.detail_play_control_tv);
        mDetailPlayControlTv.setSelected(true);
        //订阅相关的
        mDetailSubBt = this.findViewById(R.id.detail_sub_btn);


    }

    private boolean mIsLoadedMore=false;

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        BezierLayout bezierLayout=new BezierLayout(this);
        mRefreshLayout.setHeaderView(bezierLayout);
        mRefreshLayout.setMaxHeadHeight(140);
        //设置布局管理器
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);

        mDetailLIstAdapter = new DetailLIstAdapter();
        mDetailList.setAdapter(mDetailLIstAdapter);
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left= UIUtil.dip2px(view.getContext(),5);
                outRect.right= UIUtil.dip2px(view.getContext(),5);
            }
        });
        mDetailLIstAdapter.setItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this,"开始下拉刷新",Toast.LENGTH_LONG).show();
                        mRefreshLayout.finishRefreshing();
                    }
                },2000);

            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //TODO：去加载更多
                if (mAlbumdetailPresenter != null) {
                    mAlbumdetailPresenter.loadMore();
                    mIsLoadedMore=true;
                }
            }
        });

        return detailListView;
    }

    @Override
    public void onDetailListloaded(List<Track> tracks) {
        if (mIsLoadedMore&&mRefreshLayout!=null) {
               mIsLoadedMore=false;
               mRefreshLayout.finishLoadmore();
        }
        this.mCurrentTrack=tracks;
        //更新或者设置UI
        if (tracks == null&&tracks.size()==0) {
            if (mUiLoader != null) {

                mUiLoader.updataStatus(UILoader.UIStatus.ENPTY);
            }
        }
        if (mUiLoader != null) {

            mUiLoader.updataStatus(UILoader.UIStatus.SUCCESS);
            mDetailLIstAdapter.setData(tracks);
            }




    }

    @Override
    public void onAlbumLoaded(Album album) {
        mCurrentAlbum=album;
        long id=album.getId();
        mCurrentId=id;
        if(mAlbumdetailPresenter!=null) {
            mAlbumdetailPresenter.getAlbumDetail((int) id, mCurrentPage);
        }
        //拿数据，显示Loading状态
        if (mUiLoader != null) {

            mUiLoader.updataStatus(UILoader.UIStatus.LOADING);
        }
        if(mAlbumTitle!=null){
            mAlbumTitle.setText(album.getAlbumTitle());

        }
        if(mSmallCover!=null){
            LogUtil.d(TAG,album.getCoverUrlSmall()+"");
            Picasso.with(this).load(album.getCoverUrlSmall()).into(mSmallCover);


        }
        if(mAlbumAuthor!=null){
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());

        }
        //高斯模糊
        //TODO
        if(mLargeCover!=null&&null!=mLargeCover){
              Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                    @Override
                    public void onSuccess() {
                        Drawable drawable = mLargeCover.getDrawable();
                        if (drawable != null) {
                            ImageBlur.makeBlur(mLargeCover,DetailActivity.this);
                        }
                    }

                    @Override
                    public void onError() {
                        LogUtil.d(TAG,"onError");
                    }
                });


        }

    }

    @Override
    public void onNetWorkError(int errorcode, String errormasg) {
        if (mUiLoader != null) {
            mUiLoader.updataStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onLoadedMoreFinished(int size) {
           if(size>0){
               ToastUtils.makeText(this,"加载更多成功,有"+size+"条");
           }else {

               ToastUtils.makeText(this,"没有更多数据");
           }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void OnRetryclick() {
        if(mAlbumdetailPresenter!=null) {
            mAlbumdetailPresenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }


    @Override
    public void onItemClick(List<Track> mDetailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getsPlayerPresenter();
        playerPresenter.setPlayList(mDetailData,position);
        Intent intent=new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }

    //修改播放图标和文字
    private void updatePlaySate(boolean isPlaying) {
        if (mDetailPlayControlTv != null&&mDetailPlayControlIv!=null) {
            mDetailPlayControlIv.setImageResource(isPlaying?R.drawable.selector_detail_pause:R.drawable.selector_detail_play);
        }
        if (!isPlaying) {
            mDetailPlayControlTv.setText(BaseApplication.getAppContext().getResources().getString(R.string.detail_pause_txt));

        }else {
            if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                mDetailPlayControlTv.setText(mCurrentTrackTitle);
            }
        }
    }
    @Override
    public void onPlayStrat() {
        //修改图标为暂停,文字改为正在播放
        updatePlaySate(true);
    }

    @Override
    public void onPlayPause() {
        //修改图标为播放,文字改为继续播放
        updatePlaySate(false);

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
        mCurrentTrackTitle = track.getTrackTitle();
        if (!TextUtils.isEmpty(mCurrentTrackTitle)&&mDetailPlayControlTv!=null) {
            mDetailPlayControlTv.setText(mCurrentTrackTitle);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }



    @Override
    public void onAgainDetail(Track track,int playIndex) {
        if (mCurrentTrackTitle == null) {
            mCurrentTrackTitle = track.getTrackTitle();
        }
    }

    @Override
    public void onAddResult(boolean isSuccess, Album currentAlbum) {
        if (isSuccess) {
            //如果成功,修改UI成取消订阅
            mDetailSubBt.setText(R.string.cancel_sup_tips_text);
        }
        String success=isSuccess?"订阅成功":"订阅失败";
        ToastUtils.makeText(DetailActivity.this,success);
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功,修改UI成取消订阅
            mDetailSubBt.setText(R.string.sup_tips_text);
        }
        String success=isSuccess?"取消订阅成功":"取消订阅失败";
        ToastUtils.makeText(DetailActivity.this,success);

    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {
        for (Album album : albums) {

          LogUtil.d(TAG,"albums--->"+album.getAlbumTitle());
        }
    }

    @Override
    public void onSubscriptionFull(boolean isFull) {
        if(isFull){
            ToastUtils.makeText(this,"订阅已到达上限"+ Constants.SUB_MAX_COUNT+"个");
        }
    }

    @Override
    protected void onDestroy() {
        if (mSubscriptionPresenter != null) {

            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        if (mAlbumdetailPresenter != null) {

            mAlbumdetailPresenter.unRegisterViewCallback(this);
        }
        if (mPlayerPresenter != null) {

            mPlayerPresenter.unRegisterViewCallback(this);
        }
        super.onDestroy();
    }
}
