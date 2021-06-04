package com.example.myapplication.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHisttoryDao {
    /**
     * 设置回调接口
     * @param iHistoryDaoCallbak
     */
    void setCallbak(IHistoryDaoCallbak iHistoryDaoCallbak);

    /**
     * 添加历史内容
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史内容
     * @param tarck
     */
    void delHistory(Track tarck);

    /**
     * 清楚历史内容
     *
     */
    void clearnHistory();


    /**
     * 获取历史内容
     */
    void listHistories();


}
