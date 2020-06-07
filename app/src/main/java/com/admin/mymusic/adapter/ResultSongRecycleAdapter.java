package com.admin.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admin.mymusic.R;
import com.admin.mymusic.javaBean.gson.SearchResultSongData;
import com.admin.mymusic.myview.RecycleViewLisitenter;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/23 20:41
 **/
public class ResultSongRecycleAdapter extends RecyclerView.Adapter<ResultSongRecycleAdapter.ViewHolder> {
    private Context context;
    private List<SearchResultSongData> songAllData;

    public ResultSongRecycleAdapter(List<SearchResultSongData> songAllData) {
        this.songAllData = songAllData;
    }

    private RecycleViewLisitenter.onItemClickLisitenter onItemClickLisitenter;

    public void setOnItemClickLisitenter(RecycleViewLisitenter.onItemClickLisitenter onItemClickLisitenter) {
        this.onItemClickLisitenter = onItemClickLisitenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_result_song_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.ll_box.setOnClickListener((v) -> {
            if (onItemClickLisitenter != null) {
                onItemClickLisitenter.onItemClick(v, holder.getAdapterPosition());
            }
        });

        holder.moreImg.setOnClickListener((v) -> {
            if (onItemClickLisitenter != null) {
                onItemClickLisitenter.onItemClick(holder.tv_name, holder.getAdapterPosition());
            }
        });

        holder.img.setOnClickListener((v) -> {
            if (onItemClickLisitenter != null) {
                onItemClickLisitenter.onItemClick(v, holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResultSongData songData = songAllData.get(position);
        holder.tv_name.setText(songData.getName());
        if(songData.getImgPath()==null||songData.getImgPath().equals("")){
            Glide.with(context).load(R.mipmap.logo).into(holder.img);//图片
        }else {
            Glide.with(context).load(songData.getImgPath()).into(holder.img);//图片
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(songData.getSinger());
        if (songData.getTitle() != null && !songData.getTitle().equals("")) {
            buffer.append("\t—\t").append(songData.getTitle());//作者名称
        }
        holder.tv_singer.setText(buffer.toString());
    }

    @Override
    public int getItemCount() {
        return songAllData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView moreImg;
        TextView tv_name, tv_singer;
        LinearLayout ll_box;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.recycle_item_result_song_img);
            moreImg = itemView.findViewById(R.id.recycle_item_result_song_more);
            tv_name = itemView.findViewById(R.id.recycle_item_result_song_name);
            ll_box = itemView.findViewById(R.id.recycle_item_result_song_ll_box);
            tv_singer = itemView.findViewById(R.id.recycle_item_result_song_singer);
        }
    }
}
