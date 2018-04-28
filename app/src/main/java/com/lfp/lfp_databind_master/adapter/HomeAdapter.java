package com.lfp.lfp_databind_master.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.lfp.lfp_databind_master.xml.bean.ContentBean;
import com.lfp.lfp_databind_recycleview_library.MultiItemHytchAdapter;

import java.util.List;

public class HomeAdapter extends MultiItemHytchAdapter<ViewDataBinding, ContentBean> {

    public HomeAdapter(Context context) {
        this(context, null);
    }

    public HomeAdapter(Context context, List<ContentBean> dataList) {
        super(context, dataList);
        addItemViewDelegate(new HomeTitleAdapter());
        addItemViewDelegate(new ContentAdapter());
    }

    @Override
    protected boolean isEnabled(int viewType) {
        return viewType == 1;
    }
}
