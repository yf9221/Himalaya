package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasepresenter;

public  interface IRecommendPresenter extends IBasepresenter<IRecommedViewCallback> {

    /**
     *
     * 获取推荐内容
     */
    void getRecommendList();
    /**
     *
     * 下拉刷新
     */
    void pull2RefreshMore();

    /**
     * 下来刷新
     */
    void loadMore();


}
