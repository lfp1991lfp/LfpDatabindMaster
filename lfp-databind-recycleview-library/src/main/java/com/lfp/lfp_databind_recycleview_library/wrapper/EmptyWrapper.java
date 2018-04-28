package com.lfp.lfp_databind_recycleview_library.wrapper;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lfp.lfp_databind_recycleview_library.base.LfpViewHolder;
import com.lfp.lfp_databind_recycleview_library.utils.WrapperUtils;


/**
 * Created by lfp on 2017/4/25.
 * 空的包装类
 */
public class EmptyWrapper<DB extends ViewDataBinding, T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_EMPTY = Integer.MAX_VALUE - 1;

    protected RecyclerView.Adapter innerAdapter;
    private int emptyId;
    private View emptyView;

    public EmptyWrapper(RecyclerView.Adapter adapter) {
        innerAdapter = adapter;
    }

    protected boolean isEmpty() {
        return (emptyView != null || emptyId != 0) && innerAdapter.getItemCount() == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isEmpty()) {
            LfpViewHolder holder;
            DB dataViewBinding;
            if (emptyView != null) {
                dataViewBinding = DataBindingUtil.bind(emptyView);
            } else if (emptyId != 0) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                dataViewBinding = DataBindingUtil.inflate(inflater, emptyId, parent, false);
            } else {
                throw new IllegalStateException(
                        "请设置空视图界面的资源文件");
            }
            holder = LfpViewHolder.createViewHolder(dataViewBinding);
            initView(holder.getDataBinding().getRoot());
            holder.getDataBinding().getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView(v);
                }
            });
            return holder;
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    public void onClickView(View v) {

    }

    public void initView(View v) {

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(
                innerAdapter, recyclerView, (gridLayoutManager, oldLookup, position) -> {
                    if (isEmpty()) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (oldLookup != null) {
                        return oldLookup.getSpanSize(position);
                    }
                    return 1;
                });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isEmpty()) {
            WrapperUtils.setFullSpan(holder);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isEmpty()) {
            return ITEM_TYPE_EMPTY;
        }
        return innerAdapter.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isEmpty()) {
            return;
        }
        innerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        if (isEmpty()) return 1;
        return innerAdapter.getItemCount();
    }

    public void emptyResId(int emptyId) {
        this.emptyId = emptyId;
    }

    public void emptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
