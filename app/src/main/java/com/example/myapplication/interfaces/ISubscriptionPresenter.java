package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasepresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;


/**
 * 订阅有上限不能超过100个
 */
public interface ISubscriptionPresenter extends IBasepresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     * @param album
     */
    void deleteSubscription(Album album);


    /**
     * 获取订阅列表
     */
    void getSubscriptionList();


    /**
     * 判断当前专辑是否收藏/订阅
     * @param album
     * @return
     */
    boolean isSub(Album album);


}
