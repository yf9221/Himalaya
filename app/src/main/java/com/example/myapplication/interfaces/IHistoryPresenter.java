package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasepresenter;
import com.example.myapplication.data.IHistoryDaoCallbak;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryPresenter extends IBasepresenter<IHistoryCallback> {

    /**
     * 获取历史内容
     */
    void listHistories();

    /**
     * 添加历史内容
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史内容
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史
     */
    void cleanHistroy();
}
