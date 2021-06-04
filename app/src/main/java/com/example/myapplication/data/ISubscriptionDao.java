package com.example.myapplication.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubscriptionDao {

    void setCallback(ISubscritionDaoCallback callback);

    /**
     * 添加Album
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除Album
     * @param album
     */
    void delAlbum(Album album);

    /**
     * 获取订阅内容
     */
    void listAlbum();
}
