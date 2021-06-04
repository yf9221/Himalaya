package com.example.myapplication.adapters;

import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {


    private static final String TAG ="RecommendListAdapter" ;
    private List<Album> mdata=new ArrayList<>();
    private RecommendItemClickListener mItemClickListner=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载View
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendListAdapter.InnerHolder holder, final int position) {
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
        }
        //更新UI
        notifyDataSetChanged();
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
            albumPlayCountTv.setText((album.getPlayCount()/10000)>0?(album.getPlayCount()/10000)+((album.getPlayCount()%10000)*0.0001)+"万":(album.getPlayCount()%10000)+"");
            albumContentCountIv.setText(album.getIncludeTrackCount()+"集");
            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);



        }
    }
    public void setRecommendItemClickListner(RecommendItemClickListener listner){
        this.mItemClickListner=listner;
    }
    public interface RecommendItemClickListener{
         void OnItemClick(int position, Album album);
    }
}
