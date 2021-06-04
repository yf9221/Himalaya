package com.example.myapplication.presenters;

import com.example.myapplication.data.XimalayaApi;
import com.example.myapplication.interfaces.ISearchCallback;
import com.example.myapplication.interfaces.ISearchPresenter;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    private List<ISearchCallback> mCallbacks=new ArrayList<>();
    //搜索关键词
    private String mCurrentWord=null;
    private  XimalayaApi mXimalayaApi;
    private static final int DEFULT_PAGE=1;
    private int mCurrentPage=DEFULT_PAGE;
    //public List<String>mErrorType=new ArrayList<>();
    private static final String[] mErrorType ={"hotWord","toSearch"};
    private List<Album> mSearchResult =new ArrayList<>();




    private SearchPresenter (){
        mXimalayaApi = XimalayaApi.getXimalayaApi();

    }

    private static SearchPresenter sSearchPresenter=null;

    public static SearchPresenter getSearchPresenter(){
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class){
                if (sSearchPresenter == null) {
                    sSearchPresenter=new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyword) {
        mCurrentPage=DEFULT_PAGE;
        this.mCurrentWord=keyword;
        mSearchResult.clear();
        search(keyword);

    }

    private boolean isLoadMore;
    private void search(String keyword) {
        mXimalayaApi.searchKeyWord(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearchResult.addAll(albums);
                if (albums != null) {
                    if (isLoadMore) {
                         for (ISearchCallback callback : mCallbacks) {
                             if (albums.size()==0) {
                                 callback.onLoadMoreResult(mSearchResult,false);
                             }else{
                                callback.onLoadMoreResult(mSearchResult,true);
                            }
                          }
                    }
                    else {
                        LogUtil.d(TAG,"albums size----->"+albums.size());
                        for (ISearchCallback callback : mCallbacks) {
                            callback.onSearchResultLoaded(mSearchResult);
                        }
                    }

                }

            }

            @Override
            public void onError(int errorcode, String errormasg) {
                for (ISearchCallback callback : mCallbacks) {
                    if (isLoadMore) {
                        isLoadMore = false;
                        callback.onLoadMoreResult(mSearchResult, false);
                         mCurrentPage--;
                    } else {
                        callback.onError(errorcode, errormasg, mErrorType[1]);
                    }

                }

            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentWord);
    }

    @Override
    public void loadMore() {
         if (mSearchResult.size()<Constants.COUNT_DEFAULT) {
            for (ISearchCallback callback : mCallbacks) {
                callback.onLoadMoreResult(mSearchResult,false);
            }
        }else {
            mCurrentPage++;
            search(mCurrentWord);
            isLoadMore=true;
        }
    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWordList1 = hotWordList.getHotWordList();
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onHotWordLoaded(hotWordList1);
                    }
                    LogUtil.d(TAG,"hotWordList1 size---->"+hotWordList1.size());
                }
            }

            @Override
            public void onError(int errorcode, String errormsg ) {
                LogUtil.d(TAG,"hotWordList1 errorCode------>"+errorcode+"errorMsg"+errormsg);
                for (ISearchCallback callback : mCallbacks) {
                      callback.onError(errorcode,errormsg, mErrorType[0]);
                }

            }
        });

    }

    @Override
    public void getRecommendWord(String keyword) {
    mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
        @Override
        public void onSuccess(SuggestWords suggestWords) {
            if (suggestWords != null) {
                List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                LogUtil.d(TAG,"keyWordList size------>"+keyWordList.size());
                for (ISearchCallback callback : mCallbacks) {
                    callback.onRecommendWordLoaded(keyWordList);
                }
            }
        }

        @Override
        public void onError(int i, String s) {
            LogUtil.d(TAG,"keyWordList error------>"+i+"errormas---->"+s);

        }
    });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);

        }

    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(iSearchCallback);
        }

    }
}
