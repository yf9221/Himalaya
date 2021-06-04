package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder> {
    private List<QueryResult> mDatas=new ArrayList<>();
    private OnItemClickListener mOnItemClick=null;

    @NonNull
    @Override
    public SearchRecommendAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_recommend_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendAdapter.InnerHolder holder, int position) {
        TextView textView=holder.itemView.findViewById(R.id.search_commend_item);
        QueryResult queryResult=mDatas.get(position);
        final String keyword = queryResult.getKeyword();
        textView.setText(keyword);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if (mOnItemClick != null) {
                    mOnItemClick.OnItemClick(keyword);
                }
            }
        });

    }

    @Override
    public int getItemCount() {

            return mDatas.size();


    }

    public void setData(List<QueryResult> keyWordList) {
        mDatas.clear();
        mDatas.addAll(keyWordList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClick=listener;
    }

    public interface OnItemClickListener{
        void OnItemClick(String keyword);
    }
}
