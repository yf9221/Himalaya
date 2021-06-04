package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {

    /**
     * 专辑详情页面内容加载出来了
     * @param tracks
     */
    void onDetailListloaded(List<Track> tracks);


    /**
     * 把album传给UI
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 把网络错误
     * @param
     */
    void onNetWorkError(int errorcode, String errormasg);

    /**
     * 上拉加载更多
     * @param size
     */
    void onLoadedMoreFinished(int size);

    /**
     * 下拉加载更多
     * @param size
     */
    void onRefreshFinished(int size);


}

