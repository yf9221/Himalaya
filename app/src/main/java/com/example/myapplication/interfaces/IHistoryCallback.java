package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback {
    /**
     * 获取历史内容结果回调
     */
    void onHistoiesLoaded(List<Track> trackList);
}

