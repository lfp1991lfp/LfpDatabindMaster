package com.lfp.lfp_databind_recycleview_library;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;

import com.lfp.lfp_databind_recycleview_library.base.ItemViewDelegate;
import com.lfp.lfp_databind_recycleview_library.base.LfpViewHolder;

import java.util.List;

/**
 * Created by lfp on 2017/4/25.
 * 通用适配器(单布局)
 */
public abstract class CommonAdapter<DB extends ViewDataBinding, T> extends MultiItemTypeAdapter<DB, T> {
    protected LayoutInflater inflater;

    public CommonAdapter(final Context context, List<T> dataList) {
        super(context, dataList);
        inflater = LayoutInflater.from(context);

        addItemViewDelegate(new ItemViewDelegate<DB, T>() {
            @Override
            public int getItemViewLayoutId() {
                return getLayoutId();
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(LfpViewHolder holder, T item, int position) {
                CommonAdapter.this.convert(holder, item, position);
            }
        });
    }

    /**
     * 用于数据比较是否一致，确定是否局部刷新，
     * 若启用局部刷新，则需要复写两个方法.
     *
     * @param item1 value1 数据1
     * @param item2 value2 数据2
     * @return true表示唯一标识相等，true，继续走areContentsTheSame, 默认是全部刷新
     */
    @Override
    protected boolean areItemsTheSame(T item1, T item2) {
        return false;
    }

    @Override
    protected boolean areContentsTheSame(T item1, T item2) {
        return false;
    }

    /**
     * 数据转换.
     *
     * @param holder   绑定holder
     * @param item     对象
     * @param position 位置
     */
    protected abstract void convert(LfpViewHolder holder, T item, int position);

    /**
     * 获取布局ID
     *
     * @return 布局ID
     */
    protected abstract int getLayoutId();
}
