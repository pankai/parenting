package com.palmcel.parenting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Square image view. The height is always the same as width.
 */
public class SquareForWidthImageView extends ImageView {
    public SquareForWidthImageView(Context context) {
        super(context);
    }

    public SquareForWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareForWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getMeasuredWidth();
        setMeasuredDimension(size, size);
    }
}
