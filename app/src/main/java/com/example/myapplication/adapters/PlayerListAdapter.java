package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.InnerHolder> {

    private List<Track> mDatas=new ArrayList<>();
    private int playIndex=0;
    private SobPopWindow.PlayListItemClickListener mPlayItemClickListener=null;


    @NonNull
    @Override
    public PlayerListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListAdapter.InnerHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayItemClickListener != null) {
                    mPlayItemClickListener.onItemClick(position);
                }
            }
        });
          //设置数据
        Track track = mDatas.get(position);
        TextView trackTitleTv=holder.itemView.findViewById(R.id.track_title_tv);
        trackTitleTv.setText(track.getTrackTitle());
        //设置字体颜色
        trackTitleTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(playIndex==position? R.color.main_color:R.color.sub_text_title));
        //播放状态图标
        View playIconIv=holder.itemView.findViewById(R.id.play_icon_iv);
        playIconIv.setVisibility(playIndex==position?View.VISIBLE:View.GONE);

    }

    @Override
    public int getItemCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    public void setData(List<Track> data) {
        //设置数据，更新列表
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentIndexPosition(int position) {
        playIndex=position;
        notifyDataSetChanged();
    }

    public void setPlayListItemClickListener(SobPopWindow.PlayListItemClickListener listener) {
        this.mPlayItemClickListener=listener;
    }


    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
