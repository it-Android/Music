package com.admin.mymusic.fragment.search;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.admin.mymusic.R;
import com.admin.mymusic.fragment.base.ButlerBaseFragment;

/**
 * 搜索碎片管家类
 *
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/21 11:51
 **/
public class ButlerFragmentSearch extends ButlerBaseFragment {
    private SearchHomeFragment home;
    private SearchResultFragment result;
    private final static String TAG = "搜索碎片管家类消息提示";
    private int number;

    public ButlerFragmentSearch(FragmentManager manager) {
        if (home == null) {
            home = new SearchHomeFragment();
            result = new SearchResultFragment();
            initManager(manager, R.id.frame_box, home);
            number = 0;
        }
    }

    @Override
    public Fragment replace(int num) {
        Fragment fragment = null;
        switch (num) {
            case 0:
                fragment = home;
                break;
            case 1:
                fragment = result;
                break;
            default:
                fragment = home;
        }
        if (num != number) {
            getManager().beginTransaction()
                    .replace(R.id.frame_box, fragment)
                    .commit();
            number = num;
        }
        return fragment;
    }
}
