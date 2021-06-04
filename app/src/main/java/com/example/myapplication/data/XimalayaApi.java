package com.example.myapplication.data;

import com.example.myapplication.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {

    private XimalayaApi(){

    }
    private static XimalayaApi sXimalayaApi;

    public static XimalayaApi getXimalayaApi(){
        if (sXimalayaApi == null) {
                synchronized (XimalayaApi.class){
                    if (sXimalayaApi == null) {
                        sXimalayaApi=new XimalayaApi();
                    }
                }
        }
        return sXimalayaApi;
    }
    /**
     * 获取推荐内容
     * @param callBack 请求结果的回调接口
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String, String> map = new HashMap<>();
        //这个参数代表一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }

    /**
     * 根据专辑的ID获取到专辑内容
     * @param callBack 获取专辑详情的回调
     * @param albumId  专辑的ID
     * @param pageIndex 第几页
     */
    public void getAblumDetai(IDataCallBack<TrackList> callBack,long albumId,int pageIndex){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map,callBack);

    }

    /**
     * 搜索关键词
     * @param keyword
     * @param page
     * @param callBack
     */
    public void searchKeyWord(String keyword,int page,IDataCallBack<SearchAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getSearchedAlbums(map,callBack );
    }

    /**
     * 获取推荐的热词
     * @param callBack
     */
    public void getHotWords(IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORED+"");
        CommonRequest.getHotWords(map,callBack);
    }

    /**
     * 根据关键字获取联系词
     * @param suggestWord  关键字
     * @param callBack 回调
     */
    public void getSuggestWord(String suggestWord,IDataCallBack<SuggestWords> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, suggestWord);
        CommonRequest.getSuggestWord(map, callBack);
    }
}
