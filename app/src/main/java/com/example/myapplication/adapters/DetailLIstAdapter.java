package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailLIstAdapter extends RecyclerView.Adapter<DetailLIstAdapter.InnerHolder> {
    private static final String TAG = "DetailLIstAdapter";
    private List<Track> mDetailData=new ArrayList<>();
    private SimpleDateFormat mUpdateFormat =new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDutrationFormat=new SimpleDateFormat("MM:ss");
    private ItemClickListener mItemCilckListener=null;
    private ItemLongClickListener mItemLongCilckListener=null;

    @NonNull
    @Override
    public DetailLIstAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);
        return new InnerHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull DetailLIstAdapter.InnerHolder holder, final int position) {
        final View itemView=holder.itemView;
        TextView otherTV=itemView.findViewById(R.id.other_text);
        TextView titleTv=itemView.findViewById(R.id.detail_item_titile);
        TextView playCountTv=itemView.findViewById(R.id.detail_item_play_count);
        TextView durationTv=itemView.findViewById(R.id.detail_item_duration);
        TextView updataDateTv=itemView.findViewById(R.id.detail_item_updata_time);
        final Track track=mDetailData.get(position);
        otherTV.setText((position+1)+"");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText((track.getPlayCount()/10000)>0?track.getPlayCount()/10000+(track.getPlayCount()%10000)*0.0001+"万":track.getPlayCount()%10000+"");
        String duration=mDutrationFormat.format(track.getDuration()*1000);
        durationTv.setText(duration);
        String updataTextText= mUpdateFormat.format(track.getUpdatedAt());
        updataDateTv.setText(updataTextText);
        //设置item点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(itemView.getContext(),"you click item",Toast.LENGTH_SHORT).show();
                if(mItemCilckListener!=null){

                    mItemCilckListener.onItemClick(mDetailData,position);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongCilckListener != null) {
                    mItemLongCilckListener.onItemLongClick(track);
                }
                return true;
            }
        });



    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        //清楚原来的数据
        mDetailData.clear();
         //添加新的数据
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();

    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public void setItemClickListener(ItemClickListener listener){

        this.mItemCilckListener=listener;
    }

    public interface ItemClickListener{
         void onItemClick(List<Track> mDetailData, int position);

    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongCilckListener=listener;
    }

    public interface ItemLongClickListener{
        void onItemLongClick(Track track);

    }


}
