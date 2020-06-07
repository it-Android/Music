package com.admin.mymusic.fragment.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/21 12:02
 **/
public  abstract class ButlerBaseFragment{
    private FragmentManager manager;
    public boolean initManager(FragmentManager manager,int res,Fragment fragment){
        this.manager=manager;
        manager.beginTransaction().add(res,fragment).commit();
        return false;
    }
    public abstract Fragment replace(int num);
    public FragmentManager getManager(){
        return this.manager;
    }

}
