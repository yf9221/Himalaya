package com.example.myapplication.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.AlbumListAdapter;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.interfaces.ISubscriptionCallback;
import com.example.myapplication.interfaces.ISubscriptionPresenter;
import com.example.myapplication.presenters.AlbumdetailPresenter;
import com.example.myapplication.presenters.SubscriptionPresenter;
import com.example.myapplication.utils.ToastUtils;
import com.example.myapplication.views.ConfirmDialog;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.Collections;
import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnItemClickListener, AlbumListAdapter.OnLongClickListener {

    private static final String TAG = "SubscriptionFragment";
    private ISubscriptionPresenter mSubscriptionpresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlbumListAdapter;
    private TwinklingRefreshLayout mTwinklingRefreshLayout;
    private int mCurrentDelPostion;
    private Album mCurrentDelAlbum;
    private boolean mIsCreate=false;
    private UILoader mUiLoader;

    @Override
    protected View OnSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {

        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragement_subscription, container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    View EmpytView=LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.fragement_empty_view,null);
                    TextView textView=EmpytView.findViewById(R.id.empty_tv);
                    textView.setText(getString(R.string.sub_empty_tip));
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                    return EmpytView;
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }
        if (mUiLoader != null) {
            mUiLoader.updataStatus(UILoader.UIStatus.LOADING);
        }

         return rootView;


    }

    private View createSuccessView() {
        View itemView=LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_subscription,null);
        mSubscriptionpresenter = SubscriptionPresenter.getInstance();
        mSubscriptionpresenter.registerViewCallback(this);
        mSubscriptionpresenter.getSubscriptionList();
        mTwinklingRefreshLayout = itemView.findViewById(R.id.over_scroll_view);
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableLoadmore(false);
        //订阅recyclerview
        mSubListView = itemView.findViewById(R.id.subscription_list);
        LinearLayoutManager manager=new LinearLayoutManager(itemView.getContext());
        mSubListView.setLayoutManager(manager);
        mAlbumListAdapter = new AlbumListAdapter();
        mSubListView.setAdapter(mAlbumListAdapter);
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top=(UIUtil.dip2px(view.getContext(),5));
                outRect.bottom=(UIUtil.dip2px(view.getContext(),5));
                outRect.left=(UIUtil.dip2px(view.getContext(),5));
                outRect.right=(UIUtil.dip2px(view.getContext(),5));
            }
        });
        mAlbumListAdapter.setItemClickListener(this);
        mAlbumListAdapter.setOnLongClickListener(this);
        return itemView;
    }


    @Override
    public void onAddResult(boolean isSuccess, Album currentAlbum) {
        if (isSuccess) {
            try {
                mAlbumListAdapter.addData(currentAlbum,mAlbumListAdapter.getItemCount());

            }catch (Exception e){

            }
        }
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

        if (isSuccess) {
              mAlbumListAdapter.delData(mCurrentDelPostion);
              mCurrentDelPostion = 0;
            ToastUtils.makeText(BaseApplication.getAppContext(),"取消订阅成功");
        }
        if (mUiLoader != null) {
            if (mAlbumListAdapter.getItemCount()==0) {
                mUiLoader.updataStatus(UILoader.UIStatus.ENPTY);
            }
        }
    }

    @Override
    public void onSubscriptionLoaded(List<Album> albums) {
              //UI更新
        if (albums.size()==0) {
            mUiLoader.updataStatus(UILoader.UIStatus.ENPTY);
        }else {
            mUiLoader.updataStatus(UILoader.UIStatus.SUCCESS);
        }
        if (mAlbumListAdapter != null) {
             mAlbumListAdapter.setdata(albums);
        }
    }

    @Override
    public void onSubscriptionFull(boolean isFull) {

    }

    @Override
    public void OnItemClick(int position, Album album) {
        mCurrentDelAlbum = album;
        mCurrentDelPostion = position;
        AlbumdetailPresenter.getInstance().setTargeAlbum(album);
        Intent intent=new Intent(getContext(),DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
         if (mSubscriptionpresenter != null) {
            mSubscriptionpresenter.unRegisterViewCallback(this);
        }
        super.onDestroy();
    }

    @Override
    public void OnLongCItemClick(final Album album,int postion) {
        //长按订阅
        mCurrentDelAlbum = album;
        mCurrentDelPostion = postion;
        if (mSubscriptionpresenter != null) {
              //Toast.makeText(BaseApplication.getAppContext(),"取消订阅成功",Toast.LENGTH_SHORT).show();
                ConfirmDialog confirmDialog=new ConfirmDialog(getActivity());
                confirmDialog.show();
                confirmDialog.setOnDialogAlbumClickLisenter(new ConfirmDialog.OnDialogAlbumClickLisenter() {
                    @Override
                    public void onDialogCancel() {
                        mSubscriptionpresenter.deleteSubscription(album);

                    }

                    @Override
                    public void onDialogSub() {

                    }
                });

        }
    }


}
