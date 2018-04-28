package com.lfp.lfp_databind_recycleview_library;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lfp.lfp_databind_recycleview_library.base.ItemViewDelegate;
import com.lfp.lfp_databind_recycleview_library.base.ItemViewDelegateManager;
import com.lfp.lfp_databind_recycleview_library.base.LfpViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfp on 2017/4/25.
 * 多视图适配器
 */

public abstract class MultiItemTypeAdapter<DB extends ViewDataBinding, T> extends RecyclerView.Adapter<LfpViewHolder> {

    protected final Context context;
    private List<T> dataList;
    private ItemViewDelegateManager<DB, T> itemViewDelegateManager;
    private OnItemClickListener onItemClickListener;

    public MultiItemTypeAdapter(Context context, List<T> dataList) {
        this.context = context;
        this.dataList = dataList != null ? dataList : new ArrayList<T>();
        this.itemViewDelegateManager = new ItemViewDelegateManager<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (!useItemViewDelegateManager()) {
            return super.getItemViewType(position);
        }
        return itemViewDelegateManager.getItemViewType(dataList.get(position), position);
    }


    @Override
    public LfpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = itemViewDelegateManager.getItemViewDelegate(viewType);
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        DB dataViewBinding = DataBindingUtil.inflate(inflater, layoutId, parent, false);

        LfpViewHolder holder = LfpViewHolder.createViewHolder(dataViewBinding);
        onViewHolderCreated(parent, holder);
        setListener(holder, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(LfpViewHolder holder, int position) {
        convert(holder, dataList.get(position));
    }

    @Override
    public void onBindViewHolder(LfpViewHolder holder, int position, List<Object> payloads) {
        onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 视图创建过程，需要对某个View操作时，可以复写该方法.
     *
     * @param parent 父类容器
     * @param holder 绑定的holder
     */
    public void onViewHolderCreated(ViewGroup parent, LfpViewHolder holder) {

    }

    public void convert(LfpViewHolder holder, T item) {
        itemViewDelegateManager.convert(holder, item, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final LfpViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getDataBinding().getRoot().setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                if (position == -1) {
                    return;
                }
                onItemClickListener.onItemClick(v, viewHolder, position);
            }
        });

        viewHolder.getDataBinding().getRoot().setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                return position != -1 && onItemClickListener.onItemLongClick(v, viewHolder, position);
            }
            return false;
        });
    }


    /**
     * 获取数据集.
     *
     * @return 数据集的集合
     */
    public List<T> getDataList() {
        return dataList;
    }

    private void setDataList(List<T> newDataList) {
        this.dataList.clear();
        this.dataList.addAll(newDataList);
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<DB, T> itemViewDelegate) {
        itemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<DB, T>
            itemViewDelegate) {
        itemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return itemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public void setDateResult(List<T> newList) {
        DiffUtil.DiffResult diffResult = getDiffResult(newList);
        refresh(diffResult, newList);
    }

    /**
     * 提供数据差异性.
     *
     * @param newList 新数据源
     * @return 数据差异
     */
    public DiffUtil.DiffResult getDiffResult(List<T> newList) {
        return DiffUtil.calculateDiff(new DataCallBack(dataList, newList));
    }

    /**
     * 刷新数据，并重置之前的数据.
     *
     * @param diffResult 数据差异
     * @param newList    新数据
     */
    public void refresh(DiffUtil.DiffResult diffResult, List<T> newList) {
        diffResult.dispatchUpdatesTo(this);
        setDataList(newList);
    }

    /**
     * 局部刷新
     *
     * @param oldItemPosition 旧的位置
     * @param newItemPosition 新的位置
     * @return
     */
    protected T getChangePayload(
            List<T> oldList, int oldItemPosition, List<T> newList, int newItemPosition) {
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 比较唯一吗相同，一般是ID.
     *
     * @param item1 value1
     * @param item2 value2
     * @return true表示一样，否则不是不一样
     */
    protected abstract boolean areItemsTheSame(T item1, T item2);

    /**
     * 若id一样，则比较下一个内容
     *
     * @param item1 value1
     * @param item2 value2
     * @return true表示一样，否则不是不一样
     */
    protected abstract boolean areContentsTheSame(T item1, T item2);

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    /**
     * Simple implementation of the {@link OnItemClickListener} interface with stub
     * implementations of each method. Extend this if you do not intend to override
     * every method of {@link OnItemClickListener}.
     */
    public static class SimpleOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
            return false;
        }
    }

    private class DataCallBack extends DiffUtil.Callback {

        private List<T> oldDataList;
        private List<T> newDataList;

        public DataCallBack(List<T> oldDataList, List<T> newDataList) {
            this.oldDataList = oldDataList;
            this.newDataList = newDataList;
        }

        @Override
        public int getOldListSize() {
            return oldDataList.size();
        }

        @Override
        public int getNewListSize() {
            return newDataList.size();
        }

        /**
         * 比较两个集合中旧位置和新位置的值是否一致.
         *
         * @param oldItemPosition 旧位置
         * @param newItemPosition 新位置
         * @return true则执行areContentsTheSame方法
         */
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return MultiItemTypeAdapter.this.areItemsTheSame(oldDataList.get(oldItemPosition),
                    newDataList.get(newItemPosition));
        }

        /**
         * This method is called only if {@link #areItemsTheSame(int, int)} returns
         * {@code true} for these items.
         */
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return MultiItemTypeAdapter.this.areContentsTheSame(oldDataList.get(oldItemPosition),
                    newDataList.get(newItemPosition));
        }

        /**
         * 局部刷新.
         *
         * @param oldItemPosition 旧位置
         * @param newItemPosition 新位置
         * @return 需要刷新的item
         */
        @Nullable
        @Override
        public T getChangePayload(int oldItemPosition, int newItemPosition) {
            return MultiItemTypeAdapter.this.getChangePayload(oldDataList, oldItemPosition,
                    newDataList, newItemPosition);
        }
    }
}
