package com.lfp.lfp_databind_recycleview_library.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

/**
 * Created by lfp on 2017/4/25.
 * 包装类的操作类
 */
public class WrapperUtils {
  /**
   * 包装类适配器工具类.
   *
   * @param innerAdapter 内部适配器
   * @param recyclerView 布局
   * @param callback     回调
   */
  public static void onAttachedToRecyclerView(RecyclerView.Adapter innerAdapter, RecyclerView
      recyclerView, final SpanSizeCallback callback) {
    innerAdapter.onAttachedToRecyclerView(recyclerView);

    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    if (layoutManager instanceof GridLayoutManager) {
      final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
      final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

      gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
          return callback.getSpanSize(gridLayoutManager, spanSizeLookup, position);
        }
      });
      gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
    }
  }

  /**
   * 根据布局选择，把底部铺满.
   *
   * @param holder view控制器
   */
  public static void setFullSpan(RecyclerView.ViewHolder holder) {
    ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

    if (lp != null
        && lp instanceof StaggeredGridLayoutManager.LayoutParams) {

      StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

      p.setFullSpan(true);
    }
  }

  public interface SpanSizeCallback {
    int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup
            oldLookup, int position);
  }
}
