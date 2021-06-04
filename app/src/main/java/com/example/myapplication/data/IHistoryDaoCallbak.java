package com.example.myapplication.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallbak {
    /**
     * 添加历史结果回调
     * @param isSuccess
     */
    void onHistoryadd(boolean isSuccess);

    /**
     * 删除历史结果回调
     * @param isSuccess
     */
    void onHistorydel(boolean isSuccess);

    /**
     * 获取历史结果回调
     * @param trackList
     */
    void onHistoryListLoaded(List<Track> trackList);

    /**
     * 清除历史结果回调
     * @param isSuccess
     */
    void onHistoryClean(boolean isSuccess);
}
