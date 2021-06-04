package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface  ISubscriptionCallback {


    /**
     * 添加结果回调
     * @param isSuccess
     * @param currentAlbum
     */
    void onAddResult(boolean isSuccess, Album currentAlbum);


    /**
     * 删除结果回调
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);


    /**
     * 订阅专辑加载回调
     * @param albums
     */
    void onSubscriptionLoaded(List<Album> albums);

    /**
     * 订阅数已满
     * @param isFull
     */
    void onSubscriptionFull(boolean isFull);
}
