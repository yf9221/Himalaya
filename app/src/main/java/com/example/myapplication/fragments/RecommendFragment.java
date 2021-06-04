package com.example.myapplication.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.RecommendListAdapter;
import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.interfaces.IRecommedViewCallback;
import com.example.myapplication.presenters.AlbumdetailPresenter;
import com.example.myapplication.presenters.RecommendPresenter;
import com.example.myapplication.utils.LogUtil;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import java.util.List;


public class RecommendFragment extends BaseFragment implements IRecommedViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.RecommendItemClickListener {
    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader muiLoader;

    @Override
    protected View OnSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        muiLoader = new UILoader(getContext()) {
            //getSuccessView获取正在加载页面
            @Override
            protected View getSuccessView(ViewGroup containder) {
                return createSuccessView(layoutInflater,containder);
            }
        };
        //        //獲取到逻辑层对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();
        //解除绑定
        if (muiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) muiLoader.getParent()).removeView(muiLoader);
        }
        muiLoader.setOnRetryClickListener(this);
        return muiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater,ViewGroup container) {
        //VIEW加载完成
        rootView = layoutInflater.inflate(R.layout.fragement_recommend, container,false);
        //Recycleview的使用
        //1.找到控件
        mRecommendRv = rootView.findViewById(R.id.recommend_list);
        TwinklingRefreshLayout twinklingRefreshLayout=rootView.findViewById(R.id.over_scroll);
        twinklingRefreshLayout.setPureScrollModeOn();
        //2.设置布局
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left= UIUtil.dip2px(view.getContext(),5);
                outRect.right= UIUtil.dip2px(view.getContext(),5);

            }
        });
        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setRecommendItemClickListner(this);
        return rootView;
    }


    @Override
    public void onRecommendListLoad(List<Album> result) {
         //当我们获取到推荐内容，这个方法会被调用(成功了)
        //当数据回来以后更新内容
        mRecommendListAdapter.setdata(result);
        muiLoader.updataStatus(UILoader.UIStatus.SUCCESS);
        LogUtil.d(TAG,"SUCCESS");
    }

    @Override
    public void onNetworkError() {
        LogUtil.d(TAG,"onNetworkError");
        muiLoader.updataStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        muiLoader.updataStatus(UILoader.UIStatus.ENPTY);
        LogUtil.d(TAG,"onEmpty");
    }

    @Override
    public void onLoading() {
        //在逻辑层获取数据前加载
        muiLoader.updataStatus(UILoader.UIStatus.LOADING);
        LogUtil.d(TAG,"onLoading");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册
        if (mRecommendPresenter!=null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void OnRetryclick() {
        if(mRecommendPresenter!=null){
            mRecommendPresenter.getRecommendList();
          }
    }

    @Override
    public void OnItemClick(int position,Album album) {
        //根据位置拿到数据
        AlbumdetailPresenter.getInstance().setTargeAlbum(album);
        //Item被点击，转到详情页面
        Intent intent=new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
