package com.example.myapplication.presenters;

import android.widget.Toast;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.data.ISubscritionDaoCallback;
import com.example.myapplication.data.SubscriptionDao;
import com.example.myapplication.interfaces.ISubscriptionCallback;
import com.example.myapplication.interfaces.ISubscriptionPresenter;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubscritionDaoCallback {


    private static final String TAG ="SubscriptionPresenter" ;
    private SubscriptionDao mSubscriptionDao;
    private Map<Long,Album> mData=new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks=new ArrayList<>();
    private Album mCurrentAlbum;

    private SubscriptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);


    }
    private void  listSubscription(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                //只调用,不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbum();
                }

            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    private static SubscriptionPresenter sInstance=null;

    public static SubscriptionPresenter getInstance(){
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class){
                sInstance=new SubscriptionPresenter();
            }
        }
        return  sInstance;
    }

    @Override
    public void addSubscription(final Album album) {
        if ((mData.size()>=Constants.SUB_MAX_COUNT)) {
            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubscriptionFull(true);
            }
            return;
        }
              Observable.create(new ObservableOnSubscribe<Object>() {
                  @Override
                  public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                         if (mSubscriptionDao != null) {
                              mData.put(album.getId(),album);
                              mSubscriptionDao.addAlbum(album);
                              mCurrentAlbum=album;
                          }

                  }
              }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    mData.remove(album.getId());
                    mSubscriptionDao.delAlbum(album);

                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscription();
    }

    @Override
    public boolean isSub(Album album) {
         Album result = mData.get(album.getId());
        //不为空表示以订阅
        return result!=null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {

        if (!mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        mCallbacks.remove(iSubscriptionCallback);
    }

    @Override
    public void OnaddResult(final boolean isSuccess) {
     //数据添加成功的回调
         BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                    for (ISubscriptionCallback callback : mCallbacks) {
                        callback.onAddResult(isSuccess,mCurrentAlbum);
                    }

            }
        });
    }

    @Override
    public void OndelResult(final boolean isSuccess) {
        //数据删除成功的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);

                   }
            }
        });
    }

    @Override
    public void OnSubListLoaded(final List<Album> result) {
        mData.clear();
        //数据加载成功的回调
        for (Album album : result) {
            mData.put(album.getId(),album);
        }
          //通知UI更新
        //TODO
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscriptionLoaded(result);
                }
            }
        });


    }

}
