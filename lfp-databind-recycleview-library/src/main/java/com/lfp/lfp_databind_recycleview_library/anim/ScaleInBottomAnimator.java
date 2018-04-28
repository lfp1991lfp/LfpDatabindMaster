package com.lfp.lfp_databind_recycleview_library.anim;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

/**
 * Created by lfp on 2017/4/27.
 * 底部向上弹出
 */

public class ScaleInBottomAnimator extends BaseItemAnimator {

  public ScaleInBottomAnimator() {
  }

  public ScaleInBottomAnimator(Interpolator interpolator) {
    this.interpolator = interpolator;
  }

  @Override
  protected void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
    holder.itemView.setPivotY(holder.itemView.getHeight());
  }

  @Override
  protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .scaleX(0)
        .scaleY(0)
        .setDuration(getRemoveDuration())
        .setInterpolator(interpolator)
        .setListener(new DefaultRemoveVpaListener(holder))
        .start();
  }

  @Override
  protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
    holder.itemView.setPivotY(holder.itemView.getHeight());
    ViewCompat.setScaleX(holder.itemView, 0);
    ViewCompat.setScaleY(holder.itemView, 0);
  }

  @Override
  protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
    ViewCompat.animate(holder.itemView)
        .scaleX(1)
        .scaleY(1)
        .setDuration(getAddDuration())
        .setInterpolator(interpolator)
        .setListener(new DefaultAddVpaListener(holder))
        .start();
  }
}
