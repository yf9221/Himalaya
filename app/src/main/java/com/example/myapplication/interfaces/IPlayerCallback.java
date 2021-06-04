package com.example.myapplication.interfaces;


import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    /**
     * 播放开始
     */

    void onPlayStrat();

    /**
     * 播放暂停
     */

    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一首播放
     */
    void onnextPlay(Track track);

    /**
     * 上一首播放
     */

    void onPrePlay(Track track);

    /**
     * 获取播放列表
     * @param list
     */
    void onListLoaded(List<Track> list,int playIndex);

    /**
     * 播放器模式改变
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条改变了
     * @param currentProgess
     * @param totall
     */
    void onProgressChange(int currentProgess,int totall);

    /**
     * 广告正在加载
     */
    void onALoading();

    /**
     * 广告结束
     */
    void onAFinished();

    /**
     * 更新标题和封面
     * @param track
     * @param playIndex
     */
    void onTrackUpdata(Track track,int playIndex);

    /**
     * 更新列表播放顺序
     * @param isReverse
     */
    void updateListOrder(boolean isReverse);

    void onAgainDetail(Track track,int playIndex);



}
