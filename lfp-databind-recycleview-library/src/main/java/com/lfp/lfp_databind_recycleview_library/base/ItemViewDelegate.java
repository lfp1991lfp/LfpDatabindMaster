package com.lfp.lfp_databind_recycleview_library.base;

import android.databinding.ViewDataBinding;

/**
 * Created by lfp on 2017/4/25.
 * 视图view的代理
 */

public interface ItemViewDelegate<DB extends ViewDataBinding, T> {

  int getItemViewLayoutId();

  boolean isForViewType(T item, int position);

  void convert(LfpViewHolder viewHolder, T item, int position);
}
