package com.example.myapplication.presenters;

import com.example.myapplication.data.XimalayaApi;
import com.example.myapplication.interfaces.IAlbumDetailPresenter;
import com.example.myapplication.interfaces.IAlbumDetailViewCallback;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumdetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG ="AlbumdetailPresenter" ;
    private Album mTargeAlbum=null;
    private List<IAlbumDetailViewCallback> mCallbacks=new ArrayList<>();
    private int mCurrentId=-1;
    private int mCurrentPageIndex=0;
    private List<Track> mTracks=new ArrayList<>();


    private AlbumdetailPresenter (){

    }
    private static AlbumdetailPresenter sInstance=null;

    public static AlbumdetailPresenter getInstance(){

        if (sInstance==null) {
            synchronized (AlbumdetailPresenter.class){

                if(sInstance==null){
                    sInstance=new AlbumdetailPresenter();
                }
            }
        }
        return sInstance;
    }
    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        mCurrentPageIndex++;
        //传入ture表示结果放到后面
        doloaded(true);


    }

    public void doloaded(final boolean isLoadedMore){
        XimalayaApi ximalayaApi=XimalayaApi.getXimalayaApi();
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, mCurrentId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, mCurrentPageIndex+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        ximalayaApi.getAblumDetai(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks=trackList.getTracks();
                    if (isLoadedMore) {
                        //上拉加载结果放到后面去
                        mTracks.addAll(tracks);
                        int size=tracks.size();
                        handlerLoadMoreResult(size);
                    }else {
                        //下拉加载结果放到前面
                        mTracks.addAll(0,tracks);

                    }

                    LogUtil.d(TAG,"tracks size---->"+tracks.size());
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                mCurrentPageIndex--;
                handlerOnNetworkError(i,s);
                LogUtil.d(TAG,"error---->"+i+"errorMsg"+s);
            }
        },mCurrentId,mCurrentPageIndex);

    }

    private void handlerLoadMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoadedMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentId=albumId;
        this.mCurrentPageIndex=page;
        doloaded(false);


    }

    private void handlerOnNetworkError(int errorcode, String errormasg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetWorkError(errorcode,errormasg);
        }
    }


    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListloaded(tracks);
        }
    }

    /**
     * 注册UI通知接口
     ** @param detailViewCallback
     */
    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            if(mTargeAlbum!=null) {
                detailViewCallback.onAlbumLoaded(mTargeAlbum);
            }
        }

    }

    /**
     * 删除UI通知的接口
     * @param detailViewCallback
     */
    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
              mCallbacks.remove(detailViewCallback);

    }

    public  void setTargeAlbum(Album TargeAlbum){
        this.mTargeAlbum=TargeAlbum;

    }


}
