package com.lfp.lfp_databind_recycleview_library.base;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by lfp on 2017/4/25.
 * ViewHolder继承RecyclerView.Holder
 */

@SuppressWarnings({"unchecked", "unused"})
public final class LfpViewHolder extends RecyclerView.ViewHolder {

    final ViewDataBinding dataBinding;

    /**
     * 初始化构造器，实现数据绑定.
     *
     * @param dataBinding 数据绑定
     */
    private LfpViewHolder(final ViewDataBinding dataBinding) {
        super(dataBinding.getRoot());
        this.dataBinding = dataBinding;
    }

    /**
     * 绑定View视图.
     *
     * @param dataBinding 数据绑定
     * @return 绑定holder
     */
    public static LfpViewHolder createViewHolder(final ViewDataBinding dataBinding) {
        return new LfpViewHolder(dataBinding);
    }

    public ViewDataBinding getDataBinding() {
        return dataBinding;
    }
}
