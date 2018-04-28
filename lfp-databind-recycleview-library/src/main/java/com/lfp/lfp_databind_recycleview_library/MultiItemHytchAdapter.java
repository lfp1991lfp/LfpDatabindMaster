package com.lfp.lfp_databind_recycleview_library;

import android.content.Context;
import android.databinding.ViewDataBinding;

import java.util.List;

/**
 * Created by lfp on 2017/5/31.
 * 简单适配器（多数据类型）
 */

public abstract class MultiItemHytchAdapter<DB extends ViewDataBinding, T> extends MultiItemTypeAdapter<DB, T> {

    public MultiItemHytchAdapter(Context context, List<T> dataList) {
        super(context, dataList);
    }

    @Override
    protected boolean areItemsTheSame(T item1, T item2) {
        return false;
    }

    @Override
    protected boolean areContentsTheSame(T item1, T item2) {
        return false;
    }
}
