package com.example.myapplication.utils;

import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.fragments.HistoryFragment;
import com.example.myapplication.fragments.RecommendFragment;
import com.example.myapplication.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreator {
    public final static int IDEX_RECOMMEND=0;
    public final static int IDEX_SUBSCRIPTION=1;
    public final static int IDEX_HISTORY=2;
    public final static  int PAGE_COUNT=3;


    private static Map<Integer, BaseFragment> sCache=new HashMap<>();


    public static BaseFragment getFragment(Integer index){
        BaseFragment baseFragment=sCache.get(index);
        if(baseFragment!=null)
        {
            return baseFragment;

        }
        switch (index){
            case IDEX_RECOMMEND :
                baseFragment=new RecommendFragment();
                break;
            case IDEX_SUBSCRIPTION :
                baseFragment=new SubscriptionFragment();
                break;
            case IDEX_HISTORY :
                baseFragment=new HistoryFragment();
                break;



        }
        sCache.put(index,baseFragment);
        return baseFragment;

    }
}
