package com.lfp.lfp_databind_master.adapter;

import android.databinding.ViewDataBinding;

import com.lfp.lfp_databind_master.R;
import com.lfp.lfp_databind_master.databinding.ItemHomeBinding;
import com.lfp.lfp_databind_master.xml.bean.ContentBean;
import com.lfp.lfp_databind_recycleview_library.base.ItemViewDelegate;
import com.lfp.lfp_databind_recycleview_library.base.LfpViewHolder;


/**
 * 内容适配器
 */
public class ContentAdapter implements ItemViewDelegate<ViewDataBinding, ContentBean> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_home;
    }

    @Override
    public boolean isForViewType(ContentBean item, int position) {
        return Integer.parseInt(item.getType()) == 1;
    }

    @Override
    public void convert(LfpViewHolder viewHolder, ContentBean item, int position) {
        ItemHomeBinding binding = (ItemHomeBinding) viewHolder.getDataBinding();
        binding.setContentBean(item);
    }
}
