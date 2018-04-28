package com.lfp.lfp_databind_recycleview_library.wrapper;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lfp.lfp_databind_recycleview_library.base.LfpViewHolder;
import com.lfp.lfp_databind_recycleview_library.utils.WrapperUtils;

/**
 * Created by lfp on 2017/4/25.
 * 加载更多包装类,自动滑到最后一个加载更多
 */
@SuppressWarnings("all")
@Deprecated
public class LoadMoreWrapper<DB extends ViewDataBinding, T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;

    private RecyclerView.Adapter innerAdapter;
    private View loadMoreView;
    private int loadMoreLayoutId;
    private OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreWrapper(RecyclerView.Adapter adapter) {
        innerAdapter = adapter;
    }

    private boolean hasLoadMore() {
        return loadMoreView != null || loadMoreLayoutId != 0;
    }

    private boolean isShowLoadMore(int position) {
        return hasLoadMore() && (position >= innerAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMore(position)) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return innerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DB dataViewBinding;
        if (viewType == ITEM_TYPE_LOAD_MORE) {
            LfpViewHolder holder;
            if (loadMoreView != null) {
                dataViewBinding = DataBindingUtil.bind(loadMoreView);
                holder = LfpViewHolder.createViewHolder(dataViewBinding);
            } else {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                dataViewBinding = DataBindingUtil.inflate(inflater, loadMoreLayoutId, parent, false);
                holder = LfpViewHolder.createViewHolder(dataViewBinding);
            }
            return holder;
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMoreRequested();
            }
            return;
        }
        innerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(innerAdapter, recyclerView, (layoutManager, oldLookup, position) -> {
            if (isShowLoadMore(position)) {
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

        if (isShowLoadMore(holder.getLayoutPosition())) {
            setFullSpan(holder);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

            p.setFullSpan(true);
        }
    }

    @Override
    public int getItemCount() {
        return innerAdapter.getItemCount() + (hasLoadMore() ? 1 : 0);
    }

    public LoadMoreWrapper setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
        return this;
    }

    public LoadMoreWrapper setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
        return this;
    }

    public LoadMoreWrapper setLoadMoreView(int layoutId) {
        loadMoreLayoutId = layoutId;
        return this;
    }

    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }
}
