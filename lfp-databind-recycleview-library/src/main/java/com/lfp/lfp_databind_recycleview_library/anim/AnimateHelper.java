package com.lfp.lfp_databind_recycleview_library.anim;

import android.support.annotation.IntDef;
import android.view.animation.OvershootInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lfp on 2017/4/27.
 * 动画选择控制器
 */

public final class AnimateHelper {

  public static final int SCALE_RIGHT = 0x01;
  public static final int SCALE_BOTTOM = 0x02;
  public static final int SCALE_LEFT = 0x03;

  public static BaseItemAnimator animation(@ItemAnimation int animator, float level) {
    switch (animator) {
      case SCALE_LEFT:
        return new ScaleInRightAnimator(new OvershootInterpolator(level));
      case SCALE_BOTTOM:
        return new ScaleInBottomAnimator(new OvershootInterpolator(level));
      default:
        return new ScaleInRightAnimator(new OvershootInterpolator(level));
    }
  }

  @IntDef({SCALE_RIGHT, SCALE_BOTTOM, SCALE_LEFT})
  @Retention(RetentionPolicy.SOURCE)
  public @interface ItemAnimation {
  }
}
