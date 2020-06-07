package com.admin.mymusic.fragment.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admin.mymusic.R;
import com.admin.mymusic.activity.SearchActivity;
import com.admin.mymusic.adapter.HistoryRecycleAdapter;
import com.admin.mymusic.myview.RecycleViewLisitenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/21 11:45
 **/
public class SearchHomeFragment extends Fragment implements RecycleViewLisitenter.onItemClickLisitenter {
    private Context context;
    private List<String> hisAllData = new ArrayList<>();//历史搜索全部数据

    private RecyclerView recyclerViewHis;
    private LinearLayoutManager hisManager;//历史记录布局管理器

    private HistoryRecycleAdapter hisRecycleAdapter;//历史记录适配器
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_search_home, container, false);
        hisAllData.add("周杰伦");
        hisAllData.add("邓紫棋");
        hisAllData.add("张学友");
        hisAllData.add("刘德华");
        hisAllData.add("薛之谦");
        initViews(view);
        return view;
    }

    private void initViews(View view){
        recyclerViewHis = view.findViewById(R.id.fr_sc_hom_his_recycle);
        hisManager = new LinearLayoutManager(context);
        hisManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewHis.setLayoutManager(hisManager);//添加布局
        hisRecycleAdapter = new HistoryRecycleAdapter(hisAllData);
        hisRecycleAdapter.setOnItemClickLisitenter(this::onItemClick);
        recyclerViewHis.setAdapter(hisRecycleAdapter);
    }


    @Override
    public void onItemClick(View v, int position) {
        String data=hisAllData.get(position);
        ((SearchActivity)getActivity()).onClick(v,data);
    }
}
