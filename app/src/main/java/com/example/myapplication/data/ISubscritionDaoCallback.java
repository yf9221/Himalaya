package com.example.myapplication.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscritionDaoCallback {

    /**
     * 添加结果回调
     * @param isSuccess
     */
    void OnaddResult(boolean isSuccess);

    /**
     * 删除结果回调
     * @param isSucess
     */
    void OndelResult(boolean isSucess);


    /**
     * 订阅列表加载
     * @param result
     */
    void OnSubListLoaded(List<Album> result);
}
