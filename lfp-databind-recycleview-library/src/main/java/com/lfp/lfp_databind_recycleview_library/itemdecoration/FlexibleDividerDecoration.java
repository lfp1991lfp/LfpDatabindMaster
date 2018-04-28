package com.lfp.lfp_databind_recycleview_library.itemdecoration;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lfp on 2017/4/27.
 * recyclerView分割线管理
 */

@SuppressWarnings("all")
public abstract class FlexibleDividerDecoration extends RecyclerView.ItemDecoration {

  private static final int DEFAULT_SIZE = 2;
  private static final int[] ATTRS = new int[]{
      android.R.attr.listDivider
  };
  protected DividerType dividerType = DividerType.DRAWABLE;
  protected VisibilityProvider visibilityProvider;
  protected PaintProvider paintProvider;
  protected ColorProvider colorProvider;
  protected DrawableProvider drawableProvider;
  protected SizeProvider sizeProvider;
  protected boolean showLastDivider;
  protected boolean positionInsideItem;
  private Paint paint;

  protected FlexibleDividerDecoration(Builder builder) {
    if (builder.paintProvider != null) {
      dividerType = DividerType.PAINT;
      paintProvider = builder.paintProvider;
    } else if (builder.colorProvider != null) {
      dividerType = DividerType.COLOR;
      colorProvider = builder.colorProvider;
      paint = new Paint();
      setSizeProvider(builder);
    } else {
      dividerType = DividerType.DRAWABLE;
      if (builder.drawableProvider == null) {
        TypedArray a = builder.context.obtainStyledAttributes(ATTRS);
        final Drawable divider = a.getDrawable(0);
        a.recycle();
        drawableProvider = (position, parent) -> divider;
      } else {
        drawableProvider = builder.drawableProvider;
      }
      sizeProvider = builder.sizeProvider;
    }

    visibilityProvider = builder.visibilityProvider;
    showLastDivider = builder.mShowLastDivider;
    positionInsideItem = builder.mPositionInsideItem;
  }

  private void setSizeProvider(Builder builder) {
    sizeProvider = builder.sizeProvider;
    if (sizeProvider == null) {
      sizeProvider = (position, parent) -> DEFAULT_SIZE;
    }
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    RecyclerView.Adapter adapter = parent.getAdapter();
    if (adapter == null) {
      return;
    }

    int itemCount = adapter.getItemCount();
    int lastDividerOffset = getLastDividerOffset(parent);
    int validChildCount = parent.getChildCount();
    int lastChildPosition = -1;
    for (int i = 0; i < validChildCount; i++) {
      View child = parent.getChildAt(i);
      int childPosition = parent.getChildAdapterPosition(child);

      if (childPosition < lastChildPosition) {
        // Avoid remaining divider when animation starts
        continue;
      }
      lastChildPosition = childPosition;

      if (!showLastDivider && childPosition >= itemCount - lastDividerOffset) {
        // Don't draw divider for last line if showLastDivider = false
        continue;
      }

      if (wasDividerAlreadyDrawn(childPosition, parent)) {
        // No need to draw divider again as it was drawn already by previous column
        continue;
      }

      int groupIndex = getGroupIndex(childPosition, parent);
      if (visibilityProvider.shouldHideDivider(groupIndex, parent)) {
        continue;
      }

      Rect bounds = getDividerBound(groupIndex, parent, child);
      switch (dividerType) {
        case DRAWABLE:
          Drawable drawable = drawableProvider.drawableProvider(groupIndex, parent);
          drawable.setBounds(bounds);
          drawable.draw(c);
          break;
        case PAINT:
          paint = paintProvider.dividerPaint(groupIndex, parent);
          c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
          break;
        case COLOR:
          paint.setColor(colorProvider.dividerColor(groupIndex, parent));
          paint.setStrokeWidth(sizeProvider.dividerSize(groupIndex, parent));
          c.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
          break;
      }
    }
  }

  @Override
  public void getItemOffsets(Rect rect, View v, RecyclerView parent, RecyclerView.State state) {
    int position = parent.getChildAdapterPosition(v);
    int itemCount = parent.getAdapter().getItemCount();
    int lastDividerOffset = getLastDividerOffset(parent);
    if (!showLastDivider && position >= itemCount - lastDividerOffset) {
      // Don't set item offset for last line if showLastDivider = false
      return;
    }

    int groupIndex = getGroupIndex(position, parent);
    if (visibilityProvider.shouldHideDivider(groupIndex, parent)) {
      return;
    }

    setItemOffsets(rect, groupIndex, parent);
  }

  /**
   * Check if recyclerview is reverse layout
   *
   * @param parent RecyclerView
   * @return true if recyclerview is reverse layout
   */
  protected boolean isReverseLayout(RecyclerView parent) {
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    if (layoutManager instanceof LinearLayoutManager) {
      return ((LinearLayoutManager) layoutManager).getReverseLayout();
    } else {
      return false;
    }
  }

  /**
   * In the case showLastDivider = false,
   * Returns offset for how many views we don't have to draw a divider for,
   * for LinearLayoutManager it is as simple as not drawing the last child divider,
   * but for a GridLayoutManager it needs to take the span count for the last items into account
   * until we use the span count configured for the grid.
   *
   * @param parent RecyclerView
   * @return offset for how many views we don't have to draw a divider or 1 if its a
   * LinearLayoutManager
   */
  private int getLastDividerOffset(RecyclerView parent) {
    if (parent.getLayoutManager() instanceof GridLayoutManager) {
      GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
      GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
      int spanCount = layoutManager.getSpanCount();
      int itemCount = parent.getAdapter().getItemCount();
      for (int i = itemCount - 1; i >= 0; i--) {
        if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
          return itemCount - i;
        }
      }
    }

