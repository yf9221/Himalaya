package com.example.myapplication.presenters;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.data.HistoryDao;
import com.example.myapplication.data.IHistoryDaoCallbak;
import com.example.myapplication.data.IHisttoryDao;
import com.example.myapplication.interfaces.IHistoryCallback;
import com.example.myapplication.interfaces.IHistoryPresenter;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallbak {

    private static final String TAG = "HistoryPresenter";
    private static HistoryPresenter sHistoryPresenter=null;
    private final IHisttoryDao mHisttoryDao;
    private List<IHistoryCallback> mCallbaks=new ArrayList<>();
    private List<Track> mCurrentHistoiesList=null;
    private Track mCurrentTrack=null;

    private HistoryPresenter(){
        mHisttoryDao = new HistoryDao();
        mHisttoryDao.setCallbak(this);
    }

    public static HistoryPresenter getInstance(){
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class){
                if (sHistoryPresenter == null) {
                    sHistoryPresenter=new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHisttoryDao != null) {
                    mHisttoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isDODelAsOutSize=false;
    @Override
    public void addHistory(final Track track) {
        if (mCurrentHistoiesList != null&&mCurrentHistoiesList.size()>= Constants.HIS_MAX_COUNT) {
            isDODelAsOutSize=true;
            this.mCurrentTrack=track;
            delHistory(mCurrentHistoiesList.get(mCurrentHistoiesList.size()-1));
        }else {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                    if (mHisttoryDao != null) {
                        mHisttoryDao.addHistory(track);
                    }
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }

    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHisttoryDao != null) {
                    mHisttoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistroy() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHisttoryDao != null) {
                    mHisttoryDao.clearnHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }



    @Override
    public void onHistoryadd(boolean isSuccess) {
            listHistories();
    }

    @Override
    public void onHistorydel(boolean isSuccess) {
             if(isSuccess){
                 if (isDODelAsOutSize&&mCurrentTrack != null) {
                     addHistory(mCurrentTrack);
                     isDODelAsOutSize=false;
                 }
             }
             listHistories();

    }

    @Override
    public void onHistoryListLoaded(final List<Track> trackList) {
        this.mCurrentHistoiesList=trackList;
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                //通知主线程UI更新
                LogUtil.d(TAG,"获取历史列表"+trackList.size());
                for (IHistoryCallback callbak : mCallbaks) {
                    callbak.onHistoiesLoaded(trackList);
                }
            }
        });

    }

    @Override
    public void onHistoryClean(boolean isSuccess) {

    }


    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        if (mCallbaks != null) {
            mCallbaks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
          mCallbaks.remove(iHistoryCallback);
    }
}
