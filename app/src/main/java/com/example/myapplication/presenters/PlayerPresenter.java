package com.example.myapplication.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.myapplication.data.XimalayaApi;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.interfaces.IPalyerPresenter;
import com.example.myapplication.interfaces.IPlayerCallback;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPalyerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG ="PlayerPresenter" ;
    private final XmPlayerManager mPlayerManager;
    private List<IPlayerCallback> mCallbacks=new ArrayList<>();
    private int mplayIndex=0;
    private final SharedPreferences mPlayModSp;
    private XmPlayListControl.PlayMode mCurrentPlayMode=PLAY_MODEL_LIST;
    private boolean misReverse=false;
    /**
     ** 1.????????????????????????PLAY_MODEL_LIST
     * 2.????????????PLAY_MODEL_LIST_LOOP
     * 3.????????????PLAY_MODEL_RANDOM
     * 4.??????????????????PLAY_MODEL_SINGLE_LOOP
     */
    public static final int PLAY_MODEL_LIST_INT=0;
    public static final int PLAY_MODEL_LIST_LOOP_INT=1;
    public static final int PLAY_MODEL_RANDOM_INT=2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT=03;

    //Sp's key and name
    public static final String PLAY_MODEL_SP_NAME="PlayMode";
    public static final String PLAY_MODEL_SP_KEY="currentPlayMode";
    private Track mCrrentTarck;
    private int mCurrentProgress=0;
    private int mCurrentDuration=0;


    private PlayerPresenter (){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //?????????????????????
        mPlayerManager.addAdsStatusListener(this);
        //??????????????????????????????
        mPlayerManager.addPlayerStatusListener(this);
        //?????????????????????????????????
        mPlayModSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODEL_SP_NAME, Context.MODE_PRIVATE);

    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getsPlayerPresenter(){

        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class){
                if(sPlayerPresenter==null){
                    sPlayerPresenter=new PlayerPresenter();
                }
            }

        }
        return  sPlayerPresenter;
    }

   private boolean isPlayListSet=false;

    public void setPlayList(List<Track> list,int startIndex){
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list,startIndex);
            isPlayListSet=true;
        }else {
            LogUtil.d(TAG,"mPlayerManager is not null");

        }


    }

    @Override
    public void play() {
         if(isPlayListSet){
            mPlayerManager.play();
                }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //???????????????
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //???????????????
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }

    }

    @Override
    public void swichPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mode);//list_loop
            mCurrentPlayMode=mode;
            for (IPlayerCallback callback : mCallbacks) {
                callback.onPlayModeChange(mode);
            }
            //?????????SP
            SharedPreferences.Editor edit = mPlayModSp.edit();
            edit.putInt(PLAY_MODEL_SP_KEY,getInByPlayMode(mode));
            edit.commit();
        }
    }

    public boolean hasPlayList(){
         return isPlayListSet;


    }
 private int getInByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT ;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT ;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT ;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT ;

        }
        return PLAY_MODEL_LIST_INT;
 }
    private XmPlayListControl.PlayMode getPlayModeByInt(int index ){
        switch (index){
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST ;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM  ;
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP  ;

        }
        return PLAY_MODEL_LIST ;
    }
    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback callback : mCallbacks) {
                callback.onListLoaded(playList,mplayIndex);
            }
        }

    }

    @Override
    public void playByIndex(int index) {
        if (mPlayerManager != null) {
            mPlayerManager.play(index);

        }

    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);

    }

    @Override
    public boolean isPlaying() {
        //??????????????????????????????
        return mPlayerManager.isPlaying();

    }

    @Override
    public void reversePlayList() {
        //?????????????????????
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        misReverse=!misReverse;
        mplayIndex=playList.size()-1-mPlayerManager.getCurrentIndex();
        //????????????????????????????????????????????????????????????????????????
        mPlayerManager.setPlayList(playList,mplayIndex);
        //??????UI
        mCrrentTarck = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback callback : mCallbacks) {
            callback.onListLoaded(playList,mplayIndex);
            callback.onTrackUpdata(mCrrentTarck,mplayIndex);
            callback.updateListOrder(misReverse);
        }


    }

    @Override
    public void playByAblumID(long id) {
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAblumDetai(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                if (tracks != null&&tracks.size()>0) {
                        mPlayerManager.setPlayList(tracks,0);
                        isPlayListSet=true;

                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(),"??????",Toast.LENGTH_LONG).show();
            }
        },id,1);

    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        //?????????getInt?????????????????????????????????PLAY_MODEL_SP_KEY???????????????value?????????????????????????????????PLAY_MODEL_LIST_INT???
        int modeIndex = mPlayModSp.getInt(PLAY_MODEL_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode=getPlayModeByInt(modeIndex);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);
        handlerPlayStatus(iPlayerCallback);
        iPlayerCallback.onProgressChange(mCurrentProgress,mCurrentDuration);
        if(mPlayerManager.isPlaying()){
            iPlayerCallback.onAgainDetail(mCrrentTarck,mplayIndex);
            iPlayerCallback.onTrackUpdata(mCrrentTarck,mplayIndex);


        }
       if (!mCallbacks.contains(iPlayerCallback)) {
            mCallbacks.add(iPlayerCallback);

         }

    }

    private void handlerPlayStatus(IPlayerCallback iPlayerCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        if (PlayerConstants.STATE_STARTED==playerStatus) {
            iPlayerCallback.onPlayStrat();
        }else {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        if (mCallbacks.contains(iPlayerCallback)) {
            mCallbacks.remove(iPlayerCallback);

        }
    }



    //====================start=================????????????
    @Override
    public void onStartGetAdsInfo() {
    LogUtil.d(TAG,"onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds");
    }

    @Override
    public void onError(int what, int errormessage) {
        LogUtil.d(TAG,"onError----->"+what+"errormessage"+errormessage);
    }
    //??????????????????========================end================

    //???????????????======================start==============
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"????????????");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayStrat();
        }
        }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"????????????");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayPause();
        }

    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"????????????");
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"?????????");

    }

    @Override
    public void onSoundPrepared() {
        if (mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED) {
            LogUtil.d(TAG,"??????????????????---------------- ");
            LogUtil.d(TAG,mCrrentTarck+"5555555");
            mPlayerManager.setPlayMode(mCurrentPlayMode);
            mPlayerManager.play();
            if(mCallbacks!=null) {
                int index = mPlayerManager.getCurrentIndex();
                mplayIndex=index;

                for (IPlayerCallback callback : mCallbacks) {
                    callback.onTrackUpdata(mCrrentTarck, index);
                }
            }
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG,"????????????");
        if(curModel instanceof Track)
        {
            Track track=(Track) curModel;
            mplayIndex=mPlayerManager.getCurrentIndex();
            mCrrentTarck=track;
            LogUtil.d(TAG,mCrrentTarck+"1231323");
            HistoryPresenter historyPresenter=HistoryPresenter.getInstance();
            historyPresenter.addHistory(mCrrentTarck);


        }


}

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"????????????");

    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"??????????????????");

    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG,"????????????"+progress);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
     this.mCurrentProgress=currPos;
     this.mCurrentDuration=duration;
        //???????????????
        for (IPlayerCallback callback : mCallbacks) {
            callback.onProgressChange(currPos,duration);
            callback.onAgainDetail(mCrrentTarck,mplayIndex);
        }

        LogUtil.d(TAG,"?????????????????????");
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"????????????");
        return false;
    }
    //???????????????======================end===============

}
