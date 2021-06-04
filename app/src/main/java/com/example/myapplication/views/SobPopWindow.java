package com.example.myapplication.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.PlayerListAdapter;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private static final String TAG = "PlayerActivity" ;
    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayerListAdapter mPlayerListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlauModeContainer;
    private PlayListActionListener mPlayListModeClickListener=null;
    private View mPlayOrederContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public SobPopWindow() {
        //设置它的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置setOutsideTouchable之前应该设置setBackgroundDrawable,否则无法点击外部关闭pop
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //载View进来
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        mTrackList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(linearLayoutManager);
        //创建适配器
        mPlayerListAdapter = new PlayerListAdapter();
        //设置适配器
        mTrackList.setAdapter(mPlayerListAdapter);
        //播放模式
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        //播放模式图标
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlauModeContainer = mPopView.findViewById(R.id.play_list_mode_container);
        mPlayOrederContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_play_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_play_order_tv);



    }

    private void initEvent() {
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
        mPlauModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayListModeClickListener != null) {
                    mPlayListModeClickListener.onPlayModeClick();
                }
            }
        });
        mPlayOrederContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表
                //TODO：切换播放列表为顺序或逆序
                LogUtil.d(TAG,"火焚毁22222");
                mPlayListModeClickListener.onOrederClick();

            }
        });

    }

    public  void setListData(List<Track> data){
        if (mPlayerListAdapter != null) {
            mPlayerListAdapter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position){
        //设置播放列表的当前播放位置
        if (mPlayerListAdapter != null) {
            mPlayerListAdapter.setCurrentIndexPosition(position);
            mTrackList.scrollToPosition(position);
        }
    }
    public void setPlayListItemClickListener(PlayListItemClickListener listener)
    {
        mPlayerListAdapter.setPlayListItemClickListener(listener);
    }

    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        int resId = R.drawable.selector_player_list;
           switch (currentMode)
        {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_player_list;
                mPlayModeTv.setText(R.string.play_list_mode_list);
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_list_loop;
                mPlayModeTv.setText(R.string.play_list_mode_list_loop);
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_random;
                mPlayModeTv.setText(R.string.play_list_mode_random);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_single_loop;
                mPlayModeTv.setText(R.string.play_list_mode_single_loop);
                break;
        }
        mPlayModeIv.setImageResource(resId);
    }

    public interface PlayListItemClickListener{
        void onItemClick(int position);
    }
    public void setPlayListActionListener(PlayListActionListener listener){
        this.mPlayListModeClickListener=listener;
    }

    public  interface PlayListActionListener {
        //播放模式被点击
        void onPlayModeClick();
        //播放顺序切换按钮被点击
        void onOrederClick();
    }
    //更新顺序或逆序
    public void updateOrderIcon(boolean isOrder){
        mOrderIcon.setImageResource(isOrder?R.drawable.selector_player_list:R.drawable.selector_player_re_order);
        mOrderText.setText(BaseApplication.getAppContext().getResources().getString(isOrder?R.string.order_txt:R.string.re_order_txt));
    }

}
