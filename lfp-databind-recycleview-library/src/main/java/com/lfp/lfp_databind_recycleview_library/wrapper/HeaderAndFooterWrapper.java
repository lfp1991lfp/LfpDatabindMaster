package com.lfp.lfp_databind_recycleview_library.wrapper;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lfp.lfp_databind_recycleview_library.base.LfpViewHolder;
import com.lfp.lfp_databind_recycleview_library.utils.WrapperUtils;


/**
 * Created by lfp on 2017/4/25.
 * 头部和底部包装类
 */
@SuppressWarnings("all")
public class HeaderAndFooterWrapper<DB extends ViewDataBinding, T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> headerViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> footViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter innerAdapter;

    public HeaderAndFooterWrapper(RecyclerView.Adapter adapter) {
        innerAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DB dataViewBinding;
        if (headerViews.get(viewType) != null) {
            dataViewBinding = DataBindingUtil.bind(headerViews.get(viewType));
            return LfpViewHolder.createViewHolder(dataViewBinding);

        } else if (footViews.get(viewType) != null) {
            dataViewBinding = DataBindingUtil.bind(footViews.get(viewType));
            return LfpViewHolder.createViewHolder(dataViewBinding);
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return headerViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return footViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return innerAdapter.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount() {
        return innerAdapter.getItemCount();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        innerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(innerAdapter, recyclerView, (layoutManager, oldLookup, position) -> {
            int viewType = getItemViewType(position);
            if (headerViews.get(viewType) != null) {
                return layoutManager.getSpanCount();
            } else if (footViews.get(viewType) != null) {
                return layoutManager.getSpanCount();
            }
            if (oldLookup != null) {
                return oldLookup.getSpanSize(position);
            }
            return 1;
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        innerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }


    public void addHeaderView(View view) {
        headerViews.put(headerViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view) {
        footViews.put(footViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount() {
        return headerViews.size();
    }

    public int getFootersCount() {
        return footViews.size();
    }


}
