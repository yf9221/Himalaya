package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    /**
     * 搜索结果的回调方法
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的接口回调方法
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 获取加载更多结果的接口回调
     * @param hotWordList 结果
     * @param isOkey  true表示加载成功,false表示没有更多
     */
    void onLoadMoreResult(List<Album> hotWordList,boolean isOkey);

    /**
     * 联系关键词接口回调
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 错误通知回调
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode,String errorMsg,String errorType);





}
