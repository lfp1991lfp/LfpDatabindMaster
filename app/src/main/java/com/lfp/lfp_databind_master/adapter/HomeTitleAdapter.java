package com.lfp.lfp_databind_master.adapter;

import android.databinding.ViewDataBinding;

import com.lfp.lfp_databind_master.R;
import com.lfp.lfp_databind_master.databinding.ItemHomeTitleBinding;
import com.lfp.lfp_databind_master.xml.bean.ContentBean;
import com.lfp.lfp_databind_recycleview_library.base.ItemViewDelegate;
import com.lfp.lfp_databind_recycleview_library.base.LfpViewHolder;

public class HomeTitleAdapter implements ItemViewDelegate<ViewDataBinding, ContentBean> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_home_title;
    }

    @Override
    public boolean isForViewType(ContentBean item, int position) {
        return Integer.valueOf(item.getType()) == 0;
    }

    @Override
    public void convert(LfpViewHolder viewHolder, ContentBean item, int position) {
        ItemHomeTitleBinding binding = (ItemHomeTitleBinding) viewHolder.getDataBinding();
        binding.setContentBean(item);
    }
}
