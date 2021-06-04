package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {


    private static final String TAG ="RecommendListAdapter" ;
    private List<Album> mdata=new ArrayList<>();
    private OnItemClickListener mItemClickListner=null;
    private OnLongClickListener mLongClickLisenter=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载View
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListAdapter.InnerHolder holder, final int position) {
        //设置数据
        holder.itemView.setTag(position);
        holder.setData(mdata.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListner!=null) {
                    int clickPosition = (int) v.getTag();
                    mItemClickListner.OnItemClick(clickPosition ,mdata.get(clickPosition));
                }
                LogUtil.d(TAG,"onclick--->"+v.getTag());

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //表示消费该事件
                if (mLongClickLisenter != null) {
                    int clickpostion = (int) v.getTag();
                    mLongClickLisenter.OnLongCItemClick(mdata.get(clickpostion),clickpostion);

                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mdata!=null) {
            return mdata.size();
        }
        return 0;
    }



    public void setdata(List<Album> albumList) {
        if (mdata!=null) {
          mdata.clear();
          mdata.addAll(albumList);
          Collections.reverse(mdata);
        }
        //更新UI
        notifyDataSetChanged();
    }


    public void delData(int  position){
        mdata.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount()-position);

    }

    public void addData(Album album,int  position){
        Collections.reverse(mdata);
        mdata.add(album);
        Collections.reverse(mdata);
        notifyItemInserted(position);
        notifyItemRangeChanged(0,getItemCount());

    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到控件，设置数据

            //封面
            ImageView albumCoverIv=itemView.findViewById(R.id.album_cover);
            //标题
            TextView albumTitleTv=itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesrcTv=itemView.findViewById(R.id.album_description_tv);
            //播放集数
            TextView albumPlayCountTv=itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountIv=itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesrcTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText((album.getPlayCount()/10000)>0?album.getPlayCount()/10000+(album.getPlayCount()%10000)*0.0001+"万":album.getPlayCount()%10000+"");
            albumContentCountIv.setText(album.getIncludeTrackCount()+"集");
            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);



        }
    }
    public void setItemClickListener(OnItemClickListener clickListener){
        this.mItemClickListner=clickListener;
    }
    public interface OnItemClickListener {
         void OnItemClick(int position, Album album);
    }


    public void setOnLongClickListener(OnLongClickListener longClickListener){
        this.mLongClickLisenter=longClickListener;
    }
    public interface OnLongClickListener{
        void OnLongCItemClick(Album album,int postion);
    }
}
