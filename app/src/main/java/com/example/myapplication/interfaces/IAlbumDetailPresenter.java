package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasepresenter;

public interface IAlbumDetailPresenter extends IBasepresenter<IAlbumDetailViewCallback> {
    /**
     *
     * 下拉刷新
     */
    void pull2RefreshMore();

    /**
     * 下来刷新
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId,int page);



}
