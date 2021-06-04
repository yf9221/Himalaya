package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasepresenter;

public interface ISearchPresenter extends IBasepresenter<ISearchCallback> {


    /**
     * 搜索关键字
     * @param keyword
     */
    void doSearch(String keyword);

    void reSearch();

    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();


    /**
     * 获取推荐的关键词(相关的关键词)
     * @param keyword
     */
    void getRecommendWord(String keyword);
}
