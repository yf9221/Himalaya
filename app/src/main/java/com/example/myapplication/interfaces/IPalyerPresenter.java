package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasepresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPalyerPresenter extends IBasepresenter<IPlayerCallback> {
    /**
     * 播放
     */
    void play();
    /**
     * 暂停
     */
    void pause();
    /**
     * 停止
     */
    void stop();

    /**
     * 播放上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放类型
     * @param mode
     */
    void swichPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目的位置进行播放
     * @param index
     */
    void playByIndex(int index);


    /**
     * 切换播放进度
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 判断播放器是否正在播放
     * @return
     */
    boolean isPlaying();


    /**
     * 把播放器列表内容反转
     */
    void reversePlayList();

    /**
     * 播放专辑第一个节目
     * @param id
     */
    void playByAblumID(long id);

}
