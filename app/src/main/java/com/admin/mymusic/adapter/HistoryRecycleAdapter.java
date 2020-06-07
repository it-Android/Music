package com.admin.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.admin.mymusic.R;
import com.admin.mymusic.myview.RecycleViewLisitenter;

import java.util.List;

/**
 * 历史搜索数据显示RecycleView控件适配器
 *
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/21 12:47
 **/
public class HistoryRecycleAdapter extends RecyclerView.Adapter<HistoryRecycleAdapter.ViewHolder>{
    private List<String> allData;//要显示的数据
    private Context context;//上下文

    public HistoryRecycleAdapter(List<String> allData) {
        this.allData = allData;
    }
    private RecycleViewLisitenter.onItemClickLisitenter onItemClickLisitenter;//点击
    private RecycleViewLisitenter.onItemLongClickLisitenter onItemLongClickLisitenter;//长按

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_text_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener((v) -> {
            if (onItemClickLisitenter != null) {
                onItemClickLisitenter.onItemClick(v, holder.getAdapterPosition());//
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.historyText.setText(allData.get(position));
    }

    @Override
    public int getItemCount() {
        return allData.size();
    }

    public void setOnItemClickLisitenter(RecycleViewLisitenter.onItemClickLisitenter onItemClickLisitenter) {
        this.onItemClickLisitenter = onItemClickLisitenter;
    }

    public void setOnItemLongClickLisitenter(RecycleViewLisitenter.onItemLongClickLisitenter onItemLongClickLisitenter) {
        this.onItemLongClickLisitenter = onItemLongClickLisitenter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyText;

        public ViewHolder(@NonNull View view) {
            super(view);
            historyText = view.findViewById(R.id.recycle_item_text);
        }
    }
}
