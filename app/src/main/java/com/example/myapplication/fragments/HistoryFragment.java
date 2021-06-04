package com.example.myapplication.fragments;


import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.PlayerActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.DetailLIstAdapter;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.data.IHistoryDaoCallbak;
import com.example.myapplication.interfaces.IHistoryCallback;
import com.example.myapplication.presenters.HistoryPresenter;
import com.example.myapplication.presenters.PlayerPresenter;
import com.example.myapplication.utils.LogUtil;
import com.example.myapplication.utils.ToastUtils;
import com.example.myapplication.views.ConfirmCheckBoxDialog;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public  class HistoryFragment extends BaseFragment implements IHistoryCallback, DetailLIstAdapter.ItemClickListener, DetailLIstAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogAlbumClickLisenter {


    private static final String TAG = "HistoryFragment";
    private UILoader mUiLoader;
    private DetailLIstAdapter mTrackLIstAdapter;
    private HistoryPresenter mHistoryPresenter;
    private ConfirmCheckBoxDialog mConfirmCheckBoxDialog;
    private Track mCurrentItemTrack=null;
    private List<Track> mCurrentItemTrackList=null;

    @Override
    protected View OnSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragement_history, container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {
                      View EmpytView=LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.fragement_empty_view,null);
                        TextView textView=EmpytView.findViewById(R.id.empty_tv);
                        textView.setText(getString(R.string.his_empty_tip));
                        textView.setTextColor(getResources().getColor(R.color.main_color));
                   return EmpytView;
                }
            };
        }else
            {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        if (mUiLoader != null&&mUiLoader.mCurrenStatus!= UILoader.UIStatus.SUCCESS&&mCurrentItemTrackList==null) {
            mUiLoader.updataStatus(UILoader.UIStatus.LOADING);
        }
        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View itemView=LayoutInflater.from(container.getContext()).inflate(R.layout.item_hisotry,container,false);
        TwinklingRefreshLayout twinklingRefreshLayout=itemView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableLoadmore(false);
        //Recyclerview
        RecyclerView historyList=itemView.findViewById(R.id.history_list);
        //设置布局管理器
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置adapter
        mTrackLIstAdapter = new DetailLIstAdapter();
        historyList.setAdapter(mTrackLIstAdapter);
        historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left= UIUtil.dip2px(view.getContext(),5);
                outRect.right= UIUtil.dip2px(view.getContext(),5);
            }
        });
        //presenter
        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mTrackLIstAdapter.setItemClickListener(this);
        mTrackLIstAdapter.setItemLongClickListener(this);
        mHistoryPresenter.listHistories();


        return itemView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }

    }


    @Override
    public void onHistoiesLoaded(List<Track> trackList) {
        this.mCurrentItemTrackList=trackList;
        if (mTrackLIstAdapter != null&&trackList.size()>0) {
            mTrackLIstAdapter.setData(trackList);
            mUiLoader.updataStatus(UILoader.UIStatus.SUCCESS);
        }else {
            mUiLoader.updataStatus(UILoader.UIStatus.ENPTY);
        }
        }

    @Override
    public void onItemClick(List<Track> mDetailData, int position) {
        PlayerPresenter playerPresenter = PlayerPresenter.getsPlayerPresenter();
        playerPresenter.setPlayList(mDetailData,position);
        Intent intent=new Intent(getContext(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
       // ToastUtils.makeText(BaseApplication.getAppContext(),"删除"+track.getTrackTitle());
        this.mCurrentItemTrack=track;
        mConfirmCheckBoxDialog = new ConfirmCheckBoxDialog(getActivity());
        mConfirmCheckBoxDialog.setOnDialogAlbumClickLisenter(this);
        mConfirmCheckBoxDialog.show();

    }


    @Override
    public void onConFirm(boolean isChecked) {
        if (mConfirmCheckBoxDialog != null&&mHistoryPresenter!=null) {
            if (isChecked){
                for (Track track : mCurrentItemTrackList) {
                    mHistoryPresenter.delHistory(track);
                }
            }else {
                mHistoryPresenter.delHistory(mCurrentItemTrack);

            }
        }
    }

    @Override
    public void onCancel() {

    }
}
