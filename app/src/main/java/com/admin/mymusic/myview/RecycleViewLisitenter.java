package com.admin.mymusic.myview;

import android.view.View;

public class RecycleViewLisitenter {

    /**
     * RecycleView的条目点击监听
     */
    public interface onItemClickLisitenter {
        void onItemClick(View view, int position);
    }


    /**
     * RecycleView的条目长按点击监听
     */
    public interface onItemLongClickLisitenter {
        void onItemLongClick(View v, int position);
    }

}