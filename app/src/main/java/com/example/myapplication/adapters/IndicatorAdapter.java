package com.example.myapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.myapplication.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;


public class IndicatorAdapter extends CommonNavigatorAdapter {


    private final String[] mtitle;
    private OnIndicatorTabClickListener mOnTabClickListener;

    public IndicatorAdapter(Context context) {
        mtitle = context.getResources().getStringArray(R.array.indicator_title);
    }

    @Override
    public int getCount() {
        if (mtitle!=null) {
            return mtitle.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        ColorTransitionPagerTitleView colorTransitionPagerTitleView=new ColorTransitionPagerTitleView(context);
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        colorTransitionPagerTitleView.setTextSize(18);
        colorTransitionPagerTitleView.setText(mtitle[index]);
       /* SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(Color.GRAY);
        simplePagerTitleView.setSelectedColor(Color.WHITE);
        simplePagerTitleView.setText(mtitle[index]);
*/
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mViewPager.setCurrentItem(index);
                //TODO:
                if(mOnTabClickListener!=null)
                {
                    mOnTabClickListener.Ontabclick(index);
                }
            }
        });
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    public void setOnIndicatorTabClickListener(OnIndicatorTabClickListener listener){
        this.mOnTabClickListener=listener;

    }

    public interface OnIndicatorTabClickListener{
        void Ontabclick(int index);
    }
}
