package com.admin.mymusic.myview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.admin.mymusic.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/3 18:44
 **/
public class SearchAdapter extends BaseAdapter {
    private List<String> list=new ArrayList<>();

    public SearchAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHold hold=null;
        if(view==null){
            hold=new ViewHold();
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item_layout,parent,false);
            hold.textView=view.findViewById(R.id.list_item_text);
            view.setTag(hold);
        }else {
            hold= (ViewHold) view.getTag();
        }
        hold.textView.setText(list.get(position));
        return view;
    }

    class ViewHold{
        TextView textView;
    }

}
