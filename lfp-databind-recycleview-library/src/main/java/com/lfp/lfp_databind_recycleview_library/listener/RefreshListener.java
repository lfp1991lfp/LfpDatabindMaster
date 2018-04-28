package com.lfp.lfp_databind_recycleview_library.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by linfp on 2016/4/8.
 * 抽象化两个刷新接口
 */
public interface RefreshListener {

    /**
     * 上拉刷新.
     *
     * @param recyclerView 刷新的视图.
     */
    void onRefresh(RecyclerView recyclerView);

    /**
     * 下拉刷新.
     *
     * @param recyclerView 刷新视图.
     */
    void onLoadMore(RecyclerView recyclerView);
}
