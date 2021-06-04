package com.example.myapplication.presenters;

import com.example.myapplication.data.XimalayaApi;
import com.example.myapplication.interfaces.IRecommedViewCallback;
import com.example.myapplication.interfaces.IRecommendPresenter;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {


    private static final String TAG = "RecommendPresenter";
    private List<IRecommedViewCallback> mcallbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend=null;

    private RecommendPresenter() {

    }

    private static RecommendPresenter sInstance = null;

    /**
     * 获取单例模式
     *
     * @return
     */

    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();

                }

            }
        }


        return sInstance;
    }

    /**
     * 获取当前推荐专辑列表
     * @return
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }
    /**
     * 获取推荐内容,猜你喜欢
     * 接口:3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //封装数据
        updataloading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList( new IDataCallBack<GussLikeAlbumList>() {
           @Override
           public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
               //获取成功
               if (gussLikeAlbumList != null) {
                   List<Album> albumList = gussLikeAlbumList.getAlbumList();
                   //获取数据后更新UI
                   handlerRecommendResult(albumList);

               }
               LogUtil.d(TAG, "");

           }

           @Override
           public void onError(int i, String s) {
               //获取失败
               LogUtil.d(TAG, "error=====>" + i);
               LogUtil.d(TAG, "error massage=====>" + s);
               handlerError();
           }
       });
    }

    private void handlerError() {
        if (mcallbacks!=null) {
            for (IRecommedViewCallback callback : mcallbacks) {
                callback.onNetworkError();
            }

        }

    }

    /**
     * 获取推荐内容,猜你喜欢
     * 接口:3.10.6 获取猜你喜欢专辑
     */

    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommedViewCallback callback : mcallbacks) {
                    callback.onEmpty();
                }
            }//通知UI
            else
            {
                for (IRecommedViewCallback callback : mcallbacks) {
                    callback.onRecommendListLoad(albumList);
                }
              this.mCurrentRecommend=albumList;
            }

        }

    }



    private void updataloading(){
        for (IRecommedViewCallback callback : mcallbacks) {
            callback.onLoading();
        }
    }


    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommedViewCallback callback) {
        if (mcallbacks!=null&&!mcallbacks.contains(callback)) {
            mcallbacks.add(callback);
        }
    }


    @Override
    public void unRegisterViewCallback(IRecommedViewCallback callback) {
        if (mcallbacks!=null) {
            mcallbacks.remove(mcallbacks);

        }
    }
}