    return 1;
  }

  /**
   * Determines whether divider was already drawn for the row the item is in,
   * effectively only makes sense for a grid
   *
   * @param position current view position to draw divider
   * @param parent   RecyclerView
   * @return true if the divider can be skipped as it is in the same row as the previous one.
   */
  private boolean wasDividerAlreadyDrawn(int position, RecyclerView parent) {
    if (parent.getLayoutManager() instanceof GridLayoutManager) {
      GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
      GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
      int spanCount = layoutManager.getSpanCount();
      return spanSizeLookup.getSpanIndex(position, spanCount) > 0;
    }

    return false;
  }

  /**
   * Returns a group index for GridLayoutManager.
   * for LinearLayoutManager, always returns position.
   *
   * @param position current view position to draw divider
   * @param parent   RecyclerView
   * @return group index of items
   */
  private int getGroupIndex(int position, RecyclerView parent) {
    if (parent.getLayoutManager() instanceof GridLayoutManager) {
      GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
      GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
      int spanCount = layoutManager.getSpanCount();
      return spanSizeLookup.getSpanGroupIndex(position, spanCount);
    }

    return position;
  }

  protected abstract Rect getDividerBound(int position, RecyclerView parent, View child);

  protected abstract void setItemOffsets(Rect outRect, int position, RecyclerView parent);

  protected enum DividerType {
    DRAWABLE, PAINT, COLOR
  }

  /**
   * Interface for controlling divider visibility
   */
  public interface VisibilityProvider {

    /**
     * Returns true if divider should be hidden.
     *
     * @param position Divider position (or group index for GridLayoutManager)
     * @param parent   RecyclerView
     * @return True if the divider at position should be hidden
     */
    boolean shouldHideDivider(int position, RecyclerView parent);
  }

  /**
   * Interface for controlling paint instance for divider drawing
   */
  public interface PaintProvider {

    /**
     * Returns {@link Paint} for divider
     *
     * @param position Divider position (or group index for GridLayoutManager)
     * @param parent   RecyclerView
     * @return Paint instance
     */
    Paint dividerPaint(int position, RecyclerView parent);
  }

  /**
   * Interface for controlling divider color
   */
  public interface ColorProvider {

    /**
     * Returns {@link android.graphics.Color} value of divider
     *
     * @param position Divider position (or group index for GridLayoutManager)
     * @param parent   RecyclerView
     * @return Color value
     */
    int dividerColor(int position, RecyclerView parent);
  }

  /**
   * Interface for controlling drawable object for divider drawing
   */
  public interface DrawableProvider {

    /**
     * Returns drawable instance for divider
     *
     * @param position Divider position (or group index for GridLayoutManager)
     * @param parent   RecyclerView
     * @return Drawable instance
     */
    Drawable drawableProvider(int position, RecyclerView parent);
  }

  /**
   * Interface for controlling divider size
   */
  public interface SizeProvider {

    /**
     * Returns size value of divider.
     * Height for horizontal divider, width for vertical divider
     *
     * @param position Divider position (or group index for GridLayoutManager)
     * @param parent   RecyclerView
     * @return Size of divider
     */
    int dividerSize(int position, RecyclerView parent);
  }

  public static class Builder<T extends Builder> {

    protected Resources resources;
    private Context context;
    private PaintProvider paintProvider;
    private ColorProvider colorProvider;
    private DrawableProvider drawableProvider;
    private SizeProvider sizeProvider;
    private VisibilityProvider visibilityProvider = (position, parent) -> false;
    private boolean mShowLastDivider = false;
    private boolean mPositionInsideItem = false;

    public Builder(Context context) {
      this.context = context;
      resources = context.getResources();
    }

    public T paint(final Paint paint) {
      return paintProvider((position, parent) -> paint);
    }

    public T paintProvider(PaintProvider provider) {
      paintProvider = provider;
      return (T) this;
    }

    public T color(final int color) {
      return colorProvider((position, parent) -> color);
    }

    public T colorResId(@ColorRes int colorId) {
      return color(ContextCompat.getColor(context, colorId));
    }

    public T colorProvider(ColorProvider provider) {
      colorProvider = provider;
      return (T) this;
    }

    public T drawable(@DrawableRes int id) {
      return drawable(ContextCompat.getDrawable(context, id));
    }

    public T drawable(final Drawable drawable) {
      return drawableProvider((position, parent) -> drawable);
    }

    public T drawableProvider(DrawableProvider provider) {
      drawableProvider = provider;
      return (T) this;
    }

    public T size(final int size) {
      return sizeProvider((position, parent) -> size);
    }

    public T sizeResId(@DimenRes int sizeId) {
      return size(resources.getDimensionPixelSize(sizeId));
    }

    public T sizeProvider(SizeProvider provider) {
      sizeProvider = provider;
      return (T) this;
    }

    public T visibilityProvider(VisibilityProvider provider) {
      visibilityProvider = provider;
      return (T) this;
    }

    public T showLastDivider() {
      mShowLastDivider = true;
      return (T) this;
    }

    public T positionInsideItem(boolean positionInsideItem) {
      mPositionInsideItem = positionInsideItem;
      return (T) this;
    }

    protected void checkBuilderParams() {
      if (paintProvider != null) {
        if (colorProvider != null) {
          throw new IllegalArgumentException(
              "Use setColor method of Paint class to specify line color. Do not provider " +
                  "ColorProvider if you set PaintProvider.");
        }
        if (sizeProvider != null) {
          throw new IllegalArgumentException(
              "Use setStrokeWidth method of Paint class to specify line size. Do not provider " +
                  "SizeProvider if you set PaintProvider.");
        }
      }
    }
  }
}
