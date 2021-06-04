package com.example.myapplication.adapters;

import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;
import com.example.myapplication.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayTrackPagerAdapter extends PagerAdapter {
    private static final String TAG ="PlayTrackPagerAdapter" ;
    private List<Track> mTrackDatas=new ArrayList<>();
    @Override
    public int getCount() {
        return mTrackDatas.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View iteemView= LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager,container,false);
        container.addView(iteemView);
        ImageView item=iteemView.findViewById(R.id.track_pager_item);
        Track track=mTrackDatas.get(position);
        LogUtil.d(TAG,"getCoverUrlLarge----->"+track.getCoverUrlLarge());
        String coverUrlLarge = track.getCoverUrlLarge();
        Picasso.with(container.getContext()).load(coverUrlLarge).into(item);
        return iteemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    public void setData(List<Track> list) {
        mTrackDatas.clear();
        mTrackDatas.addAll(list);
        notifyDataSetChanged();

    }
}
