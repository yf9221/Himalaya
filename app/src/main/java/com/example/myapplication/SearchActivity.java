package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.AlbumListAdapter;
import com.example.myapplication.adapters.SearchRecommendAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.interfaces.ISearchCallback;
import com.example.myapplication.presenters.AlbumdetailPresenter;
import com.example.myapplication.presenters.SearchPresenter;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.LogUtil;
import com.example.myapplication.utils.ToastUtils;
import com.example.myapplication.views.FlowTextLayout;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchActivity extends BaseActivity implements ISearchCallback, SearchRecommendAdapter.OnItemClickListener, AlbumListAdapter.OnItemClickListener {

    private static final String TAG ="SearchActivity" ;
    private static final long TIM_SHOW_IMM = 500;
    private ImageView mSearchBack;
    private EditText mSearchInput;
    private View mSearchBtn;
    private SearchPresenter mSearchPresenter;
    private FrameLayout mResultContainer;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private FlowTextLayout mFlowTextLayout;
    private String mErrorType=null;
    private InputMethodManager mMImm;
    private View mDelBtn;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdapter mSearchRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mNeedSuggestWord=true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);
        initView();
        initPresenter();
        initEvent();
    }

    private void initEvent() {
         mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInput.setText("");
            }
        });

        mSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void OnRetryclick() {
                if (mSearchPresenter != null) {
                    if(mErrorType== Constants.ERROR_TYPE_SEARCH){
                        mSearchPresenter.reSearch();
                    }
                   else if(mErrorType== Constants.ERROR_TYPE_HOT_WORD) {
                        mSearchPresenter.getHotWord();
                    }
                    mUILoader.updataStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchPresenter != null) {
                    String kedWord = mSearchInput.getText().toString().trim();
                    mSearchPresenter.doSearch(kedWord);
                    mUILoader.updataStatus(UILoader.UIStatus.LOADING);

                }
            }
        });
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                     if (mSearchPresenter != null) {
                        mSearchPresenter.getHotWord();
                        mDelBtn.setVisibility(View.GONE);
                      }
                }else {
                    if (mNeedSuggestWord) {
                            mDelBtn.setVisibility(View.VISIBLE);
                            //TODO 触发联想查询
                            getSuggestWord(s.toString());
                        }else {
                            mNeedSuggestWord=true;
                        }



                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 获取联系关键词
     * @param kedword
     */
    private void getSuggestWord(String kedword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(kedword);
        }
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        //拿到热词
        mSearchPresenter.getHotWord();
       mUILoader.updataStatus(UILoader.UIStatus.LOADING);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }



    private void initView() {
        mSearchBack = this.findViewById(R.id.search_back);
        mSearchInput = this.findViewById(R.id.search_edit);
         mSearchBtn = this.findViewById(R.id.search_btn);
        mDelBtn = this.findViewById(R.id.search_input_delete);
        mDelBtn.setVisibility(View.GONE);
        mMImm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchInput.requestFocus();
                mMImm.showSoftInput(mSearchInput,InputMethodManager.SHOW_IMPLICIT);
            }
        }, TIM_SHOW_IMM);
        mResultContainer = this.findViewById(R.id.search_container);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

            };
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mResultContainer.addView(mUILoader);

        }

    }

    //创建数据请求成功的View
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result,null);
       //显示热词
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);
        mResultListView = resultView.findViewById(R.id.result_list_view);
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        mRefreshLayout.setEnableRefresh(false);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);
        //设置适配器
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),6);
                outRect.bottom = UIUtil.dip2px(view.getContext(),6);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        LogUtil.d(TAG,mFlowTextLayout+"444444444444444444444444");
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                if (mSearchInput != null) {
                    String keyWord = text;
                    mNeedSuggestWord=false;
                    mSearchInput.setText(keyWord);
                    mSearchInput.setSelection(text.length());
                    mSearchPresenter.doSearch(keyWord);
                    mUILoader.updataStatus(UILoader.UIStatus.LOADING);

                }
            }
        });
        //搜索推荐
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        //设置布局管理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(linearLayoutManager);
        //设置适配器
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //Toast.makeText(SearchActivity.this,"上拉刷新",Toast.LENGTH_LONG).show();
                if (mSearchPresenter != null) {

                    mSearchPresenter.loadMore();
                }

            }
        });
        mAlbumListAdapter.setItemClickListener(this);
       mSearchRecommendList.setAdapter(mSearchRecommendAdapter);
       mSearchRecommendAdapter.setOnItemClickListener(this);


       return resultView;
    }


    @Override
    public void onSearchResultLoaded(List<Album> result) {
        handlerSearchResult(result);
        mMImm.hideSoftInputFromWindow(mSearchInput.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void handlerSearchResult(List<Album> result) {
        hideSuccessView();
        mRefreshLayout.setVisibility(View.VISIBLE);
        //隐藏键盘

        if (result != null) {
            if (result.size() == 0) {
                //数据为空
                if (mUILoader != null) {
                    mUILoader.updataStatus(UILoader.UIStatus.ENPTY);
                }
            }else {
                //如果不为空，那么设置数据
                mAlbumListAdapter.setdata(result);
                mUILoader.updataStatus(UILoader.UIStatus.SUCCESS);
              }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUILoader != null) {
            mUILoader.updataStatus(UILoader.UIStatus.SUCCESS);
        }
        List<String> hotWords = new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchword = hotWord.getSearchword();
            hotWords.add(searchword);
        }
        Collections.sort(hotWords);
        //更新UI
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> hotWordList, boolean isOkey) {
         //处理加载更多的结果
        if (mRefreshLayout != null) {
               mRefreshLayout.finishLoadmore();
        }
        if (isOkey) {
            mAlbumListAdapter.setdata(hotWordList);
            handlerSearchResult(hotWordList);

        }else {

        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
                   LogUtil.d(TAG,"keyWordList size---->"+keyWordList.size());
        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setData(keyWordList);
        }
        //控制UI显示和隐藏
        if (mUILoader != null) {
            mUILoader.updataStatus(UILoader.UIStatus.SUCCESS);
        }
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);

    }
private void hideSuccessView(){
        mSearchRecommendList.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
}


    @Override
    public void onError(int errorCode, String errorMsg,String errorType) {
         this.mErrorType=errorType;
        if (mUILoader != null&&errorCode==604) {
            mUILoader.updataStatus(UILoader.UIStatus.NETWORK_ERROR);
        }else {
            mUILoader.updataStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void OnItemClick(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(keyword);
            mSearchInput.setText(keyword);
            mSearchInput.setSelection(keyword.length());
            mUILoader.updataStatus(UILoader.UIStatus.LOADING);
        }
    }

    @Override
    public void OnItemClick(int position, Album album) {
        //根据位置拿到数据
        AlbumdetailPresenter.getInstance().setTargeAlbum(album);
        //Item被点击，转到详情页面
        Intent intent=new Intent(SearchActivity.this, DetailActivity.class);
        startActivity(intent);
    }
}
